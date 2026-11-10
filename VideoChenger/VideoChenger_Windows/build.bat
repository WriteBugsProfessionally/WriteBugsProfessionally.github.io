@echo off
setlocal
cd /d "%~dp0"

if not exist ".venv\Scripts\python.exe" (
    echo [ERROR] virtual environment not found. Run:
    echo     python -m venv .venv
    echo     .venv\Scripts\python.exe -m pip install -r requirements.txt
    exit /b 1
)

rem Use "python -m PyInstaller" instead of the .exe launcher:
rem the launcher embeds an absolute path and breaks if .venv was moved.
".venv\Scripts\python.exe" -m PyInstaller build.spec --noconfirm --clean
if errorlevel 1 (
    echo [ERROR] build failed
    exit /b 1
)

echo.
echo Build done: dist\VideoChanger\VideoChanger.exe
echo Tip: put ffmpeg.exe / yt-dlp.exe into bin\ before building to bundle them.
