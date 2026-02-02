#!/usr/bin/env python3
"""
EMOJI-FLOW Pattern Generator
Converts emojis to UTF-8 hex patterns for Flex lexer rules
"""

import sys
import re
import datetime


def emoji_to_hex_pattern(emoji_str):
    """Convert emoji string to C-style hex pattern"""
    utf8_bytes = emoji_str.encode("utf-8")
    hex_pattern = "".join(f"\\x{b:02X}" for b in utf8_bytes)
    return f'"{hex_pattern}"'


def get_unicode_info(emoji_str):
    """Get Unicode codepoint information for an emoji"""
    codepoints = []
    for char in emoji_str:
        codepoint = ord(char)
        codepoints.append(f"U+{codepoint:04X}")
    return " ".join(codepoints)


def generate_lexer_rule(emoji, token_name, token_const, description=""):
    """Generate complete lexer rule for an emoji"""
    hex_pattern = emoji_to_hex_pattern(emoji)
    unicode_info = get_unicode_info(emoji)

    # Count emoji characters for column tracking
    emoji_char_count = len([c for c in emoji if ord(c) > 127])

    rule = f"    /* {description}: {emoji} ({unicode_info}) */\n"
    rule += f'{hex_pattern:<40} {{ output_token({token_const}, "{token_name}", yytext); col_num += {emoji_char_count}; return {token_const}; }}'

    return rule


def analyze_existing_patterns():
    """Analyze patterns from the current emoji_flow.l file"""
    try:
        with open("emoji_flow.l", "r", encoding="utf-8") as f:
            content = f.read()

        # Find all emoji patterns in comments
        emoji_pattern = r"/\* .+?: (.+?) \(([^)]+)\) \*/"
        matches = re.findall(emoji_pattern, content)

        print("=== EXISTING EMOJIS IN LEXER ===")
        for i, (emoji, unicode_info) in enumerate(matches, 1):
            hex_pattern = emoji_to_hex_pattern(emoji)
            print(f"{i:2d}. {emoji:<3} → {hex_pattern:<40} ({unicode_info})")

    except FileNotFoundError:
        print("emoji_flow.l not found in current directory")


def interactive_mode():
    """Interactive emoji-to-pattern converter"""
    print("\n=== INTERACTIVE PATTERN GENERATOR ===")
    print("Enter emojis to convert to hex patterns (or 'quit' to exit)")
    print("Example: ▶️ or 🤔 or 🔁❓")

    while True:
        try:
            user_input = input("\nEmoji: ").strip()

            if user_input.lower() in ["quit", "exit", "q"]:
                break

            if not user_input:
                continue

            hex_pattern = emoji_to_hex_pattern(user_input)
            unicode_info = get_unicode_info(user_input)
            byte_count = len(user_input.encode("utf-8"))
            char_count = len([c for c in user_input if ord(c) > 127])

            print(f"  Emoji: {user_input}")
            print(f"  Unicode: {unicode_info}")
            print(f"  Hex Pattern: {hex_pattern}")
            print(f"  Byte Length: {byte_count} bytes")
            print(f"  Char Count: {char_count} (for col_num)")
            print(f"  C Rule: {hex_pattern:<40} {{ /* Your action here */ }}")

        except KeyboardInterrupt:
            break
        except Exception as e:
            print(f"Error: {e}")


def batch_convert_from_file(filename):
    """Convert emojis from a text file"""
    try:
        with open(filename, "r", encoding="utf-8") as f:
            emojis = f.read().strip().split("\n")

        print(f"\n=== BATCH CONVERSION FROM {filename} ===")
        for i, emoji in enumerate(emojis, 1):
            if emoji.strip():
                hex_pattern = emoji_to_hex_pattern(emoji.strip())
                unicode_info = get_unicode_info(emoji.strip())
                print(
                    f"{i:2d}. {emoji.strip():<3} → {hex_pattern:<40} ({unicode_info})"
                )

    except FileNotFoundError:
        print(f"File '{filename}' not found")


