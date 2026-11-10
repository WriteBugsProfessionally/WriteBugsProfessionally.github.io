"""生成演示用素材：真实壁纸的重复副本 + FFmpeg 生成的短视频。"""

from __future__ import annotations

import shutil
import subprocess
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
DEMO = ROOT / "demo_data"
WALLPAPERS = [
    Path(r"C:\Windows\Web\Wallpaper\ThemeB\img24.jpg"),
    Path(r"C:\Windows\Web\Wallpaper\ThemeC\img28.jpg"),
    Path(r"C:\Windows\Web\Wallpaper\ThemeA\img20.jpg"),
]


def main() -> int:
    if DEMO.exists():
        shutil.rmtree(DEMO)
    photos = DEMO / "照片" / "2019 川西"
    backup = DEMO / "照片" / "手机备份"
    videos = DEMO / "视频"
    for folder in (photos, backup, videos):
        folder.mkdir(parents=True, exist_ok=True)

    available = [path for path in WALLPAPERS if path.is_file()]
    if len(available) < 3:
        print("缺少示例壁纸，跳过图片生成")
        return 1

    names = ["IMG_1024.jpg", "IMG_1025.jpg", "IMG_1026.jpg"]
    for source, name in zip(available, names):
        shutil.copy2(source, photos / name)

    # 内容相同、名字不同的副本
    shutil.copy2(photos / names[0], photos / "IMG_1024 - 副本.jpg")
    shutil.copy2(photos / names[0], backup / "川西旅行_照片(1).jpg")
    shutil.copy2(photos / names[1], backup / "IMG_1025 备份.jpg")

    ffmpeg = shutil.which("ffmpeg")
    if ffmpeg:
        video = videos / "VID_2024 徒步.mp4"
        command = [
            ffmpeg, "-y", "-v", "error",
            "-f", "lavfi", "-i", "testsrc2=size=1280x720:rate=25",
            "-t", "6", "-c:v", "libx264", "-preset", "veryfast",
            "-crf", "27", "-pix_fmt", "yuv420p", str(video),
        ]
        completed = subprocess.run(command, capture_output=True)
        if completed.returncode == 0:
            shutil.copy2(video, videos / "VID_2024 徒步-副本.mp4")
            subprocess.run(
                [
                    ffmpeg, "-y", "-v", "error",
                    "-f", "lavfi", "-i", "smptebars=size=1280x720:rate=25",
                    "-t", "2", "-c:v", "libx264", "-preset", "veryfast",
                    "-crf", "28", "-pix_fmt", "yuv420p",
                    str(videos / "片头.mp4"),
                ],
                capture_output=True,
            )
        else:
            print("FFmpeg 生成视频失败：", completed.stderr.decode("utf-8", "replace"))

    docs = DEMO / "文档"
    docs.mkdir(exist_ok=True)
    (docs / "行程.txt").write_text("D1 成都 - 康定 - 新都桥\nD2 新都桥 - 稻城\n", encoding="utf-8")
    shutil.copy2(docs / "行程.txt", docs / "行程 - 副本.txt")

    # 供“空文件夹清理”演示/截图使用
    for relative in (
        "照片/空相册",
        "视频/待剪辑",
        "临时导出/2024-01",
        "空文件夹示例/待整理",
        "下载/未整理/2023",
    ):
        (DEMO / relative).mkdir(parents=True, exist_ok=True)

    total = sum(path.stat().st_size for path in DEMO.rglob("*") if path.is_file())
    print(f"演示素材已生成：{DEMO}（{total / 1024 / 1024:.1f} MB）")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
