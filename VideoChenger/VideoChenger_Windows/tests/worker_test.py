"""后台线程层测试：验证 ProcessWorker 的完成、进度解析与取消。

用法：
    set QT_QPA_PLATFORM=offscreen
    .venv\\Scripts\\python.exe tests\\worker_test.py
"""

from __future__ import annotations

import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(ROOT / "src"))

from PySide6.QtCore import QEventLoop, QTimer  # noqa: E402
from PySide6.QtWidgets import QApplication  # noqa: E402

import core  # noqa: E402
import main as app_main  # noqa: E402


def run_worker(worker, on_start=None) -> dict:
    loop = QEventLoop()
    result: dict = {"ok": None, "message": "", "progress": []}
    worker.progress.connect(lambda value: result["progress"].append(value))

    def finished(ok, message):
        result["ok"] = ok
        result["message"] = message
        loop.quit()

    worker.done.connect(finished)
    worker.finished.connect(worker.deleteLater)
    worker.start()
    if on_start is not None:
        on_start(worker)
    QTimer.singleShot(120000, loop.quit)
    loop.exec()
    worker.wait(5000)
    return result


def make_ts(tmp: Path) -> str:
    target = tmp / "part1.ts"
    subprocess.run(
        [
            core.ffmpeg_path(), "-y", "-hide_banner", "-loglevel", "error",
            "-f", "lavfi", "-i", "color=c=red:s=320x240:r=25:d=1",
            "-f", "lavfi", "-i", "sine=frequency=440:duration=1",
            "-c:v", "mpeg4", "-q:v", "5", "-pix_fmt", "yuv420p",
            "-c:a", "aac", "-shortest", "-f", "mpegts", str(target),
        ],
        check=True,
    )
    return str(target)


def main() -> None:
    app = QApplication(sys.argv)  # noqa: F841 - 事件循环需要

    tmp = Path(tempfile.mkdtemp(prefix="bili-ts-worker-test-"))
    try:
        # 1. 正常合并
        ts = make_ts(tmp)
        filelist = core.write_concat_filelist([ts], tmp / "filelist.txt")
        output = tmp / "merged.mp4"
        result = run_worker(
            app_main.ProcessWorker(
                [core.build_ts_merge_cmd(filelist, output)],
                labels=["合并"],
                success_message="完成",
            )
        )
        assert result["ok"] is True, result
        assert output.is_file() and output.stat().st_size > 0
        print("[通过] ProcessWorker 正常完成任务")

        # 2. 进度解析
        script = (
            "import time\n"
            "for p in ('10.0', '50.0', '100.0'):\n"
            "    print(f'[download] {p}% of 1.00MiB', flush=True)\n"
            "    time.sleep(0.05)\n"
        )
        result = run_worker(
            app_main.ProcessWorker(
                [[sys.executable, "-c", script]],
                progress_re=core.YTDLP_PROGRESS_RE,
            )
        )
        assert result["ok"] is True, result
        assert max(result["progress"]) >= 50, result["progress"]
        print(f"[通过] 进度解析：{result['progress']}")

        # 3. 取消正在运行的长任务
        long_output = tmp / "long.mp4"
        worker = app_main.ProcessWorker(
            [[
                core.ffmpeg_path(), "-y", "-hide_banner", "-loglevel", "error",
                "-f", "lavfi", "-i", "testsrc2=s=1280x720:r=25", "-t", "600",
                "-c:v", "libx264", "-preset", "veryslow", str(long_output),
            ]],
            labels=["长任务"],
        )
        result = run_worker(worker, on_start=lambda w: QTimer.singleShot(1200, w.cancel))
        assert result["ok"] is False, result
        assert "取消" in result["message"], result
        print(f"[通过] 取消任务：{result['message']}")
    finally:
        shutil.rmtree(tmp, ignore_errors=True)

    print("\n后台线程测试全部通过。")
    app.quit()


if __name__ == "__main__":
    main()
