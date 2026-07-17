#!/usr/bin/env python3
"""Offline resource and packaging checks for Project Skyblock."""

from __future__ import annotations

import json
import struct
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RESOURCES = ROOT / "src" / "main" / "resources"
ASSETS = RESOURCES / "assets" / "projectskyblock"

errors: list[str] = []


def fail(message: str) -> None:
    errors.append(message)


json_files = sorted(RESOURCES.rglob("*.json"))
parsed: dict[Path, object] = {}
for path in json_files:
    try:
        parsed[path] = json.loads(path.read_text(encoding="utf-8"))
    except Exception as exc:  # noqa: BLE001 - validator should report every parser failure
        fail(f"Invalid JSON: {path.relative_to(ROOT)}: {exc}")

for archive in RESOURCES.rglob("*.zip"):
    fail(f"Archive packaged inside runtime resources: {archive.relative_to(ROOT)}")


def check_model_ref(reference: str, source: Path) -> None:
    if reference.startswith("minecraft:") or reference.startswith("builtin/"):
        return
    namespace, _, model = reference.partition(":")
    if not model:
        model = namespace
        namespace = "minecraft"
    if namespace != "projectskyblock":
        return
    target = RESOURCES / "assets" / namespace / "models" / f"{model}.json"
    if not target.exists():
        fail(f"Missing model {reference} referenced by {source.relative_to(ROOT)}")


for path, data in parsed.items():
    if not isinstance(data, dict):
        continue
    relative = path.relative_to(ASSETS) if ASSETS in path.parents else None
    if relative and relative.parts and relative.parts[0] == "blockstates":
        queue = [data]
        while queue:
            value = queue.pop()
            if isinstance(value, dict):
                if isinstance(value.get("model"), str):
                    check_model_ref(value["model"], path)
                queue.extend(value.values())
            elif isinstance(value, list):
                queue.extend(value)
    if relative and relative.parts and relative.parts[0] == "models":
        parent = data.get("parent")
        if isinstance(parent, str):
            check_model_ref(parent, path)
        textures = data.get("textures", {})
        if isinstance(textures, dict):
            for texture in textures.values():
                if not isinstance(texture, str) or texture.startswith("#") or texture.startswith("minecraft:"):
                    continue
                namespace, _, name = texture.partition(":")
                if namespace == "projectskyblock":
                    target = RESOURCES / "assets" / namespace / "textures" / f"{name}.png"
                    if not target.exists():
                        fail(f"Missing texture {texture} referenced by {path.relative_to(ROOT)}")

for png in sorted(RESOURCES.rglob("*.png")):
    raw = png.read_bytes()
    if len(raw) < 24 or raw[:8] != b"\x89PNG\r\n\x1a\n":
        fail(f"Invalid PNG signature: {png.relative_to(ROOT)}")
        continue
    width, height = struct.unpack(">II", raw[16:24])
    meta = png.with_suffix(png.suffix + ".mcmeta")
    if meta.exists() and height % width != 0:
        fail(f"Animated texture height is not a multiple of width: {png.relative_to(ROOT)} ({width}x{height})")

if errors:
    print("Validation failed:")
    for error in errors:
        print(f" - {error}")
    sys.exit(1)

print(f"Validation passed: {len(json_files)} JSON files and {len(list(RESOURCES.rglob('*.png')))} PNG files checked.")
