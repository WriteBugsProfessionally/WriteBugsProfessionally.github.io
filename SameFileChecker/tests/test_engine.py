"""核心引擎测试：造一批“内容相同、名字不同”的文件，验证分组、缓存、硬链接、取消。"""

from __future__ import annotations

import json
import os
import subprocess
import sys
import tempfile
import threading
import time
import unittest

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SRC = os.path.join(ROOT, "src")
if SRC not in sys.path:
    sys.path.insert(0, SRC)

from sfc.cache import HashCache  # noqa: E402
from sfc.fileops import move_to_quarantine  # noqa: E402
from sfc.report import write_csv, write_json  # noqa: E402
from sfc.scanner import ScanOptions, scan  # noqa: E402
from sfc.util import MEDIA_EXTS, human_size  # noqa: E402


def _write(path: str, data: bytes) -> str:
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "wb") as handle:
        handle.write(data)
    return path


class EngineTests(unittest.TestCase):
    def setUp(self) -> None:
        self.tmp = tempfile.mkdtemp(prefix="sfc_test_")
        self.root = os.path.join(self.tmp, "素材库")
        self.a = os.urandom(700_000)
        self.b = os.urandom(700_000)
        self.video = os.urandom(3_000_000)
        self.small = os.urandom(10_000)

        self.a1 = _write(os.path.join(self.root, "照片", "a.jpg"), self.a)
        self.a2 = _write(os.path.join(self.root, "照片", "a - 副本.jpg"), self.a)
        self.a3 = _write(os.path.join(self.root, "照片", "sub", "另一个名字.jpg"), self.a)
        self.hard = os.path.join(self.root, "照片", "a-hardlink.jpg")
        os.link(self.a1, self.hard)
        self.b1 = _write(os.path.join(self.root, "照片", "b.jpg"), self.b)

        self.v1 = _write(os.path.join(self.root, "视频", "clip.mp4"), self.video)
        self.v2 = _write(os.path.join(self.root, "视频", "clip-copy.mp4"), self.video)

        self.s1 = _write(os.path.join(self.root, "小文件", "x.png"), self.small)
        self.s2 = _write(os.path.join(self.root, "小文件", "y.png"), self.small)

        self.t1 = _write(os.path.join(self.root, "文档", "t1.txt"), b"hello world " * 500)
        self.t2 = _write(os.path.join(self.root, "文档", "t2.txt"), b"hello world " * 500)
        _write(os.path.join(self.root, "空文件.bin"), b"")

    def tearDown(self) -> None:
        import shutil

        from sfc.util import to_extended

        shutil.rmtree(self.tmp, ignore_errors=True)
        shutil.rmtree(to_extended(self.tmp), ignore_errors=True)

    def test_media_groups(self) -> None:
        result = scan([self.root], ScanOptions(exts=MEDIA_EXTS, workers=2))
        self.assertFalse(result.cancelled)
        self.assertEqual(result.stats.groups, 3, [g.files for g in result.groups])
        sizes = sorted(g.file_size for g in result.groups)
        self.assertEqual(sizes, [10_000, 700_000, 3_000_000])
        self.assertEqual(result.stats.duplicate_files, 3 + 2 + 2)  # 图片组有 3 个不同 inode
        self.assertEqual(
            result.stats.wasted, 2 * 700_000 + 3_000_000 + 10_000
        )
        photo = next(g for g in result.groups if g.file_size == 700_000)
        self.assertEqual(photo.count, 3)
        paths = {f.path for f in photo.files}
        self.assertEqual(len(paths & {self.a1, self.hard}), 1, "硬链接应被折叠")
        self.assertIn(self.a2, paths)
        self.assertIn(self.a3, paths)
        self.assertNotIn(self.b1, paths)

    def test_all_files_extension_filter(self) -> None:
        result = scan([self.root], ScanOptions(min_size=1, exts=None, workers=2))
        txt = [g for g in result.groups if g.files[0].ext == "txt"]
        self.assertEqual(len(txt), 1)
        self.assertEqual(txt[0].count, 2)
        self.assertEqual(result.stats.groups, 4)

    def test_cache_skips_reread(self) -> None:
        cache = HashCache(os.path.join(self.tmp, "cache.db"))
        try:
            first = scan([self.root], ScanOptions(exts=MEDIA_EXTS, workers=2), cache=cache)
            self.assertGreater(first.stats.bytes_read, 0)
            second = scan([self.root], ScanOptions(exts=MEDIA_EXTS, workers=2), cache=cache)
            self.assertEqual(second.stats.bytes_read, 0, "第二次扫描不应再读文件内容")
            self.assertEqual(len(second.groups), len(first.groups))
            self.assertEqual(second.stats.wasted, first.stats.wasted)
        finally:
            cache.close()

    def test_cancel(self) -> None:
        event = threading.Event()
        event.set()
        result = scan([self.root], ScanOptions(exts=MEDIA_EXTS), cancel=event)
        self.assertTrue(result.cancelled)

    def test_overlapping_roots(self) -> None:
        result = scan(
            [self.root, os.path.join(self.root, "照片")],
            ScanOptions(exts=MEDIA_EXTS, workers=2),
        )
        self.assertEqual(len(result.roots), 1)
        self.assertEqual(result.stats.groups, 3)

    def test_quarantine_and_reports(self) -> None:
        result = scan([self.root], ScanOptions(exts=MEDIA_EXTS, workers=2))
        selected = {self.a2, self.v2}
        quarantine = os.path.join(self.tmp, "quarantine")
        outcome = move_to_quarantine(sorted(selected), quarantine)
        self.assertEqual(len(outcome.moved), 2)
        self.assertEqual(outcome.failures, [])
        self.assertFalse(os.path.exists(self.a2))
        self.assertFalse(os.path.exists(self.v2))
        self.assertTrue(os.path.exists(self.a1))

        csv_path = os.path.join(self.tmp, "报告.csv")
        rows = write_csv(csv_path, result, selected, "保留最早修改")
        self.assertGreater(rows, 0)
        with open(csv_path, "rb") as handle:
            raw = handle.read()
        self.assertTrue(raw.startswith(b"\xef\xbb\xbf"), "CSV 需要 BOM 以兼容 Excel")
        self.assertIn("建议删除".encode("utf-8"), raw)

        json_path = os.path.join(self.tmp, "报告.json")
        write_json(json_path, result, selected, "保留最早修改")
        with open(json_path, encoding="utf-8") as handle:
            payload = json.load(handle)
        self.assertEqual(payload["stats"]["groups"], 3)
        self.assertEqual(len(payload["groups"]), 3)

    def test_cli(self) -> None:
        out = os.path.join(self.tmp, "cli.json")
        proc = subprocess.run(
            [
                sys.executable,
                os.path.join(SRC, "main.py"),
                "--cli",
                self.root,
                "--json",
                out,
                "--quiet",
                "--workers",
                "2",
            ],
            capture_output=True,
            text=True,
            encoding="utf-8",
            errors="replace",
            env={**os.environ, "PYTHONUTF8": "1", "PYTHONIOENCODING": "utf-8"},
            timeout=120,
        )
        self.assertEqual(proc.returncode, 0, proc.stderr + proc.stdout)
        with open(out, encoding="utf-8") as handle:
            payload = json.load(handle)
        self.assertEqual(payload["stats"]["groups"], 3)
        self.assertIn("可释放", proc.stdout)

    def test_full_hash_needed_when_samples_match(self) -> None:
        """头/中段/尾部都相同、中间有差异时，必须靠全文哈希区分。"""
        folder = os.path.join(self.tmp, "采样陷阱")
        os.makedirs(folder)
        size = 4 * 1024 * 1024
        data = bytearray(os.urandom(size))
        first = _write(os.path.join(folder, "one.bin"), bytes(data))
        data[1024 * 1024] ^= 0xFF
        _write(os.path.join(folder, "two.bin"), bytes(data))
        result = scan([folder], ScanOptions(min_size=1, exts=None, workers=2))
        self.assertEqual(result.stats.groups, 0, [g.files for g in result.groups])
        self.assertGreater(result.stats.bytes_read, 8 * 1024 * 1024)
        self.assertTrue(os.path.exists(first))

    def test_cache_invalidates_on_change(self) -> None:
        folder = os.path.join(self.tmp, "缓存失效")
        os.makedirs(folder)
        _write(os.path.join(folder, "a.jpg"), self.a)
        copy = _write(os.path.join(folder, "a - 副本.jpg"), self.a)
        cache = HashCache(os.path.join(self.tmp, "cache2.db"))
        try:
            first = scan([folder], ScanOptions(min_size=1, exts=None, workers=2), cache=cache)
            self.assertEqual(len(first.groups), 1)
            with open(copy, "r+b") as handle:
                handle.seek(1000)
                handle.write(b"\x00" * 64)
            os.utime(copy, ns=(time.time_ns() + 2_000_000, time.time_ns() + 2_000_000))
            second = scan([folder], ScanOptions(min_size=1, exts=None, workers=2), cache=cache)
            self.assertEqual(len(second.groups), 0)
            self.assertGreater(second.stats.bytes_read, 0)
        finally:
            cache.close()

    def test_long_path(self) -> None:
        """超过 260 字符的深层路径也要能扫描、读取。"""
        from sfc.util import to_extended

        parts = [self.tmp, "长路径测试"] + [f"目录{i}" + "x" * 12 for i in range(12)]
        folder = os.path.join(*parts)
        os.makedirs(to_extended(folder), exist_ok=True)
        data = os.urandom(600_000)
        paths = [
            os.path.join(folder, "很长的文件名" + "a" * 40 + ".jpg"),
            os.path.join(folder, "另一个名字" + "b" * 40 + ".jpg"),
        ]
        for path in paths:
            with open(to_extended(path), "wb") as handle:
                handle.write(data)
        self.assertGreater(len(paths[0]), 260)
        result = scan([self.tmp], ScanOptions(exts=frozenset({"jpg"}), workers=2))
        matching = [group for group in result.groups if group.file_size == len(data)]
        self.assertEqual(len(matching), 1, result.stats.files_seen)
        self.assertEqual(matching[0].count, 2)

    def test_human_size(self) -> None:
        self.assertEqual(human_size(512), "512 B")
        self.assertEqual(human_size(1024), "1 KB")
        self.assertEqual(human_size(1536), "1.5 KB")


if __name__ == "__main__":
    unittest.main(verbosity=2)