def generate_common_emojis():
    """Generate patterns for common programming emojis"""
    common_emojis = {
        # Control Flow
        "▶️": ("START", "START", "Play/Start"),
        "⏹️": ("END", "END", "Stop/End"),
        "⏸️": ("PAUSE", "PAUSE", "Pause"),
        "🔁": ("LOOP", "LOOP", "Loop"),
        "🔚": ("STOP", "STOP", "Stop"),
        # Logic
        "🤔": ("IF", "IF", "If condition"),
        "❓": ("QUESTION", "QUESTION", "Question mark"),
        "✅": ("TRUE", "TRUE", "True/Success"),
        "❌": ("FALSE", "FALSE", "False/Error"),
        "🚫": ("NOT", "NOT", "Logical NOT"),
        # I/O Operations
        "📥": ("INPUT", "INPUT", "Input"),
        "📤": ("OUTPUT", "OUTPUT", "Output"),
        "📝": ("LOG", "LOG", "Log/Write"),
        "🔔": ("ALERT", "ALERT", "Alert/Notification"),
        # File Operations
        "📂": ("OPEN", "OPEN", "Open file"),
        "📁": ("CLOSE", "CLOSE", "Close file"),
        "🗃️": ("FILE", "FILE", "File"),
        # Data Types
        "🔢": ("INT", "INT", "Integer"),
        "🔤": ("STRING", "STRING", "String"),
        "📛": ("ID", "ID", "Identifier"),
        # Operators
        "➕": ("PLUS", "PLUS", "Addition"),
        "➖": ("MINUS", "MINUS", "Subtraction"),
        "✖️": ("MULTIPLY", "MULTIPLY", "Multiplication"),
        "➗": ("DIVIDE", "DIVIDE", "Division"),
        "🟰": ("EQUAL", "EQUAL", "Equal"),
    }

    print("\n=== COMMON PROGRAMMING EMOJIS ===")
    for emoji, (token_name, token_const, description) in common_emojis.items():
        rule = generate_lexer_rule(emoji, token_name, token_const, description)
        print(rule)
        print()


def generate_complete_emoji_list(output_filename="emoji_patterns_output.txt"):
    """Generate complete list of all emojis with UTF codes and patterns, save to file"""

    # Extract emojis from lexer file
    emoji_data = []

    try:
        with open("emoji_flow.l", "r", encoding="utf-8") as f:
            content = f.read()

        # Find all emoji patterns in comments with their descriptions
        emoji_pattern = r"/\* (.+?): (.+?) \(([^)]+)\) \*/"
        matches = re.findall(emoji_pattern, content)

        for description, emoji, unicode_info in matches:
            hex_pattern = emoji_to_hex_pattern(emoji)
            byte_length = len(emoji.encode("utf-8"))
            char_count = len([c for c in emoji if ord(c) > 127])

            emoji_data.append(
                {
                    "description": description,
                    "emoji": emoji,
                    "unicode_info": unicode_info,
                    "hex_pattern": hex_pattern,
                    "byte_length": byte_length,
                    "char_count": char_count,
                }
            )

    except FileNotFoundError:
        print("Warning: emoji_flow.l not found, generating common emojis instead")
        # Fallback to common emojis if lexer file not found
        common_emojis = {
            "START": "▶️",
            "END": "⏹️",
            "PAUSE": "⏸️",
            "LOOP": "🔁",
            "STOP": "🔚",
            "IF": "🤔",
            "QUESTION": "❓",
            "TRUE": "✅",
            "FALSE": "❌",
            "NOT": "🚫",
            "INPUT": "📥",
            "OUTPUT": "📤",
            "LOG": "📝",
            "ALERT": "🔔",
            "OPEN": "📂",
            "CLOSE": "📁",
            "FILE": "🗃️",
            "DELETE": "🗑️",
            "INT": "🔢",
            "STRING": "🔤",
            "ID": "📛",
            "PLUS": "➕",
            "MINUS": "➖",
        }

        for desc, emoji in common_emojis.items():
            unicode_info = get_unicode_info(emoji)
            hex_pattern = emoji_to_hex_pattern(emoji)
            byte_length = len(emoji.encode("utf-8"))
            char_count = len([c for c in emoji if ord(c) > 127])

            emoji_data.append(
                {
                    "description": desc,
                    "emoji": emoji,
                    "unicode_info": unicode_info,
                    "hex_pattern": hex_pattern,
                    "byte_length": byte_length,
                    "char_count": char_count,
                }
            )

    # Generate output file
    try:
        with open(output_filename, "w", encoding="utf-8") as f:
            # Write header
            f.write("# EMOJI-FLOW Pattern Reference\n")
            f.write("# Generated by emoji_pattern_generator.py\n")
            f.write("# Complete list of emojis with UTF codes and hex patterns\n")
            f.write("=" * 80 + "\n\n")

            # Write summary
            f.write(f"Total Emojis: {len(emoji_data)}\n")
            f.write(
                f"Generated: {sys.modules['datetime'].datetime.now() if 'datetime' in sys.modules else 'Now'}\n\n"
            )

            # Write table header
            f.write(
                "| # | Description      | Emoji | Unicode Info        | Hex Pattern                              | Bytes | Chars |\n"
            )
            f.write(
                "|---|------------------|-------|---------------------|------------------------------------------|-------|-------|\n"
            )

            # Write emoji data
            for i, data in enumerate(emoji_data, 1):
                f.write(
                    f"| {i:2d} | {data['description']:<16} | {data['emoji']:<5} | {data['unicode_info']:<19} | {data['hex_pattern']:<40} | {data['byte_length']:5d} | {data['char_count']:5d} |\n"
                )

            f.write("\n" + "=" * 80 + "\n\n")

            # Write C code snippets
            f.write("/* C LEXER PATTERNS FOR COPY-PASTE */\n\n")
            for i, data in enumerate(emoji_data, 1):
                f.write(
                    f"    /* {data['description']}: {data['emoji']} ({data['unicode_info']}) */\n"
                )
                f.write(
                    f"{data['hex_pattern']:<40} {{ output_token({data['description'].upper()}_TOK, \"{data['description'].upper()}\", yytext); col_num += {data['char_count']}; return {data['description'].upper()}_TOK; }}\n\n"
                )

            # Write Python dictionary for reference
            f.write("\n" + "=" * 80 + "\n")
            f.write("# PYTHON REFERENCE DICTIONARY\n\n")
            f.write("EMOJI_PATTERNS = {\n")
            for data in emoji_data:
                f.write(f"    '{data['emoji']}': {{\n")
                f.write(f"        'description': '{data['description']}',\n")
                f.write(f"        'unicode': '{data['unicode_info']}',\n")
                f.write(f"        'hex_pattern': {data['hex_pattern']},\n")
                f.write(f"        'byte_length': {data['byte_length']},\n")
                f.write(f"        'char_count': {data['char_count']}\n")
                f.write(f"    }},\n")
            f.write("}\n\n")

            # Write JSON format
            f.write("# JSON FORMAT\n")
            f.write("[\n")
            for i, data in enumerate(emoji_data):
                f.write("  {\n")
                f.write(f"    \"emoji\": \"{data['emoji']}\",\n")
                f.write(f"    \"description\": \"{data['description']}\",\n")
                f.write(f"    \"unicode\": \"{data['unicode_info']}\",\n")
                f.write(f"    \"hex_pattern\": \"{data['hex_pattern']}\",\n")
                f.write(f"    \"byte_length\": {data['byte_length']},\n")
                f.write(f"    \"char_count\": {data['char_count']}\n")
                f.write("  }" + ("," if i < len(emoji_data) - 1 else "") + "\n")
            f.write("]\n")

        print(f"✅ Complete emoji list generated: {output_filename}")
        print(f"📊 Total emojis processed: {len(emoji_data)}")
        print(f"📄 File includes: Table view, C patterns, Python dict, JSON format")

        # Also print summary to console
        print(f"\n{'='*60}")
        print("EMOJI SUMMARY")
        print(f"{'='*60}")
        for i, data in enumerate(emoji_data, 1):
            print(
                f"{i:2d}. {data['emoji']:<3} {data['description']:<15} → {data['hex_pattern']:<35} ({data['unicode_info']})"
            )

        return True

    except Exception as e:
        print(f"❌ Error writing to {output_filename}: {e}")
        return False


