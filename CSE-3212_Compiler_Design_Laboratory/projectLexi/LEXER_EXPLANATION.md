# EMOJI-FLOW Lexer - Line-by-Line Explanation 📖

## Author: Arka Braja Prasad Nath (Roll: 2107055)

This document provides a complete explanation of the `emoji_flow.l` Flex lexer file for the EMOJI-FLOW programming language.

---

## 📚 Table of Contents

1. [Flex File Structure Overview](#1-flex-file-structure-overview)
2. [Section 1: Definitions Section (%{ ... %})](#2-section-1-definitions-section)
3. [Section 2: Options and Macros](#3-section-2-options-and-macros)
4. [Section 3: Rules Section (%% ... %%)](#4-section-3-rules-section)
5. [Section 4: User Code Section (main function)](#5-section-4-user-code-section)
6. [How Emojis Are Matched (UTF-8 Encoding)](#6-how-emojis-are-matched-utf-8-encoding)
7. [Token Flow Diagram](#7-token-flow-diagram)
8. [Viva Questions & Answers](#8-viva-questions--answers)

---

## 1. Flex File Structure Overview

A Flex (.l) file has **three sections** separated by `%%`:

```
%{
    DEFINITIONS SECTION (C code)
%}
    OPTIONS AND MACROS
%%
    RULES SECTION (patterns and actions)
%%
    USER CODE SECTION (main function)
```

---

## 2. Section 1: Definitions Section

### Lines 1-2: Opening the Definition Block

```c
%{
```

**Explanation:** This opens the **definitions section**. Everything between `%{` and `%}` is copied directly to the generated C file (`lex.yy.c`). This is where we write C code that will be available throughout the lexer.

---

### Lines 3-5: Header Includes

```c
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
```

| Header     | Purpose                                                         |
| ---------- | --------------------------------------------------------------- |
| `stdio.h`  | Standard I/O functions (`printf`, `fprintf`, `fopen`, `fclose`) |
| `stdlib.h` | Standard library functions (`exit`)                             |
| `string.h` | String manipulation (not heavily used but available)            |

---

### Lines 7-60: Token Definitions

```c
/* Token definitions for Bison compatibility */
#define START       256
#define END         257
#define BLOCK       258
...
```

**Explanation:**

- `#define` creates **symbolic constants** for token types
- Numbers start from **256** because ASCII values 0-255 are reserved for single characters
- When Bison parser is used, these tokens are passed between lexer and parser
- Each token has a **unique integer value**

**Example Token Definitions:**

| Token Name   | Value | Meaning             |
| ------------ | ----- | ------------------- |
| `START`      | 256   | Workflow start (▶️) |
| `END`        | 257   | Workflow end (⏹️)   |
| `LOOP`       | 263   | Loop statement (🔁) |
| `IF`         | 266   | Conditional (🤔)    |
| `NUMBER`     | 295   | Integer literal     |
| `IDENTIFIER` | 297   | Variable name       |

---

### Lines 62-64: Global Variables

```c
int line_num = 1;
int col_num = 1;
FILE *output_file;
```

| Variable      | Purpose                                    |
| ------------- | ------------------------------------------ |
| `line_num`    | Tracks current line number (starts at 1)   |
| `col_num`     | Tracks current column number (starts at 1) |
| `output_file` | File pointer for writing token output      |

**Why track line/column?**

- Error reporting (tells user WHERE the error occurred)
- Debugging (helps locate problems in source code)

---

### Lines 66-69: Helper Function

```c
void output_token(int token, const char* token_name, const char* lexeme) {
    fprintf(output_file, "%s %s\n", token_name, lexeme);
    printf("Line %d, Col %d: %-15s -> %s\n", line_num, col_num, lexeme, token_name);
}
```

**Explanation:**

- This function is called every time a token is recognized
- **Parameters:**
  - `token`: Integer token value (e.g., 256 for START)
  - `token_name`: String name (e.g., "START")
  - `lexeme`: Actual matched text (e.g., "▶️")

**What it does:**

1. Writes `TOKEN_NAME LEXEME` to output file (for Bison)
2. Prints formatted output to console (for debugging)

**Example output:**

```
Line 1, Col 1: ▶️              -> START
```

---

### Line 71: Closing Definition Block

```c
%}
```

**Explanation:** Closes the definitions section. After this, we can define options and macros.

---

## 3. Section 2: Options and Macros

### Lines 73-74: Flex Options

```c
%option noyywrap
%option yylineno
```

| Option     | Explanation                                                                                                                                         |
| ---------- | --------------------------------------------------------------------------------------------------------------------------------------------------- |
| `noyywrap` | Tells Flex we don't need `yywrap()` function. Normally Flex calls `yywrap()` at end of file to check if there's more input. We say "no, just stop." |
| `yylineno` | Flex automatically tracks line numbers in built-in variable `yylineno`                                                                              |

**Why `noyywrap`?**

- Without it, we'd need to define `yywrap()` function or link with `-lfl` library
- Simplifies our code

---

### Lines 76-79: UTF-8 Macro Definitions

```c
/* UTF-8 multibyte character patterns */
UTF8_CONT   [\x80-\xBF]
UTF8_2BYTE  [\xC2-\xDF]{UTF8_CONT}
UTF8_3BYTE  [\xE0-\xEF]{UTF8_CONT}{2}
UTF8_4BYTE  [\xF0-\xF4]{UTF8_CONT}{3}
```

**Explanation:**
These define patterns for matching UTF-8 encoded characters (emojis use UTF-8).

| Macro        | Bytes | Pattern                             | Example Characters |
| ------------ | ----- | ----------------------------------- | ------------------ |
| `UTF8_CONT`  | 1     | Continuation byte (10xxxxxx)        | -                  |
| `UTF8_2BYTE` | 2     | 110xxxxx 10xxxxxx                   | é, ñ, ü            |
| `UTF8_3BYTE` | 3     | 1110xxxx 10xxxxxx 10xxxxxx          | ▶, ⏹               |
| `UTF8_4BYTE` | 4     | 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx | 🔁, 📝, 🤔         |

---

## 4. Section 3: Rules Section

### Line 81: Start of Rules

```c
%%
```

**Explanation:** This marks the beginning of the **rules section**. Each rule has the format:

```
PATTERN    { ACTION }
```

---

### Understanding a Rule (Example)

```c
"\xE2\x96\xB6\xEF\xB8\x8F"      { output_token(START, "START", yytext); col_num += 2; return START; }
```

**Breaking it down:**

| Part                         | Explanation                                        |
| ---------------------------- | -------------------------------------------------- |
| `"\xE2\x96\xB6\xEF\xB8\x8F"` | **Pattern:** UTF-8 bytes for ▶️ emoji              |
| `{ ... }`                    | **Action:** C code to execute when pattern matches |
| `output_token(...)`          | Call our helper function to log the token          |
| `col_num += 2`               | Update column position (emoji = 2 visual columns)  |
| `return START`               | Return token type to caller (parser)               |
| `yytext`                     | Built-in Flex variable containing matched text     |

---

### How Emojis Are Converted to Hex Bytes

**Example: ▶️ (Play Button)**

1. Unicode code point: U+25B6 (▶) + U+FE0F (variation selector)
2. UTF-8 encoding:
   - U+25B6 → `E2 96 B6` (3 bytes)
   - U+FE0F → `EF B8 8F` (3 bytes)
3. Combined: `\xE2\x96\xB6\xEF\xB8\x8F`

---

### All Token Rules Explained

#### Program Structure Tokens

| Rule                         | Emoji | Token | Explanation           |
| ---------------------------- | ----- | ----- | --------------------- |
| `"\xE2\x96\xB6\xEF\xB8\x8F"` | ▶️    | START | Workflow start marker |
| `"\xE2\x8F\xB9..."`          | ⏹️    | END   | Workflow end marker   |
| `"\xF0\x9F\xA7\xB1"`         | 🧱    | BLOCK | Code block marker     |
| `";"`                        | ;     | SEMI  | Statement terminator  |

#### Task & Action Tokens

| Rule                 | Emoji | Token | Explanation      |
| -------------------- | ----- | ----- | ---------------- |
| `"\xE2\x9A\x99..."`  | ⚙️    | TASK  | Define a task    |
| `"\xE2\x96\xB6"`     | ▶     | RUN   | Execute/run task |
| `"\xE2\x8F\xB3"`     | ⏳    | WAIT  | Delay/wait       |
| `"\xF0\x9F\x94\x81"` | 🔁    | LOOP  | Loop statement   |
| `"\xF0\x9F\x94\x9A"` | 🔚    | STOP  | Stop execution   |
| `"\xE2\x99\xBB..."`  | ♻️    | RETRY | Retry on failure |

#### Conditional & Logic Tokens

| Rule                             | Emoji | Token | Explanation  |
| -------------------------------- | ----- | ----- | ------------ |
| `"\xF0\x9F\xA4\x94"`             | 🤔    | IF    | If condition |
| `"\xF0\x9F\x94\x81\xE2\x9D\x93"` | 🔁❓  | ELSE  | Else branch  |
| `"\xF0\x9F\xA4\x9D"`             | 🤝    | AND   | Logical AND  |
| `"\xF0\x9F\x94\x80"`             | 🔀    | OR    | Logical OR   |
| `"\xF0\x9F\x9A\xAB"`             | 🚫    | NOT   | Logical NOT  |
| `"\xF0\x9F\x94\xBC"`             | 🔼    | GT    | Greater than |
| `"\xF0\x9F\x94\xBD"`             | 🔽    | LT    | Less than    |
| `"\xF0\x9F\x9F\xB0"`             | 🟰    | EQ    | Equal to     |

#### I/O Tokens

| Rule                 | Emoji | Token  | Explanation       |
| -------------------- | ----- | ------ | ----------------- |
| `"\xF0\x9F\x93\xA5"` | 📥    | INPUT  | Read input        |
| `"\xF0\x9F\x93\xA4"` | 📤    | OUTPUT | Print output      |
| `"\xF0\x9F\x93\x9D"` | 📝    | LOG    | Log message       |
| `"\xF0\x9F\x94\x94"` | 🔔    | ALERT  | Show notification |

#### File Operation Tokens

| Rule                 | Emoji | Token  | Explanation    |
| -------------------- | ----- | ------ | -------------- |
| `"\xF0\x9F\x93\x82"` | 📂    | OPEN   | Open file      |
| `"\xF0\x9F\x93\x81"` | 📁    | CLOSE  | Close file     |
| `"\xF0\x9F\xA7\xBE"` | 🧾    | FILE   | File reference |
| `"\xE2\x9D\x8C"`     | ❌    | DELETE | Delete file    |
| `"\xE2\x9C\x8F..."`  | ✏️    | WRITE  | Write to file  |
| `"\xF0\x9F\x93\x84"` | 📄    | READ   | Read from file |

#### Variable & Type Tokens

| Rule                 | Emoji | Token       | Explanation          |
| -------------------- | ----- | ----------- | -------------------- |
| `"\xF0\x9F\x93\x9B"` | 📛    | ID          | Variable name marker |
| `"\xF0\x9F\x94\xA2"` | 🔢    | INT_TYPE    | Integer type         |
| `"\xF0\x9F\x94\xA4"` | 🔤    | STRING_TYPE | String type          |
| `"\xF0\x9F\x94\xA5"` | 🔥    | TRUE        | Boolean true         |
| `"\xE2\x9D\x84..."`  | ❄️    | FALSE       | Boolean false        |

---

### Standard Token Rules

```c
/* Numbers */
[0-9]+                          { output_token(NUMBER, "NUMBER", yytext); col_num += yyleng; return NUMBER; }
```

| Pattern  | Explanation                                |
| -------- | ------------------------------------------ |
| `[0-9]+` | One or more digits (0-9)                   |
| `yyleng` | Built-in variable = length of matched text |

**Examples matched:** `42`, `100`, `2107055`

---

```c
/* String literals */
\"[^\"]*\"                      { output_token(STRING_LIT, "STRING", yytext); col_num += yyleng; return STRING_LIT; }
```

| Pattern Part | Explanation                                 |
| ------------ | ------------------------------------------- |
| `\"`         | Opening double quote                        |
| `[^\"]* `    | Zero or more characters that are NOT quotes |
| `\"`         | Closing double quote                        |

**Examples matched:** `"Hello"`, `"EMOJI-FLOW"`, `""`

---

```c
/* Identifiers */
[a-zA-Z_][a-zA-Z0-9_]*          { output_token(IDENTIFIER, "IDENTIFIER", yytext); col_num += yyleng; return IDENTIFIER; }
```

| Pattern Part    | Explanation                                |
| --------------- | ------------------------------------------ |
| `[a-zA-Z_]`     | First character: letter or underscore      |
| `[a-zA-Z0-9_]*` | Following: letters, digits, or underscores |

**Examples matched:** `counter`, `myVar`, `build_status`, `_temp`

---

### Whitespace and Special Handling

```c
/* Whitespace handling */
[ \t]+                          { col_num += yyleng; /* Skip whitespace */ }
\n                              { line_num++; col_num = 1; /* Track newlines */ }
\r                              { /* Ignore carriage return */ }
```

| Pattern  | Action                                             |
| -------- | -------------------------------------------------- |
| `[ \t]+` | Spaces and tabs: update column, don't return token |
| `\n`     | Newline: increment line, reset column to 1         |
| `\r`     | Carriage return: ignore (Windows line endings)     |

---

### Comment Handling

```c
/* Comments: 💬 text until newline */
"\xF0\x9F\x92\xAC"[^\n]*        { /* Skip comments */ }
```

| Pattern Part         | Explanation               |
| -------------------- | ------------------------- |
| `"\xF0\x9F\x92\xAC"` | 💬 emoji (comment marker) |
| `[^\n]*`             | Everything until newline  |

**Example:** `💬 This is a comment` → Ignored completely

---

### Error Handling

```c
.                               {
                                    fprintf(stderr, "Warning: Unknown character '%s' at line %d, col %d\n",
                                            yytext, line_num, col_num);
                                    col_num += 1;
                                }
```

| Pattern | Explanation                                                |
| ------- | ---------------------------------------------------------- |
| `.`     | Matches ANY single character not matched by previous rules |

**Purpose:** Catch unrecognized characters and report warnings

---

## 5. Section 4: User Code Section

### Lines 255-311: Main Function

```c
int main(int argc, char **argv) {
```

| Parameter | Meaning                                           |
| --------- | ------------------------------------------------- |
| `argc`    | Argument count (number of command-line arguments) |
| `argv`    | Argument vector (array of argument strings)       |

---

### Command-Line Argument Processing

```c
if (argc < 2) {
    fprintf(stderr, "Usage: %s <input_file> [output_file]\n", argv[0]);
    return 1;
}
```

**Explanation:**

- Check if user provided input file
- If not, print usage message and exit with error code 1
- `argv[0]` = program name, `argv[1]` = input file, `argv[2]` = output file

---

### File Opening

```c
FILE *input_file = fopen(argv[1], "r");
if (!input_file) {
    fprintf(stderr, "Error: Cannot open input file '%s'\n", argv[1]);
    return 1;
}
```

| Function  | Parameters     | Purpose                     |
| --------- | -------------- | --------------------------- |
| `fopen()` | filename, mode | Open file for reading ("r") |

**Error checking:** If file doesn't exist, report error and exit.

---

### Setting Up Flex Input

```c
yyin = input_file;
```

| Variable | Purpose                                         |
| -------- | ----------------------------------------------- |
| `yyin`   | Built-in Flex variable - input source for lexer |

**Default:** `yyin = stdin` (keyboard input)
**We change it to:** our input file

---

### The Tokenization Loop

```c
while (yylex());
```

| Function  | Returns                                 |
| --------- | --------------------------------------- |
| `yylex()` | Token value (256+), or 0 at end of file |

**How it works:**

1. `yylex()` scans input and returns next token
2. Loop continues until `yylex()` returns 0 (EOF)
3. Each call matches one pattern and executes its action

---

## 6. How Emojis Are Matched (UTF-8 Encoding)

### The UTF-8 Encoding Process

```
Emoji → Unicode Code Point → UTF-8 Bytes → Flex Pattern
```

### Example: 🔁 (LOOP)

1. **Emoji:** 🔁
2. **Unicode:** U+1F501
3. **Binary:** 0001 1111 0101 0000 0001
4. **UTF-8 Encoding:**
   - Needs 4 bytes (code point > U+FFFF)
   - Pattern: 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
   - Result: F0 9F 94 81
5. **Flex Pattern:** `"\xF0\x9F\x94\x81"`

### Quick Reference: Byte Patterns

| Code Point Range   | UTF-8 Bytes | Example    |
| ------------------ | ----------- | ---------- |
| U+0000 - U+007F    | 1 byte      | A, z, 1    |
| U+0080 - U+07FF    | 2 bytes     | é, ñ       |
| U+0800 - U+FFFF    | 3 bytes     | ▶, ⏹, ❌   |
| U+10000 - U+10FFFF | 4 bytes     | 🔁, 📝, 🤔 |

---

## 7. Token Flow Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    EMOJI-FLOW LEXER                          │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│   INPUT FILE                 LEXER                  OUTPUT   │
│   (emoji code)             (Flex)                 (tokens)   │
│                                                              │
│   ▶️              ──────►  Pattern Match  ──────►  START     │
│   📝              ──────►  Pattern Match  ──────►  LOG       │
│   "Hello"         ──────►  Pattern Match  ──────►  STRING    │
│   ;               ──────►  Pattern Match  ──────►  SEMI      │
│   ⏹️              ──────►  Pattern Match  ──────►  END       │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 8. Viva Questions & Answers

### Q1: What is Flex?

**A:** Flex (Fast Lexical Analyzer Generator) is a tool that generates lexical analyzers (scanners) from a specification file. It converts patterns (regular expressions) into C code that can tokenize input.

### Q2: What are the three sections of a Flex file?

**A:**

1. **Definitions Section** (`%{ ... %}`) - C code, includes, macros
2. **Rules Section** (`%% ... %%`) - Patterns and actions
3. **User Code Section** (after second `%%`) - main() and helper functions

### Q3: What is `yytext`?

**A:** `yytext` is a built-in Flex variable (char pointer) that holds the text matched by the current pattern.

### Q4: What is `yyleng`?

**A:** `yyleng` is a built-in Flex variable (int) that holds the length of the matched text.

### Q5: What is `yyin`?

**A:** `yyin` is a built-in Flex variable (FILE pointer) that specifies the input source. Default is stdin.

### Q6: Why do token values start from 256?

**A:** Values 0-255 are reserved for ASCII characters. Starting from 256 ensures no conflict with single-character tokens.

### Q7: What does `%option noyywrap` do?

**A:** It tells Flex we don't need the `yywrap()` function, which is normally called at EOF to check for additional input files.

### Q8: How are emojis represented in the lexer?

**A:** Emojis are represented as UTF-8 byte sequences in hexadecimal format. For example, 🔁 is `\xF0\x9F\x94\x81`.

### Q9: What happens when no pattern matches?

**A:** The `.` rule (dot) matches any unrecognized character and reports a warning.

### Q10: What is the purpose of the lexer in a compiler?

**A:** The lexer (lexical analyzer) is the first phase of a compiler. It reads source code character by character and groups them into meaningful tokens for the parser.

### Q11: How does the lexer handle whitespace?

**A:** Whitespace (spaces and tabs) is matched by `[ \t]+` pattern, which updates the column counter but doesn't return a token (effectively skipping it).

### Q12: What is Bison and how does it relate to Flex?

**A:** Bison is a parser generator that works with Flex. Flex produces tokens, and Bison uses grammar rules to parse those tokens into an Abstract Syntax Tree (AST).

---

## Summary

The EMOJI-FLOW lexer:

1. **Reads** emoji-based source code
2. **Matches** patterns using UTF-8 byte sequences
3. **Produces** tokens for the parser
4. **Tracks** line and column numbers
5. **Handles** comments, whitespace, and errors

**Key Flex Variables:**

- `yytext` - matched text
- `yyleng` - length of match
- `yyin` - input file
- `yylineno` - current line number

**Key Functions:**

- `yylex()` - get next token
- `output_token()` - our helper to log tokens
