"""命令行模式：不启动 Qt，便于自动化、脚本和排障。

用法：
    python src/main.py --cli D:\\Photos --min-size 1KB
    python src/main.py --cli D:\\Photos --csv report.csv --json report.json
"""

from __future__ import annotations

import argparse
import os
import re
import sys
import time

from .cache import HashCache
from .models import ScanResult
from .scanner import ScanOptions, scan
from .util import format_elapsed, human_size
from .report import write_csv, write_json

_SIZE_RE = re.compile(r"^\s*(\d+(?:\.\d+)?)\s*(B|KB|MB|GB|TB)?\s*$", re.IGNORECASE)
_UNITS = {"B": 1, "KB": 1024, "MB": 1024 ** 2, "GB": 1024 ** 3, "TB": 1024 ** 4}


def parse_size(text: str) -> int:
    match = _SIZE_RE.match(text)
    if not match:
        raise argparse.ArgumentTypeError(f"无法识别的大小：{text}")
    value, unit = match.groups()
    return int(float(value) * _UNITS[(unit or "B").upper()])


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        prog="SameFileChecker --cli",
        description="按内容查找重复文件（命令行模式）",
    )
    parser.add_argument("roots", nargs="+", help="要扫描的文件夹，可以多个")
    parser.add_argument("--min-size", type=parse_size, default=1024, help="最小文件大小，如 1KB、10MB（默认 1KB）")
    parser.add_argument("--all-files", action="store_true", help="扫描所有类型，默认只扫图片/视频/音频")
    parser.add_argument("--exts", default="", help="只扫描这些扩展名，逗号分隔，如 jpg,png,mp4")
    parser.add_argument("--workers", type=int, default=4, help="并行读取线程数（机械硬盘建议 2-4）")
    parser.add_argument("--no-cache", action="store_true", help="不使用哈希缓存")
    parser.add_argument("--include-hidden", action="store_true", help="包含隐藏/系统文件")
    parser.add_argument("--csv", default="", help="导出 CSV 报告")
    parser.add_argument("--json", default="", help="导出 JSON 报告")
    parser.add_argument("--limit", type=int, default=50, help="终端最多打印多少组（默认 50）")
    parser.add_argument("--quiet", action="store_true", help="不打印进度")
    return parser


def _make_options(args: argparse.Namespace) -> ScanOptions:
    if args.all_files:
        exts = None
    elif args.exts.strip():
        exts = frozenset(e.strip().lower().lstrip(".") for e in args.exts.split(",") if e.strip())
    else:
        from .util import MEDIA_EXTS

        exts = MEDIA_EXTS
    return ScanOptions(
        min_size=args.min_size,
        exts=exts,
        workers=args.workers,
        include_hidden=args.include_hidden,
        use_cache=not args.no_cache,
    )


def _print_result(result: ScanResult, limit: int) -> None:
    stats = result.stats
    print()
    print(
        f"枚举 {stats.files_seen:,} 个文件，其中 {stats.candidates:,} 个大小相同参与比对；"
        f"读取 {human_size(stats.bytes_read)}，用时 {format_elapsed(stats.elapsed)}"
    )
    print(
        f"找到 {stats.groups:,} 组重复，{stats.duplicate_files:,} 个文件，"
        f"可释放 {human_size(stats.wasted)}"
    )
    if result.errors:
        print(f"有 {len(result.errors):,} 个文件/文件夹读取失败（不影响其余结果）")
    print()
    for index, group in enumerate(result.groups[:limit], start=1):
        print(
            f"[{index:>3}] {group.count} 个文件 · 每个 {human_size(group.file_size)} · "
            f"可释放 {human_size(group.wasted)}"
        )
        for entry in group.files:
            print(f"      {entry.path}")
        print()
    if len(result.groups) > limit:
        print(f"…… 还有 {len(result.groups) - limit} 组未显示，可用 --csv / --json 导出完整结果。")


def main(argv: list[str] | None = None) -> int:
    if sys.stdout is None:
        sys.stdout = open(os.devnull, "w", encoding="utf-8")
    if sys.stderr is None:
        sys.stderr = open(os.devnull, "w", encoding="utf-8")
    parser = build_parser()
    args = parser.parse_args(argv)
    options = _make_options(args)
    cache = None if args.no_cache else HashCache()

    state = {"label": "", "last": 0.0, "total": 0}

    def on_phase(_key: str, label: str, total_bytes: int, total_files: int) -> None:
        state["label"] = label
        state["total"] = total_files
        if not args.quiet and sys.stdout.isatty():
            print(f"\r{label}…".ljust(78), end="", flush=True)

    def on_progress(done: int, total: int, files_done: int, files_total: int, speed: float) -> None:
        if args.quiet or not sys.stdout.isatty():
            return
        now = time.monotonic()
        if now - state["last"] < 0.4:
            return
        state["last"] = now
        if total:
            pct = min(100.0, done * 100.0 / total)
            text = (
                f"{state['label']} {pct:5.1f}%  "
                f"{human_size(done)} / {human_size(total)}  {human_size(speed)}/s"
            )
        else:
            text = f"{state['label']}… 已发现 {files_done:,} 个文件"
        print(f"\r{text}".ljust(78), end="", flush=True)

    def on_error(path: str, message: str) -> None:
        if not args.quiet:
            print(f"\n[跳过] {path}：{message}", file=sys.stderr)

    result = scan(
        args.roots,
        options,
        cache=cache,
        on_phase=on_phase,
        on_progress=on_progress,
        on_error=on_error,
    )
    if cache is not None:
        cache.close()
    if not args.quiet and sys.stdout.isatty():
        print("\r" + " " * 78 + "\r", end="", flush=True)

    if result.cancelled:
        print("扫描已取消。", file=sys.stderr)
        return 130
    if not result.roots:
        print("没有可扫描的文件夹。", file=sys.stderr)
        return 2

    _print_result(result, max(1, args.limit))
    if args.csv:
        rows = write_csv(args.csv, result)
        print(f"已导出 CSV：{args.csv}（{rows:,} 行）")
    if args.json:
        write_json(args.json, result)
        print(f"已导出 JSON：{args.json}")
    return 0
