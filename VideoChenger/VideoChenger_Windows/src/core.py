"""VideoChanger 核心逻辑（不依赖 GUI，便于单独测试）。

包含：
- 打包/开发两种模式下的二进制路径解析
- FFmpeg 命令构造（TS 合并、B站缓存 m4s -> MP4）
- yt-dlp 命令构造（B站下载）
- B站缓存目录扫描、标题推断、文件名清洗

B站缓存有两种布局，这里都支持：
1. 手机 APP / 旧版客户端：<视频目录>/video.m4s + audio.m4s（清晰度放在 64/、80/ 子目录）
2. Windows 客户端：<cid>/<cid>-<page>-<id>.m4s 平铺，302xx 是音频轨，
   且文件开头带 9 字节占位，ffmpeg 必须 -skip_initial_bytes 才能读。
"""

from __future__ import annotations

import json
import os
import re
import shutil
import sys
from pathlib import Path
from typing import NamedTuple

APP_NAME = "VideoChanger"
__version__ = "0.3.1"

IS_WINDOWS = os.name == "nt"
CREATE_NO_WINDOW = 0x08000000 if IS_WINDOWS else 0

FFMPEG = "ffmpeg.exe" if IS_WINDOWS else "ffmpeg"
FFPROBE = "ffprobe.exe" if IS_WINDOWS else "ffprobe"
YTDLP = "yt-dlp.exe" if IS_WINDOWS else "yt-dlp"

# FFmpeg 公共参数：不读取 stdin、不刷进度行、只输出警告与错误、自动覆盖
FFMPEG_QUIET = [
    "-hide_banner",
    "-nostdin",
    "-nostats",
    "-loglevel",
    "warning",
    "-y",
]

# yt-dlp 下载进度形如：[download]  42.3% of  123.45MiB at ...
YTDLP_PROGRESS_RE = r"\[download\]\s+(\d+(?:\.\d+)?)%"


# ---------------------------------------------------------------------------
# 路径解析
# ---------------------------------------------------------------------------
def project_root() -> Path:
    """开发模式下的项目根目录（src/ 的上一级）。"""
    return Path(__file__).resolve().parent.parent


def get_bin_path(filename: str) -> str:
    """定位 bin 目录中的可执行文件。

    打包后优先取 exe 同级的 bin/（用户可以自行替换 yt-dlp.exe 来更新），
    其次取 PyInstaller 的 _MEIPASS/bin；开发模式取项目根目录下的 bin/。
    """
    candidates: list[Path] = []
    if getattr(sys, "frozen", False):
        exe_dir = Path(sys.executable).resolve().parent
        candidates.append(exe_dir / "bin" / filename)
        meipass = getattr(sys, "_MEIPASS", None)
        if meipass:
            candidates.append(Path(meipass) / "bin" / filename)
    else:
        candidates.append(project_root() / "bin" / filename)

    for candidate in candidates:
        if candidate.is_file():
            return str(candidate)

    found = shutil.which(filename)
    if found:
        return found
    return str(candidates[0])


def get_bin_dir() -> str:
    return str(Path(get_bin_path(FFMPEG)).parent)


def ffmpeg_path() -> str:
    return get_bin_path(FFMPEG)


def ffprobe_path() -> str:
    return get_bin_path(FFPROBE)


def ytdlp_path() -> str:
    return get_bin_path(YTDLP)


# ---------------------------------------------------------------------------
# 通用工具
# ---------------------------------------------------------------------------
def natural_key(text: str) -> list:
    """自然排序键：第2集排在 第10集 之前。"""
    return [int(part) if part.isdigit() else part.lower() for part in re.split(r"(\d+)", str(text))]


def unique_path(path: Path) -> Path:
    """目标文件已存在时，追加 (2)、(3)……"""
    path = Path(path)
    if not path.exists():
        return path
    for index in range(2, 1000):
        candidate = path.with_name(f"{path.stem} ({index}){path.suffix}")
        if not candidate.exists():
            return candidate
    return path


_INVALID_FILENAME = re.compile(r'[<>:"/\\|?*\x00-\x1f]')
_RESERVED_NAMES = {
    "CON", "PRN", "AUX", "NUL",
    *(f"COM{i}" for i in range(1, 10)),
    *(f"LPT{i}" for i in range(1, 10)),
}


def sanitize_filename(name: str, max_length: int = 120) -> str:
    """清洗成 Windows 上合法的文件名（保留中文）。"""
    name = _INVALID_FILENAME.sub("_", str(name)).strip().rstrip(". ")
    if not name:
        name = "video"
    if name.upper() in _RESERVED_NAMES:
        name = "_" + name
    return name[:max_length].rstrip(". ") or "video"


