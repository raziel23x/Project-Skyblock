#!/usr/bin/env python3
"""Offline resource and packaging checks for Project Skyblock."""

from __future__ import annotations

import json
import re
import struct
import sys
from pathlib import Path
from urllib.parse import unquote

ROOT = Path(__file__).resolve().parents[1]
RESOURCES = ROOT / "src" / "main" / "resources"
ASSETS = RESOURCES / "assets" / "projectskyblock"
JAVA_ROOT = ROOT / "src" / "main" / "java"
SIMULATION_ROOT = JAVA_ROOT / "raziel23x" / "projectskyblock" / "simulation"

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

# The greenfield simulation engine must remain host-independent. Persistence formats are
# quarantined at explicit platform adapters and may not leak into runtime code.
for source in sorted(SIMULATION_ROOT.rglob("*.java")):
    text = source.read_text(encoding="utf-8")
    for forbidden in ("net.minecraft.", "net.neoforged.", "com.mojang."):
        if f"import {forbidden}" in text:
            fail(f"Platform import leaked into simulation engine: {source.relative_to(ROOT)}")
    for forbidden_type in ("CompoundTag", "ListTag", "NbtOps", "Tag.TAG_"):
        if forbidden_type in text:
            fail(f"Persistence type leaked into simulation engine: {source.relative_to(ROOT)} ({forbidden_type})")

# Keep the unavoidable Minecraft NBT surface explicit. Legacy prototype block entities are
# temporary stress fixtures; new NBT-bearing gameplay files must not appear unnoticed.
allowed_nbt_sources = {
    Path("src/main/java/raziel23x/projectskyblock/blockentity/MaterialCrusherBlockEntity.java"),
    Path("src/main/java/raziel23x/projectskyblock/blockentity/ResourceGeneratorBlockEntity.java"),
    Path("src/main/java/raziel23x/projectskyblock/blockentity/ThermalGeneratorBlockEntity.java"),
    Path("src/main/java/raziel23x/projectskyblock/platform/neoforge/machine/EngineMachineBlockEntity.java"),
    Path("src/main/java/raziel23x/projectskyblock/platform/neoforge/machine/MachineRuntimeNbtCodec.java"),
}
for source in sorted(JAVA_ROOT.rglob("*.java")):
    text = source.read_text(encoding="utf-8")
    if "import net.minecraft.nbt." not in text:
        continue
    relative = source.relative_to(ROOT)
    if relative not in allowed_nbt_sources:
        fail(f"Unexpected NBT-bearing source outside quarantine: {relative}")

# Project Skyblock custom data types use a mod-specific root so unrelated mods cannot collide
# with a generic `materials/` or `processing_routes/` folder during a fail-closed reload.
for legacy_directory in (
    RESOURCES / "data" / "projectskyblock" / "materials",
    RESOURCES / "data" / "projectskyblock" / "processing_routes",
):
    if legacy_directory.exists():
        fail(f"Legacy generic custom-data directory remains: {legacy_directory.relative_to(ROOT)}")

wrapper_properties = ROOT / "gradle" / "wrapper" / "gradle-wrapper.properties"
if not wrapper_properties.exists():
    fail("Missing Gradle wrapper properties")
elif "distributionSha256Sum=" not in wrapper_properties.read_text(encoding="utf-8"):
    fail("Gradle wrapper distribution checksum is not pinned")

gradlew = ROOT / "gradlew"
if not gradlew.exists():
    fail("Missing Gradle wrapper script")
elif not gradlew.stat().st_mode & 0o111:
    fail("gradlew is not executable")


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


# Documentation manifest and local-link integrity.
docs_root = ROOT / "docs"
manifest = docs_root / "MANIFEST.md"
doc_files = sorted(path.relative_to(docs_root).as_posix() for path in docs_root.rglob("*.md"))
if not manifest.exists():
    fail("Missing docs/MANIFEST.md")
else:
    manifest_text = manifest.read_text(encoding="utf-8")
    count_match = re.search(r"^Markdown files: (\d+)$", manifest_text, re.MULTILINE)
    if not count_match or int(count_match.group(1)) != len(doc_files):
        fail(f"Documentation manifest count does not match {len(doc_files)} Markdown files")
    listed = re.findall(r"^- `([^`]+\.md)`$", manifest_text, re.MULTILINE)
    if set(listed) != set(doc_files):
        missing = sorted(set(doc_files) - set(listed))
        stale = sorted(set(listed) - set(doc_files))
        if missing:
            fail("Documentation manifest missing: " + ", ".join(missing))
        if stale:
            fail("Documentation manifest contains stale entries: " + ", ".join(stale))

markdown_files = sorted(ROOT.rglob("*.md"))
link_pattern = re.compile(r"\[[^\]]*]\(([^)]+)\)")
for markdown in markdown_files:
    text = markdown.read_text(encoding="utf-8")
    for raw_target in link_pattern.findall(text):
        target = raw_target.strip().split(maxsplit=1)[0].strip("<>")
        if not target or target.startswith(("#", "http://", "https://", "mailto:")):
            continue
        path_part = unquote(target.split("#", 1)[0])
        if not path_part:
            continue
        resolved = (markdown.parent / path_part).resolve()
        try:
            resolved.relative_to(ROOT.resolve())
        except ValueError:
            fail(f"Markdown link escapes repository: {markdown.relative_to(ROOT)} -> {target}")
            continue
        if not resolved.exists():
            fail(f"Broken Markdown link: {markdown.relative_to(ROOT)} -> {target}")

if errors:
    print("Validation failed:")
    for error in errors:
        print(f" - {error}")
    sys.exit(1)

print(f"Validation passed: {len(json_files)} JSON files and {len(list(RESOURCES.rglob('*.png')))} PNG files checked.")
