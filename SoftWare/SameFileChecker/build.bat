@echo off
setlocal
cd /d "%~dp0"

if not exist ".venv\Scripts\pyinstaller.exe" (
    echo [ERROR] Missing venv. Run:
    echo     python -m venv .venv
    echo     .venv\Scripts\python.exe -m pip install -r requirements.txt
    exit /b 1
)

if not exist "assets\app.ico" (
    echo Generating icon...
    ".venv\Scripts\python.exe" tools\make_icon.py
)

".venv\Scripts\pyinstaller.exe" build.spec --noconfirm --clean
if errorlevel 1 (
    echo [ERROR] Build failed
    exit /b 1
)

echo.
echo Done: dist\SameFileChecker\SameFileChecker.exe
