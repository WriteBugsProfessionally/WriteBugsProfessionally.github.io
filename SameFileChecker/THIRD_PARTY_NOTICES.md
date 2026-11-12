# Third-party notices

SameFileChecker 1.2.0  
Copyright (c) 2026 WriteBugsProfessionally

The application source code is licensed under the MIT License; see `LICENSE`.

## Dependencies

| Component | Version | License | Notes |
| --- | --- | --- | --- |
| Python | 3.14 | PSF License 2.0 | `LICENSES/PYTHON_LICENSE.txt` |
| PySide6 / Qt | 6.12 | LGPL-3.0 | `LICENSES/LGPL-3.0.txt`; only bundled in the full Windows package |
| PyInstaller | 6.22.3 | GPL-2.0-or-later with bootloader exception | `LICENSES/PYINSTALLER_LICENSE.txt` |
| BLAKE2b | Python stdlib `hashlib` | PSF License 2.0 | no separate binary |

FFmpeg is **not bundled**. Video thumbnails use an FFmpeg executable already
installed on the user's machine, or one placed in `bin/`.

## Icons and logo

The UI icons are original SVG drawings. The application logo is a pixel-art
design derived from an avatar supplied by the project owner. If you redistribute
the logo publicly, make sure you have the right to use that avatar.

## Screenshots

The screenshots under `docs/` were captured with demo material. The older demo
material used Windows wallpapers; those screenshots are only included in the
personal full package and must not be redistributed publicly. The public source
package does not include any screenshot images.

## Qt / LGPL information (full Windows package only)

The full Windows package includes Qt and PySide6 libraries under LGPL-3.0.
You may replace the Qt/PySide6 DLLs in `_internal/PySide6/` with compatible
versions. The application source is included in the `source/` directory.
Qt source code is available at <https://code.qt.io/cgit/>.
