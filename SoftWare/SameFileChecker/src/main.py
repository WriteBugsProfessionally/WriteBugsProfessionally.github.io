"""程序入口：默认启动图形界面，加 --cli 进入命令行模式。"""

from __future__ import annotations

import os
import sys


def _bootstrap() -> None:
    here = os.path.dirname(os.path.abspath(__file__))
    if here not in sys.path:
        sys.path.insert(0, here)


def main(argv: list[str] | None = None) -> int:
    _bootstrap()
    args = list(sys.argv[1:] if argv is None else argv)
    if "--cli" in args:
        args.remove("--cli")
        from sfc.cli import main as cli_main

        return cli_main(args)
    from sfc.ui.app import run

    return run(args)


if __name__ == "__main__":
    raise SystemExit(main())
