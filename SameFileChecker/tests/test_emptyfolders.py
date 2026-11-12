"""空文件夹扫描/删除测试。"""

from __future__ import annotations

import ctypes
import os
import sys
import tempfile
import threading
import unittest

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SRC = os.path.join(ROOT, "src")
if SRC not in sys.path:
    sys.path.insert(0, SRC)

from sfc.emptyfolders import (  # noqa: E402
    EmptyScanOptions,
    delete_empty_dirs,
    scan_empty_folders,
)


def _mkdir(path: str) -> str:
    os.makedirs(path, exist_ok=True)
    return path


def _write(path: str, text: str = "x") -> None:
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as handle:
        handle.write(text)


class EmptyFolderTests(unittest.TestCase):
    def setUp(self) -> None:
        self.tmp = tempfile.mkdtemp(prefix="sfc_empty_")
        self.root = os.path.join(self.tmp, "素材")
        os.makedirs(self.root)
        self.empty1 = _mkdir(os.path.join(self.root, "empty1"))
        self.tree = _mkdir(os.path.join(self.root, "tree"))
        _mkdir(os.path.join(self.root, "tree", "nested", "deep"))
        self.onlyempty = _mkdir(os.path.join(self.root, "onlyempty"))
        _mkdir(os.path.join(self.root, "onlyempty", "child"))
        _write(os.path.join(self.root, "mixed", "keep.txt"))
        self.mixed_empty = _mkdir(os.path.join(self.root, "mixed", "empty_child"))
        _mkdir(os.path.join(self.root, "excluded_parent", "node_modules", "empty"))
        self.deep = _mkdir(os.path.join(self.root, "deep"))
        _mkdir(os.path.join(self.root, "deep", "a", "b", "c"))
        self.hidden_parent = _mkdir(os.path.join(self.root, "hidden_parent"))
        self.hidden_child = _mkdir(os.path.join(self.root, "hidden_parent", "hidden_child"))
        ctypes.windll.kernel32.SetFileAttributesW(self.hidden_child, 0x2)

    def tearDown(self) -> None:
        import shutil

        shutil.rmtree(self.tmp, ignore_errors=True)

    def test_topmost_empty_folders(self) -> None:
        result = scan_empty_folders([self.root])
        self.assertFalse(result.cancelled)
        found = set(result.folders)
        self.assertIn(self.empty1, found)
        self.assertIn(self.tree, found)
        self.assertIn(self.onlyempty, found)
        self.assertIn(self.mixed_empty, found)
        self.assertIn(self.deep, found)
        self.assertNotIn(self.root, found)
        self.assertNotIn(os.path.join(self.root, "tree", "nested"), found)
        self.assertNotIn(os.path.join(self.root, "onlyempty", "child"), found)
        self.assertNotIn(os.path.join(self.root, "excluded_parent"), found)
        self.assertNotIn(self.hidden_parent, found)
        self.assertGreater(result.dirs_scanned, 5)

    def test_include_hidden(self) -> None:
        result = scan_empty_folders([self.root], EmptyScanOptions(include_hidden=True))
        found = set(result.folders)
        self.assertIn(self.hidden_parent, found)
        # hidden_parent 是最外层，hidden_child 不重复列出
        self.assertNotIn(os.path.join(self.root, "hidden_parent", "hidden_child"), found)

    def test_cancel(self) -> None:
        event = threading.Event()
        event.set()
        result = scan_empty_folders([self.root], cancel=event)
        self.assertTrue(result.cancelled)

    def test_missing_root(self) -> None:
        result = scan_empty_folders([os.path.join(self.tmp, "不存在")])
        self.assertEqual(result.folders, [])
        self.assertTrue(result.errors)

    def test_direct_delete(self) -> None:
        result = scan_empty_folders([self.root])
        targets = [path for path in result.folders if path in (self.empty1, self.tree, self.onlyempty)]
        self.assertEqual(len(targets), 3)
        outcome = delete_empty_dirs(targets, mode="direct")
        self.assertEqual(len(outcome.removed), 3)
        self.assertEqual(outcome.failures, [])
        for path in targets:
            self.assertFalse(os.path.exists(path))
        self.assertTrue(os.path.exists(os.path.join(self.root, "mixed", "keep.txt")))

    def test_refuses_non_empty_folder(self) -> None:
        outcome = delete_empty_dirs([os.path.join(self.root, "mixed")], mode="direct")
        self.assertEqual(outcome.removed, [])
        self.assertTrue(outcome.failures)
        self.assertTrue(os.path.exists(os.path.join(self.root, "mixed", "keep.txt")))

    def test_refuses_folder_that_gained_file(self) -> None:
        outcome = delete_empty_dirs([self.deep], mode="direct")
        self.assertEqual(len(outcome.removed), 1)
        # 重新造一个空的再放入文件，模拟扫描后发生变化
        os.makedirs(self.deep, exist_ok=True)
        _write(os.path.join(self.deep, "new.txt"))
        outcome = delete_empty_dirs([self.deep], mode="direct")
        self.assertEqual(outcome.removed, [])
        self.assertTrue(os.path.exists(os.path.join(self.deep, "new.txt")))


if __name__ == "__main__":
    unittest.main(verbosity=2)
