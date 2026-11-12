# -*- mode: python ; coding: utf-8 -*-
"""PyInstaller 打包配置：生成 dist/SameFileChecker/ 目录版程序。"""

a = Analysis(
    ['src/main.py'],
    pathex=['src'],
    binaries=[],
    datas=[('assets/app.ico', 'assets'), ('assets/app.png', 'assets')],
    hiddenimports=[],
    hookspath=[],
    hooksconfig={},
    runtime_hooks=[],
    excludes=['tkinter', 'unittest', 'pytest'],
    noarchive=False,
)

pyz = PYZ(a.pure)

exe = EXE(
    pyz,
    a.scripts,
    [],
    exclude_binaries=True,
    name='SameFileChecker',
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
    icon='assets/app.ico',
    version='assets/version_info.txt',
)

coll = COLLECT(
    exe,
    a.binaries,
    a.datas,
    strip=False,
    upx=False,
    upx_exclude=[],
    name='SameFileChecker',
)