# ---------------------------------------------------------------------------
# TS 合并
# ---------------------------------------------------------------------------
def write_concat_filelist(files: list[str], dest_path: str | Path) -> Path:
    """生成 FFmpeg concat demuxer 的 filelist.txt。

    路径使用正斜杠（FFmpeg 在 Windows 上同样接受），单引号按 FFmpeg 规则转义。
    """
    dest = Path(dest_path)
    lines: list[str] = []
    for file in files:
        posix = Path(file).resolve().as_posix().replace("'", "'\\''")
        lines.append(f"file '{posix}'")
    dest.write_text("\n".join(lines) + "\n", encoding="utf-8", newline="\n")
    return dest


def build_ts_merge_cmd(filelist_path: str | Path, output_path: str | Path) -> list[str]:
    """TS 拼接命令：流复制，只保留视频/音频，丢弃会破坏 MP4 封装的数据流。"""
    return [
        ffmpeg_path(),
        *FFMPEG_QUIET,
        "-f", "concat",
        "-safe", "0",
        "-i", str(filelist_path),
        "-map", "0:v?",
        "-map", "0:a?",
        "-c", "copy",
        "-avoid_negative_ts", "make_zero",
        str(output_path),
    ]


# ---------------------------------------------------------------------------
# HLS / m3u8 合并
#
# QQ浏览器等下载的是 AES-128 加密的 HLS：分片本身是密文，必须把播放列表
# 交给 ffmpeg，由它用同目录的 key 文件解密，逐个喂分片一定失败。
# ---------------------------------------------------------------------------
def is_playlist(path: str | Path) -> bool:
    return Path(path).suffix.lower() == ".m3u8"


def _playlist_priority(path: Path) -> tuple[int, int]:
    """排序键，越小越优先：本地分片 > 远程地址。"""
    if path.name.lower() == "local.m3u8":
        return (0, 0)
    try:
        text = path.read_text(encoding="utf-8", errors="replace")
    except OSError:
        return (99, len(path.name))
    remote = len(re.findall(r"^https?://", text, re.MULTILINE))
    return (10 + remote, len(path.name))


def find_playlist(folder: str | Path) -> Path | None:
    """在目录里挑一个最适合离线合并的 m3u8（优先 local.m3u8）。"""
    folder = Path(folder)
    if not folder.is_dir():
        return None
    candidates = sorted(folder.glob("*.m3u8"), key=_playlist_priority)
    return candidates[0] if candidates else None


def find_playlist_dirs(root: str | Path, max_depth: int = 3) -> list[Path]:
    """找出「一个视频一个目录」的那些目录。

    用户经常直接把上一层父目录（缓存根目录）拖进来，里面每个子目录才是
    一个完整视频。这里按深度优先找出所有含播放列表的目录，避免把多个
    视频的加密分片摊平成一个列表（那样既混在一起也合不出正确结果）。
    """
    root = Path(root)
    if find_playlist(root) is not None:
        return [root]

    found: list[Path] = []

    def walk(folder: Path, depth: int) -> None:
        if depth > max_depth:
            return
        try:
            children = sorted(folder.iterdir())
        except OSError:
            return
        for child in children:
            if not child.is_dir():
                continue
            if find_playlist(child) is not None:
                found.append(child)
            else:
                walk(child, depth + 1)

    walk(root, 1)
    return found


def build_hls_merge_cmd(playlist_path: str | Path, output_path: str | Path) -> list[str]:
    """按 m3u8 合并（自动解密 AES-128 分片），流复制不重编码。"""
    return [
        ffmpeg_path(),
        *FFMPEG_QUIET,
        "-allowed_extensions", "ALL",
        "-i", str(playlist_path),
        "-map", "0:v?",
        "-map", "0:a?",
        "-c", "copy",
        str(output_path),
    ]


# ---------------------------------------------------------------------------
# B站缓存（m4s）转 MP4
# ---------------------------------------------------------------------------
class M4sPair(NamedTuple):
    """一对可以封装成 MP4 的音视频文件。"""

    video: Path
    audio: Path
    folder: Path  # 用于推断标题、按清晰度去重


# B站 Windows 客户端把音视频平铺成 <cid>-<page>-<id>.m4s，302xx 是音频轨：
# 30216=64K, 30232=132K, 30280=192K, 30250=杜比全景声, 30251=Hi-Res 无损
AUDIO_STREAM_IDS = {"30216", "30232", "30280", "30250", "30251"}
AUDIO_STREAM_ORDER = {"30251": 0, "30250": 1, "30280": 2, "30232": 3, "30216": 4}

