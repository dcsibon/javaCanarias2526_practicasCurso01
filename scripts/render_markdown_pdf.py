#!/usr/bin/env python3
from __future__ import annotations

import argparse
import re
import subprocess
from pathlib import Path

from fpdf import FPDF

EMOJI_ICON_MAP = {
    "🎵": "1f3b5",
    "🎯": "1f3af",
    "✅": "2705",
    "⚠️": "26a0",
    "⚠": "26a0",
}

# Match known emoji tokens first for deterministic prefix parsing.
EMOJI_PREFIX_RE = re.compile(r"^(🎵|🎯|✅|⚠️|⚠)\s*")
HR_RE = re.compile(r"[-*_]{3,}")


def ensure_emoji_icon(cache_dir: Path, emoji: str) -> Path | None:
    code = EMOJI_ICON_MAP.get(emoji)
    if not code:
        return None
    out = cache_dir / f"{code}.png"
    if out.exists() and out.stat().st_size > 0:
        return out
    url = f"https://raw.githubusercontent.com/twitter/twemoji/master/assets/72x72/{code}.png"
    try:
        subprocess.run(
            ["curl", "-fsSL", "-o", str(out), url],
            check=True,
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
        )
        return out if out.exists() and out.stat().st_size > 0 else None
    except Exception:
        return None


def split_prefix_emoji(text: str) -> tuple[str | None, str]:
    m = EMOJI_PREFIX_RE.match(text)
    if not m:
        return None, text
    emoji = m.group(1)
    rest = text[m.end() :].lstrip()
    return emoji, rest


def render_line_with_prefix_emoji(pdf: FPDF, text: str, line_h: float, icon_cache: Path) -> None:
    emoji, rest = split_prefix_emoji(text)
    if not emoji:
        pdf.multi_cell(0, line_h, text, markdown=True, new_x="LMARGIN", new_y="NEXT")
        return

    icon = ensure_emoji_icon(icon_cache, emoji)
    y0 = pdf.get_y()
    x0 = pdf.l_margin
    if icon:
        size = max(4.8, line_h - 0.7)
        pdf.image(str(icon), x=x0, y=y0 + 0.4, w=size, h=size)
        pdf.set_xy(x0 + size + 2.2, y0)
    else:
        pdf.set_xy(x0, y0)
        pdf.cell(6, line_h, "*", new_x="RIGHT", new_y="TOP")

    pdf.multi_cell(0, line_h, rest, markdown=True, new_x="LMARGIN", new_y="NEXT")


def draw_shaded_separator(pdf: FPDF) -> None:
    y = pdf.get_y() + 1
    x = pdf.l_margin
    w = pdf.w - pdf.l_margin - pdf.r_margin
    pdf.set_fill_color(236, 239, 243)
    pdf.rect(x, y, w, 3.4, style="F")
    pdf.set_draw_color(198, 204, 212)
    pdf.set_line_width(0.2)
    pdf.line(x, y + 1.7, x + w, y + 1.7)
    pdf.set_y(y + 5.2)


def add_fonts(pdf: FPDF) -> None:
    # Unicode font keeps accents and Spanish punctuation stable.
    font_path = Path("/System/Library/Fonts/Supplemental/Arial Unicode.ttf")
    if font_path.exists():
        pdf.add_font("DocFont", "", str(font_path))
        pdf.add_font("DocFont", "B", str(font_path))
        pdf.add_font("DocFont", "I", str(font_path))
        pdf.add_font("DocFont", "BI", str(font_path))
    else:
        # Fallback for environments without Arial Unicode.
        pdf.add_font("DocFont", "", "/Library/Fonts/Arial Unicode.ttf")
        pdf.add_font("DocFont", "B", "/Library/Fonts/Arial Unicode.ttf")
        pdf.add_font("DocFont", "I", "/Library/Fonts/Arial Unicode.ttf")
        pdf.add_font("DocFont", "BI", "/Library/Fonts/Arial Unicode.ttf")


def sanitize_lines(lines: list[str]) -> list[str]:
    sanitized = [ln.rstrip("\n") for ln in lines]
    while sanitized and (not sanitized[-1].strip() or HR_RE.fullmatch(sanitized[-1].strip())):
        sanitized.pop()
    return sanitized


def has_room(pdf: FPDF, needed_height: float) -> bool:
    return (pdf.h - pdf.b_margin - pdf.get_y()) >= needed_height


def ensure_room(pdf: FPDF, needed_height: float) -> None:
    if not has_room(pdf, needed_height):
        pdf.add_page()


