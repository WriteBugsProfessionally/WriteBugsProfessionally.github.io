"""端到端冒烟测试：合成媒体文件，实际调用 ffmpeg 验证转换链路。

用法（不依赖 GUI）：
    python -I tests/smoke_test.py
"""

from __future__ import annotations

import json
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(ROOT / "src"))

import core  # noqa: E402


def run(cmd: list[str], desc: str) -> None:
    result = subprocess.run(
        cmd,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
        encoding="utf-8",
        errors="replace",
    )
    if result.returncode != 0:
        print(f"[失败] {desc}\n{result.stdout}")
        raise SystemExit(1)
    print(f"[通过] {desc}")


def duration_of(path: Path) -> float | None:
    """用 ffprobe 读时长；没装 ffprobe 时返回 None，调用方跳过时长校验。"""
    ffprobe = core.ffprobe_path()
    if not Path(ffprobe).is_file():
        return None
    result = subprocess.run(
        [
            ffprobe, "-v", "error",
            "-show_entries", "format=duration",
            "-of", "default=noprint_wrappers=1:nokey=1",
            str(path),
        ],
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
    )
    try:
        # ffprobe 的告警也走 stderr（已合并进 stdout），从最后一行往上找数值
        for line in reversed(result.stdout.splitlines()):
            line = line.strip()
            if not line:
                continue
            try:
                return float(line)
            except ValueError:
                continue
        raise ValueError(result.stdout)
    except ValueError:
        print(f"[失败] 无法读取时长：{path}\n{result.stdout}")
        raise SystemExit(1)


def check_paths() -> None:
    for name, path in (("ffmpeg", core.ffmpeg_path()),
                       ("ffprobe", core.ffprobe_path()),
                       ("yt-dlp", core.ytdlp_path())):
        if not Path(path).is_file():
            if name == "ffprobe":
                print(f"[警告] 找不到 ffprobe，将跳过时长校验：{path}")
                continue
            print(f"[失败] 找不到 {name}：{path}")
            raise SystemExit(1)
        print(f"[通过] {name}：{path}")


def check_duration(path: Path, low: float, high: float, desc: str) -> None:
    duration = duration_of(path)
    if duration is None:
        print(f"[跳过] {desc}：没有 ffprobe，无法校验时长")
        return
    if not low <= duration <= high:
        print(f"[失败] {desc} 时长异常：{duration}")
        raise SystemExit(1)
    print(f"[通过] {desc} 时长 {duration:.2f}s")


def make_ts_merge(tmp: Path) -> None:
    ffmpeg = core.ffmpeg_path()
    sources = []
    for index, color in enumerate(("red", "blue", "green"), start=1):
        target = tmp / f"part{index}.ts"
        run(
            [
                ffmpeg, "-y", "-hide_banner", "-loglevel", "error",
                "-f", "lavfi", "-i", f"color=c={color}:s=320x240:r=25:d=2",
                "-f", "lavfi", "-i", "sine=frequency=440:duration=2",
                "-c:v", "mpeg4", "-q:v", "5", "-pix_fmt", "yuv420p",
                "-c:a", "aac", "-shortest", "-f", "mpegts", str(target),
            ],
            f"生成 TS 分片 {target.name}",
        )
        sources.append(str(target))

    filelist = core.write_concat_filelist(sources, tmp / "filelist.txt")
    merged = tmp / "merged.mp4"
    run(core.build_ts_merge_cmd(filelist, merged), "TS 合并命令")
    check_duration(merged, 5.0, 7.5, "TS 合并结果")


def make_m4s_cache(tmp: Path) -> None:
    """APP 缓存布局：video.m4s + audio.m4s，清晰度放在 64/、80/ 子目录。"""
    ffmpeg = core.ffmpeg_path()
    cache = tmp / "cache" / "测试视频"
    high = cache / "80"
    low = cache / "64"
    high.mkdir(parents=True)
    low.mkdir(parents=True)

    run(
        [
            ffmpeg, "-y", "-hide_banner", "-loglevel", "error",
            "-f", "lavfi", "-i", "testsrc=s=320x240:r=25:d=2",
            "-c:v", "mpeg4", "-q:v", "5", "-pix_fmt", "yuv420p",
            "-movflags", "frag_keyframe+empty_moov", "-f", "mp4",
            str(high / "video.m4s"),
        ],
        "生成 video.m4s",
    )
    run(
        [
            ffmpeg, "-y", "-hide_banner", "-loglevel", "error",
            "-f", "lavfi", "-i", "sine=frequency=440:duration=2",
            "-c:a", "aac", "-movflags", "frag_keyframe+empty_moov", "-f", "mp4",
            str(high / "audio.m4s"),
        ],
        "生成 audio.m4s",
    )
    shutil.copy2(high / "video.m4s", low / "video.m4s")
    shutil.copy2(high / "audio.m4s", low / "audio.m4s")
    (cache / "entry.json").write_text(
        json.dumps({"title": "测试视频", "page_data": {"part": "第一集"}}, ensure_ascii=False),
        encoding="utf-8",
    )

    pairs = core.find_m4s_pairs(cache)
    if [pair.folder for pair in pairs] != [high.resolve()]:
        print(f"[失败] 缓存扫描结果异常：{pairs}")
        raise SystemExit(1)
    print("[通过] 缓存扫描只保留最高清晰度")

    title = core.guess_title(pairs[0].folder)
    if title != "测试视频 - 第一集":
        print(f"[失败] 标题推断异常：{title}")
        raise SystemExit(1)
    print(f"[通过] 标题推断：{title}")

    output = tmp / f"{core.sanitize_filename(title)}.mp4"
    run(core.build_m4s_cmd(pairs[0].video, pairs[0].audio, output), "m4s 转 MP4 命令")
    check_duration(output, 1.5, 2.5, "m4s 转 MP4 结果")


