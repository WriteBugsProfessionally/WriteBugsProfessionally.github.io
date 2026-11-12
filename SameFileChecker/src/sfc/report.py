"""扫描结果导出：CSV（Excel 友好）与 JSON。"""

from __future__ import annotations

import csv
import json
from datetime import datetime

from . import __version__
from .models import ScanResult
from .util import format_mtime, human_size, norm_key


def write_csv(path: str, result: ScanResult, selected: set[str] | None = None, policy: str = "") -> int:
    selected_keys = {norm_key(p) for p in (selected or set())}
    rows = 0
    with open(path, "w", encoding="utf-8-sig", newline="") as handle:
        writer = csv.writer(handle)
        writer.writerow(
            [
                "组号", "文件名", "完整路径", "大小(字节)", "大小", "修改时间",
                "类型", "同组文件数", "组可释放(字节)", "完整哈希", "建议",
            ]
        )
        for index, group in enumerate(result.groups, start=1):
            for entry in group.files:
                writer.writerow(
                    [
                        index,
                        entry.name,
                        entry.path,
                        entry.size,
                        human_size(entry.size),
                        format_mtime(entry.mtime_ns),
                        entry.kind,
                        group.count,
                        group.wasted,
                        group.digest.hex()[:32],
                        "建议删除" if norm_key(entry.path) in selected_keys else "保留",
                    ]
                )
                rows += 1

        writer.writerow([])
        stats = result.stats
        writer.writerow(["扫描统计"])
        writer.writerow(["扫描文件夹", "；".join(result.roots)])
        writer.writerow(["扫描时间", datetime.now().strftime("%Y-%m-%d %H:%M:%S")])
        writer.writerow(["枚举文件数", stats.files_seen])
        writer.writerow(["参与比对文件数", stats.candidates])
        writer.writerow(["重复组数", stats.groups])
        writer.writerow(["重复文件数", stats.duplicate_files])
        writer.writerow(["可释放空间(字节)", stats.wasted])
        writer.writerow(["可释放空间", human_size(stats.wasted)])
        writer.writerow(["读取数据量", human_size(stats.bytes_read)])
        writer.writerow(["用时(秒)", f"{stats.elapsed:.1f}"])
        writer.writerow(["保留策略", policy or "未指定"])
    return rows


def write_json(path: str, result: ScanResult, selected: set[str] | None = None, policy: str = "") -> None:
    selected_keys = {norm_key(p) for p in (selected or set())}
    stats = result.stats
    payload = {
        "app": "SameFileChecker",
        "version": __version__,
        "exported_at": datetime.now().isoformat(timespec="seconds"),
        "roots": result.roots,
        "policy": policy,
        "stats": {
            "files_seen": stats.files_seen,
            "dirs_seen": stats.dirs_seen,
            "candidates": stats.candidates,
            "hashed_files": stats.hashed_files,
            "bytes_read": stats.bytes_read,
            "elapsed": round(stats.elapsed, 3),
            "groups": stats.groups,
            "duplicate_files": stats.duplicate_files,
            "wasted": stats.wasted,
        },
        "groups": [
            {
                "index": index,
                "hash": group.digest.hex(),
                "size": group.file_size,
                "wasted": group.wasted,
                "files": [
                    {
                        "path": entry.path,
                        "name": entry.name,
                        "size": entry.size,
                        "mtime_ns": entry.mtime_ns,
                        "kind": entry.kind,
                        "selected_for_delete": norm_key(entry.path) in selected_keys,
                    }
                    for entry in group.files
                ],
            }
            for index, group in enumerate(result.groups, start=1)
        ],
        "errors": [{"path": e.path, "message": e.message} for e in result.errors],
    }
    with open(path, "w", encoding="utf-8") as handle:
        json.dump(payload, handle, ensure_ascii=False, indent=2)