FLAT_M4S_RE = re.compile(r"^(?P<prefix>.+)-(?P<stream_id>\d+)\.m4s$", re.IGNORECASE)

_MP4_BOX_TYPES = {b"ftyp", b"styp", b"moov", b"mdat", b"moof", b"sidx", b"free"}


def m4s_offset(path: str | Path, probe_bytes: int = 64) -> int:
    """返回 MP4 数据真正的起始偏移。

    B站 Windows 客户端会在 .m4s 开头写 9 个字节的 ASCII "000000000" 占位，
    直接交给 ffmpeg 会报 "Invalid data found when processing input"。
    """
    try:
        with open(path, "rb") as handle:
            head = handle.read(probe_bytes)
    except OSError:
        return 0
    for offset in range(0, max(0, len(head) - 8)):
        size = int.from_bytes(head[offset:offset + 4], "big")
        if head[offset + 4:offset + 8] in _MP4_BOX_TYPES and 8 <= size <= 1 << 31:
            return offset
    return 0


def m4s_input_args(path: str | Path) -> list[str]:
    """构造 -i 的输入参数；带头部偏移时强制 mov 解复用并跳过前缀。

    -skip_initial_bytes 只有显式指定 -f mov 时 mov 解复用器才会生效，
    否则 ffmpeg 仍从偏移 0 开始探测，照样报错。
    """
    offset = m4s_offset(path)
    if offset <= 0:
        return ["-i", str(path)]
    return ["-f", "mov", "-skip_initial_bytes", str(offset), "-i", str(path)]


def build_m4s_cmd(video_path: str | Path, audio_path: str | Path, output_path: str | Path) -> list[str]:
    """把分离的音视频轨无损封装成 MP4（兼容带 9 字节头的客户端缓存）。"""
    return [
        ffmpeg_path(),
        *FFMPEG_QUIET,
        *m4s_input_args(video_path),
        *m4s_input_args(audio_path),
        "-map", "0:v:0",
        "-map", "1:a:0",
        "-c", "copy",
        "-movflags", "+faststart",
        str(output_path),
    ]


# B站清晰度目录名（16=360P, 32=480P, 64=720P, 80=1080P, 112=1080P+, 116=1080P60,
# 120=4K, 125=HDR, 127=8K）。只有这些目录才可能互为“同一视频的不同清晰度”。
QUALITY_DIRS = {"16", "32", "64", "74", "80", "112", "116", "120", "125", "127"}


def _pairs_in_dir(folder: Path) -> list[M4sPair]:
    """找出单个目录里的音视频配对，兼容 APP 缓存与 Windows 客户端缓存。"""
    classic_video = folder / "video.m4s"
    classic_audio = folder / "audio.m4s"
    if classic_video.is_file() and classic_audio.is_file():
        return [M4sPair(classic_video, classic_audio, folder)]

    if not folder.is_dir():
        return []

    groups: dict[str, list[tuple[str, Path]]] = {}
    for entry in folder.iterdir():
        if not entry.is_file():
            continue
        match = FLAT_M4S_RE.match(entry.name)
        if match:
            groups.setdefault(match["prefix"], []).append((match["stream_id"], entry))

    pairs: list[M4sPair] = []
    for items in groups.values():
        videos = [item for item in items if item[0] not in AUDIO_STREAM_IDS]
        audios = [item for item in items if item[0] in AUDIO_STREAM_IDS]
        if not videos or not audios:
            continue
        # 同一 cid 时长一致，文件越大码率越高（清晰度越好）
        videos.sort(key=lambda item: (item[1].stat().st_size, int(item[0])), reverse=True)
        audios.sort(key=lambda item: AUDIO_STREAM_ORDER.get(item[0], 99))
        pairs.append(M4sPair(videos[0][1], audios[0][1], folder))
    return pairs