def render_markdown_to_pdf(input_md: Path, output_pdf: Path, icon_cache: Path) -> None:
    lines = sanitize_lines(input_md.read_text(encoding="utf-8").splitlines())

    pdf = FPDF(format="A4")
    pdf.set_auto_page_break(auto=True, margin=16)
    pdf.set_margins(18, 16, 18)
    pdf.add_page()
    add_fonts(pdf)

    text_color = (22, 22, 24)
    muted = (96, 101, 110)
    code_bg = (246, 247, 249)

    in_code = False

    for raw in lines:
        line = raw
        stripped = line.strip()

        if stripped.startswith("```"):
            in_code = not in_code
            if in_code:
                lang = stripped.strip("`").strip()
                if lang:
                    pdf.set_text_color(*muted)
                    pdf.set_font("DocFont", "I", 9)
                    pdf.multi_cell(0, 4.8, f"[{lang}]", new_x="LMARGIN", new_y="NEXT")
                pdf.ln(0.8)
            else:
                pdf.ln(1.8)
            continue

        if in_code:
            pdf.set_fill_color(*code_bg)
            pdf.set_text_color(*text_color)
            pdf.set_font("Courier", "", 10)
            code_line = line if line else " "
            pdf.multi_cell(0, 5.6, code_line, fill=True, new_x="LMARGIN", new_y="NEXT")
            continue

        if not stripped:
            if has_room(pdf, 4.0):
                pdf.ln(2.8)
            continue

        if HR_RE.fullmatch(stripped):
            if has_room(pdf, 8.0):
                draw_shaded_separator(pdf)
            elif has_room(pdf, 2.0):
                pdf.ln(1.2)
            continue

        if stripped.startswith("### "):
            ensure_room(pdf, 10.0)
            pdf.set_text_color(*text_color)
            pdf.set_font("DocFont", "B", 13)
            render_line_with_prefix_emoji(pdf, stripped[4:], 7.0, icon_cache)
            pdf.ln(0.5)
            continue

        if stripped.startswith("## "):
            ensure_room(pdf, 11.0)
            pdf.set_text_color(*text_color)
            pdf.set_font("DocFont", "B", 17)
            render_line_with_prefix_emoji(pdf, stripped[3:], 8.1, icon_cache)
            pdf.ln(1.2)
            continue

        if stripped.startswith("# "):
            ensure_room(pdf, 13.0)
            pdf.set_text_color(*text_color)
            pdf.set_font("DocFont", "B", 22)
            render_line_with_prefix_emoji(pdf, stripped[2:], 10.0, icon_cache)
            pdf.ln(1.6)
            continue

        if stripped.startswith(("- ", "* ")):
            # Keep bullet marker and first content line together on the same page.
            ensure_room(pdf, 8.0)
            content = stripped[2:]
            pdf.set_text_color(*text_color)
            pdf.set_font("DocFont", "", 11)
            x0, y0 = pdf.get_x(), pdf.get_y()

            bullet_emoji, _ = split_prefix_emoji(content)
            if bullet_emoji and ensure_emoji_icon(icon_cache, bullet_emoji):
                icon = ensure_emoji_icon(icon_cache, bullet_emoji)
                size = 4.8
                pdf.image(str(icon), x=x0, y=y0 + 0.4, w=size, h=size)
                pdf.set_xy(x0 + size + 2.0, y0)
            else:
                pdf.cell(5, 6, "-", new_x="RIGHT", new_y="TOP")
                pdf.set_xy(x0 + 5, y0)

            render_line_with_prefix_emoji(pdf, content, 6.0, icon_cache)
            continue

        pdf.set_text_color(*text_color)
        pdf.set_font("DocFont", "", 11)
        render_line_with_prefix_emoji(pdf, stripped, 6.0, icon_cache)

    output_pdf.parent.mkdir(parents=True, exist_ok=True)
    pdf.output(str(output_pdf))


def main() -> None:
    parser = argparse.ArgumentParser(description="Render Markdown to styled PDF")
    parser.add_argument("input", type=Path, help="Input markdown file")
    parser.add_argument("-o", "--output", type=Path, help="Output PDF file")
    parser.add_argument(
        "--emoji-cache",
        type=Path,
        default=Path(".cache/twemoji"),
        help="Directory to cache emoji icons",
    )
    args = parser.parse_args()

    output = args.output or args.input.with_suffix(".pdf")
    args.emoji_cache.mkdir(parents=True, exist_ok=True)

    render_markdown_to_pdf(args.input, output, args.emoji_cache)
    print(f"Generated styled PDF: {output.resolve()}")


if __name__ == "__main__":
    main()
