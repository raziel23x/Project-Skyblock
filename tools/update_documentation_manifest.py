#!/usr/bin/env python3
"""Regenerate docs/MANIFEST.md from the repository's authoritative Markdown files."""

from __future__ import annotations

from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
DOCS = ROOT / "docs"
MANIFEST = DOCS / "MANIFEST.md"

files = sorted(path.relative_to(DOCS).as_posix() for path in DOCS.rglob("*.md"))
lines = [
    "# Documentation Manifest",
    "",
    f"Markdown files: {len(files)}",
    "",
    "This manifest indexes the authoritative `docs` folder.",
    "",
    "## Included Files",
    "",
]
lines.extend(f"- `{path}`" for path in files)
lines.append("")
MANIFEST.write_text("\n".join(lines), encoding="utf-8")
print(f"Updated {MANIFEST.relative_to(ROOT)} with {len(files)} Markdown files.")