def main():
    print("🎯 EMOJI-FLOW Pattern Generator")
    print("=" * 50)

    if len(sys.argv) > 1:
        if sys.argv[1] == "--analyze":
            analyze_existing_patterns()
        elif sys.argv[1] == "--common":
            generate_common_emojis()
        elif sys.argv[1] == "--generate" or sys.argv[1] == "--list":
            output_file = (
                sys.argv[2] if len(sys.argv) > 2 else "emoji_patterns_output.txt"
            )
            generate_complete_emoji_list(output_file)
        elif sys.argv[1] == "--file" and len(sys.argv) > 2:
            batch_convert_from_file(sys.argv[2])
        else:
            # Convert single emoji from command line
            emoji = " ".join(sys.argv[1:])
            hex_pattern = emoji_to_hex_pattern(emoji)
            unicode_info = get_unicode_info(emoji)
            print(f"Emoji: {emoji}")
            print(f"Pattern: {hex_pattern}")
            print(f"Unicode: {unicode_info}")
    else:
        # Show menu
        print("Usage options:")
        print(
            "  python emoji_pattern_generator.py --analyze      # Analyze existing .l file"
        )
        print(
            "  python emoji_pattern_generator.py --generate     # Generate complete emoji list to file"
        )
        print(
            "  python emoji_pattern_generator.py --list [file]  # Generate list (custom filename)"
        )
        print(
            "  python emoji_pattern_generator.py --common       # Show common emoji patterns"
        )
        print(
            "  python emoji_pattern_generator.py --file <txt>   # Convert from text file"
        )
        print(
            "  python emoji_pattern_generator.py <emoji>        # Convert single emoji"
        )
        print("  python emoji_pattern_generator.py               # Interactive mode")
        print()

        choice = input(
            "Choose mode (1=Analyze, 2=Generate List, 3=Common, 4=Interactive, 5=Quit): "
        ).strip()

        if choice == "1":
            analyze_existing_patterns()
        elif choice == "2":
            output_file = input(
                "Output filename (default: emoji_patterns_output.txt): "
            ).strip()
            if not output_file:
                output_file = "emoji_patterns_output.txt"
            generate_complete_emoji_list(output_file)
        elif choice == "3":
            generate_common_emojis()
        elif choice == "4":
            interactive_mode()
        elif choice == "5":
            print("Goodbye! 👋")
        else:
            interactive_mode()


if __name__ == "__main__":
    main()
