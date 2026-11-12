# -*- mode: python ; coding: utf-8 -*-
"""PyInstaller 打包配置：生成单文件夹分发的 VideoChanger.exe。

bin/ 目录不嵌入 _internal，而是在打包完成后复制到 exe 同级目录：
这样本地构建时可以自带 ffmpeg/yt-dlp，也可以完全不带（由用户自行安装到 PATH）。
许可证文本随包附带，满足 LGPLv3 的随附要求。
"""

import os
import shutil

APP_NAME = 'VideoChanger'

a = Analysis(
    ['src/main.py'],
    pathex=['src'],
    binaries=[],
    datas=[(os.path.join(SPECPATH, 'assets', 'app.ico'), 'assets')],
    hiddenimports=[],
    hookspath=[],
    hooksconfig={},
    runtime_hooks=[],
    excludes=[],
    noarchive=False,
)

pyz = PYZ(a.pure)

exe = EXE(
    pyz,
    a.scripts,
    [],
    exclude_binaries=True,
    name=APP_NAME,
    debug=False,
    bootloader_ignore_signals=False,
    strip=False,
    upx=False,
    console=False,
    disable_windowed_traceback=False,
    argv_emulation=False,
    target_arch=None,
    codesign_identity=None,
    entitlements_file=None,
    icon=os.path.join(SPECPATH, 'assets', 'app.ico'),
)

coll = COLLECT(
    exe,
    a.binaries,
    a.datas,
    strip=False,
    upx=False,
    upx_exclude=[],
    name=APP_NAME,
)

# ffmpeg.exe / yt-dlp.exe 放到 exe 同级目录（本地构建时可选）
dist_dir = os.path.join(DISTPATH, APP_NAME)
bin_src = os.path.join(SPECPATH, 'bin')
if os.path.isdir(bin_src):
    shutil.copytree(bin_src, os.path.join(dist_dir, 'bin'), dirs_exist_ok=True)

# 说明文档与许可证文本（LGPLv3 要求随二进制分发时附带）
for name in ('README.md', 'LICENSE', 'THIRD_PARTY_NOTICES.md'):
    src = os.path.join(SPECPATH, name)
    if os.path.isfile(src):
        shutil.copy2(src, os.path.join(dist_dir, name))

licenses_src = os.path.join(SPECPATH, 'licenses')
if os.path.isdir(licenses_src):
    shutil.copytree(licenses_src, os.path.join(dist_dir, 'licenses'), dirs_exist_ok=True)