def make_windows_client_cache(tmp: Path) -> None:
    """Windows 客户端布局：<cid>-<page>-<id>.m4s 平铺，且开头有 9 字节占位头。"""
    ffmpeg = core.ffmpeg_path()
    folder = tmp / "winclient" / "27940095310"
    folder.mkdir(parents=True)

    def build(target: Path, args: list[str]) -> None:
        raw = target.with_suffix(".raw")
        run(
            [
                ffmpeg, "-y", "-hide_banner", "-loglevel", "error", *args,
                "-movflags", "frag_keyframe+empty_moov", "-f", "mp4", str(raw),
            ],
            f"生成 {target.name} 原始数据",
        )
        target.write_bytes(b"000000000" + raw.read_bytes())
        raw.unlink()

    build(
        folder / "27940095310-1-100050.m4s",
        ["-f", "lavfi", "-i", "testsrc=s=320x240:r=25:d=2",
         "-c:v", "mpeg4", "-q:v", "5", "-pix_fmt", "yuv420p"],
    )
    build(
        folder / "27940095310-1-30280.m4s",
        ["-f", "lavfi", "-i", "sine=frequency=440:duration=2", "-c:a", "aac"],
    )
    (folder / "videoInfo.json").write_text(
        json.dumps({"title": "客户端缓存测试"}, ensure_ascii=False), encoding="utf-8"
    )

    pairs = core.find_m4s_pairs(tmp / "winclient")
    if len(pairs) != 1 or pairs[0].audio.name != "27940095310-1-30280.m4s":
        print(f"[失败] Windows 客户端缓存扫描异常：{pairs}")
        raise SystemExit(1)
    print("[通过] Windows 客户端缓存识别出音视频配对")

    if core.m4s_offset(pairs[0].video) != 9:
        print("[失败] 未识别出 9 字节头部偏移")
        raise SystemExit(1)
    print("[通过] 识别出 9 字节头部偏移")

    title = core.guess_title(pairs[0].folder)
    if title != "客户端缓存测试":
        print(f"[失败] videoInfo.json 标题推断异常：{title}")
        raise SystemExit(1)
    print(f"[通过] 标题推断：{title}")

    output = tmp / "winclient.mp4"
    run(core.build_m4s_cmd(pairs[0].video, pairs[0].audio, output), "客户端缓存转 MP4")
    check_duration(output, 1.5, 2.5, "客户端缓存转 MP4 结果")


def make_hls_cache(tmp: Path) -> None:
    """加密 HLS 缓存（QQ浏览器那种）：分片是密文，必须走 local.m3u8 才能解密。"""
    ffmpeg = core.ffmpeg_path()
    folder = tmp / "hls" / "ce789d6833b8f32aa5dfe12b952f6f38"
    folder.mkdir(parents=True)
    key = folder / "enc.key"
    key.write_bytes(bytes(range(16)))
    key_info = folder / "keyinfo.txt"
    key_info.write_text(
        f"enc.key\n{key.resolve().as_posix()}\n00000000000000000000000000000000\n",
        encoding="utf-8",
    )
    run(
        [
            ffmpeg, "-y", "-hide_banner", "-loglevel", "error",
            "-f", "lavfi", "-i", "testsrc=s=320x240:r=25:d=4",
            "-f", "lavfi", "-i", "sine=frequency=440:duration=4",
            "-c:v", "libx264", "-preset", "ultrafast", "-pix_fmt", "yuv420p",
            "-c:a", "aac", "-shortest",
            "-f", "hls", "-hls_time", "1", "-hls_playlist_type", "vod",
            "-hls_key_info_file", str(key_info),
            "-hls_segment_filename", str(folder / "seg%d.ts"),
            str(folder / "local.m3u8"),
        ],
        "生成加密 HLS 缓存",
    )

    if (folder / "seg0.ts").read_bytes()[:1] == b"\x47":
        print("[失败] 生成的分片没有被加密，用例失去意义")
        raise SystemExit(1)
    print("[通过] 分片已加密（首个字节不是 TS 同步字节）")

    playlist = core.find_playlist(folder)
    if playlist is None or playlist.name != "local.m3u8":
        print(f"[失败] 未能选出 local.m3u8：{playlist}")
        raise SystemExit(1)
    print(f"[通过] 播放列表选择：{playlist.name}")

    output = tmp / "hls.mp4"
    run(core.build_hls_merge_cmd(playlist, output), "加密 HLS 合并")
    check_duration(output, 3.5, 4.5, "加密 HLS 合并结果")


def check_helpers() -> None:
    assert core.normalize_cookie("abc,123") == "abc,123"
    assert core.normalize_cookie("SESSDATA=abc,123") == "abc,123"
    assert core.normalize_cookie("foo=1; SESSDATA=abc,123; bar=2") == "abc,123"
    assert core.sanitize_filename('a<b>c:d"e/f\\g|h?i*j') == "a_b_c_d_e_f_g_h_i_j"
    assert core.natural_key("第10集.ts") > core.natural_key("第2集.ts")
    assert core.m4s_input_args("plain.m4s") == ["-i", "plain.m4s"]
    print("[通过] 工具函数")


def main() -> None:
    check_paths()
    check_helpers()
    tmp = Path(tempfile.mkdtemp(prefix="bili-ts-test-"))
    try:
        make_ts_merge(tmp)
        make_m4s_cache(tmp)
        make_windows_client_cache(tmp)
        make_hls_cache(tmp)
    finally:
        shutil.rmtree(tmp, ignore_errors=True)
    print("\n全部冒烟测试通过。")


if __name__ == "__main__":
    main()