def find_m4s_pairs(root: str | Path) -> list[M4sPair]:
    """递归查找可转换的音视频配对。

    兼容两种缓存布局：
    1. APP/旧版：<视频目录>/video.m4s + audio.m4s（清晰度放在 64/、80/ 等子目录）
    2. Windows 客户端：<cid>/<cid>-<page>-<id>.m4s 平铺在同一目录

    同一视频的不同清晰度会出现多份，同一个父目录下只保留清晰度最高的一份。
    """
    root = Path(root).resolve()
    if not root.is_dir():
        return []

    groups: dict[Path, list[M4sPair]] = {}
    for dirpath, dirnames, _ in os.walk(root):
        current = Path(dirpath)
        found = _pairs_in_dir(current)
        if found:
            groups.setdefault(current.parent, []).extend(found)
            dirnames[:] = []  # 命中后不再深入

    result: list[M4sPair] = []
    for pairs in groups.values():
        quality = [p for p in pairs if p.folder.name in QUALITY_DIRS]
        independent = [p for p in pairs if p.folder.name not in QUALITY_DIRS]
        if quality:
            quality.sort(key=lambda p: int(p.folder.name), reverse=True)
            result.append(quality[0])
        result.extend(independent)

    result.sort(key=lambda p: natural_key(str(p.folder)))
    return result


CACHE_META_FILES = ("entry.json", "videoInfo.json", ".videoinfo")


def load_cache_meta(folder: str | Path) -> dict | None:
    """在自身及两级父目录中查找 B站缓存的元数据 JSON。

    APP 缓存用 entry.json；Windows 客户端用 videoInfo.json / .videoinfo。
    """
    folder = Path(folder)
    for current in (folder, folder.parent, folder.parent.parent):
        for name in CACHE_META_FILES:
            candidate = current / name
            if not candidate.is_file():
                continue
            try:
                data = json.loads(candidate.read_text(encoding="utf-8-sig"))
            except (OSError, ValueError):
                continue
            if isinstance(data, dict):
                return data
    return None


def guess_title(pair_dir: str | Path) -> str:
    """尽量从缓存元数据推断视频标题，失败时回退到目录名。"""
    pair_dir = Path(pair_dir)
    data = load_cache_meta(pair_dir)
    if data:
        title = str(data.get("title") or "").strip()
        part = ""
        page_data = data.get("page_data")
        if isinstance(page_data, dict):
            part = str(page_data.get("part") or "").strip()
        if part and part != title:
            return f"{title} - {part}" if title else part
        if title:
            return title

    current = pair_dir
    for _ in range(4):
        if not current.name.isdigit():
            return current.name
        if current.parent == current:
            break
        current = current.parent
    return "video"


# ---------------------------------------------------------------------------
# B站下载（yt-dlp）
# ---------------------------------------------------------------------------
def normalize_cookie(cookie: str | None) -> str:
    """接受裸 SESSDATA、"SESSDATA=xxx" 或整段 Cookie，取出 SESSDATA 值。"""
    cookie = (cookie or "").strip()
    if cookie.lower().startswith("cookie:"):
        cookie = cookie.split(":", 1)[1].strip()
    match = re.search(r"(?:^|;\s*)SESSDATA=([^;]+)", cookie, re.IGNORECASE)
    if match:
        return match.group(1).strip()
    if cookie.upper().startswith("SESSDATA="):
        return cookie.split("=", 1)[1].strip()
    return cookie


def write_cookie_file(sessdata: str, dest_path: str | Path) -> Path:
    """生成 Netscape 格式 cookies.txt。

    B站提取器从 Cookie 罐读取 SESSDATA，仅用 --add-header 不一定能被识别，
    所以这里写成临时 cookies 文件交给 --cookies。
    """
    dest = Path(dest_path)
    lines = [
        "# Netscape HTTP Cookie File",
        "# https://curl.se/docs/http-cookies.html",
        ".bilibili.com\tTRUE\t/\tFALSE\t2147483647\tSESSDATA\t" + sessdata,
        "",
    ]
    dest.write_text("\n".join(lines), encoding="utf-8")
    return dest


def build_ytdlp_cmd(
    url: str,
    output_dir: str | Path,
    cookie: str | None = None,
    cookies_path: str | Path | None = None,
) -> list[str]:
    """构造 yt-dlp 下载命令。

    优先使用 cookies_path（Netscape cookies.txt，最可靠）；
    未提供时退回 --add-header 方式。
    """
    cmd = [
        ytdlp_path(),
        "--newline",
        "--no-colors",
        "--encoding", "utf-8",
        "--ffmpeg-location", get_bin_dir(),
        "-f", "bv*+ba/b",
        "--merge-output-format", "mp4",
        "--remux-video", "mp4",
        "-P", str(output_dir),
        "-o", "%(title)s.%(ext)s",
    ]

    sessdata = normalize_cookie(cookie)
    if cookies_path is not None:
        cmd += ["--cookies", str(cookies_path)]
    elif sessdata:
        cmd += ["--add-header", f"Cookie:SESSDATA={sessdata}"]

    cmd.append(url)
    return cmd
