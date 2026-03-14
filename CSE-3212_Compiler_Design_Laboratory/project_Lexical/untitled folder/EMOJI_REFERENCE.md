# EMOJI-FLOW Beginner Documentation

This guide explains EMOJI-FLOW from zero, in simple steps.

Goal of this document:

1. Explain how your compiler works internally
2. Show exact emoji syntax you can write
3. Give a complete emoji meaning list
4. Help you debug errors quickly

Primary project files:

1. [CSE-3212-Compiler-Project/emoji_flow_lexer.l](CSE-3212-Compiler-Project/emoji_flow_lexer.l)
2. [CSE-3212-Compiler-Project/emoji_flow_parser.y](CSE-3212-Compiler-Project/emoji_flow_parser.y)
3. [CSE-3212-Compiler-Project/Makefile](CSE-3212-Compiler-Project/Makefile)
4. [CSE-3212-Compiler-Project/tests](CSE-3212-Compiler-Project/tests)
5. [CSE-3212-Compiler-Project/emoji_converter_data](CSE-3212-Compiler-Project/emoji_converter_data)

## 1) Big Picture: How The Compiler Works

Think of the flow like this:

1. You write `.emoji` source code
2. Flex lexer reads characters and turns them into tokens
3. Bison parser checks grammar and builds executable structures
4. Runtime executes statements (print, assign, if, loop, input)
5. Output appears in terminal

In one line:

Source text -> Tokens -> Parsed structure -> Execution -> Output

## 2) What Flex (Lexer) Does

File: [CSE-3212-Compiler-Project/emoji_flow_lexer.l](CSE-3212-Compiler-Project/emoji_flow_lexer.l)

Lexer job:

1. Match emoji byte patterns such as `"\xF0\x9F\x93\xA4"` for 📤
2. Match normal tokens like numbers, identifiers, operators
3. Return token names to parser (`OUTPUT`, `LOOP`, `IF`, etc.)
4. Pass values for tokens like numbers/strings/identifiers
5. Skip spaces/newlines/comments

Comments supported in source code:

1. `# this is comment`
2. `💬 this is comment`

## 3) What Bison (Parser + Runtime) Does

File: [CSE-3212-Compiler-Project/emoji_flow_parser.y](CSE-3212-Compiler-Project/emoji_flow_parser.y)

Parser/runtime job:

1. Enforce grammar rules (valid EMOJI-FLOW syntax)
2. Build internal structures for statements and expressions
3. Execute your program
4. Print real runtime output for 📤
5. Report syntax errors with line/column via `yyerror`

This means now you get actual output (for example: `x is greater than 5`) instead of only parser trace logs.

## 4) Build And Run Commands

From [CSE-3212-Compiler-Project](CSE-3212-Compiler-Project):

1. Build:
```bash
make
```

2. Run default test:
```bash
make run
```

3. Run a specific file:
```bash
make run FILE=tests/02_if_else.emoji
```

4. Run all tests:
```bash
make test
```

5. Clean generated files:
```bash
make clean
```

## 5) Core Program Structure (Must Follow)

Every program shape:

```text
▶️
🧱 {
  ... statements ...
}
⏹️
```

Rules:

1. Start with ▶️
2. Use 🧱 before `{ ... }` blocks
3. End with ⏹️
4. End simple statements with `;`

## 6) Complete Syntax You Can Write

### 6.1 Variable declaration

```text
📛 variableName 🟰 expression;
```

Example:

```text
📛 count 🟰 5;
```

### 6.2 Assignment

```text
variableName 🟰 expression;
```

Example:

```text
count 🟰 count - 1;
```

### 6.3 Print/output

```text
📤 expression;
```

Examples:

```text
📤 "Hello";
📤 count;
```

### 6.4 Input

```text
📥 variableName;
```

Example:

```text
📥 n;
```

### 6.5 Task statement

```text
⚙️ taskName;
```

Example:

```text
⚙️ backup;
```

### 6.6 If / else

```text
🤔 (condition) 🧱 {
  ...
} 🔁❓ 🧱 {
  ...
}
```

Else block is optional.

### 6.7 Loop

```text
🔁 (condition) 🧱 {
  ...
}
```

### 6.8 Conditions

Supported condition forms:

1. `expression 🔼 expression`
2. `expression 🔽 expression`
3. `expression 🟰 expression`
4. `🔥`
5. `❄️`
6. `🚫 condition`
7. `condition 🤝 condition`
8. `condition 🔀 condition`
9. `(condition)`

### 6.9 Expressions

Supported expression forms:

1. `number`
2. `"string"`
3. `identifier`
4. `(expression)`
5. `expression + expression`
6. `expression - expression`
7. `expression * expression`
8. `expression / expression`

## 7) Complete Emoji Meaning List

## Core syntax emojis (actively used by grammar)

1. ▶️ : program start
2. ⏹️ : program end
3. 🧱 : block marker
4. ⚙️ : task statement
5. 📛 : variable declaration marker
6. 🟰 : assignment/equality token
7. 🤔 : if
8. 🔁❓ : else
9. 🔁 : loop
10. 📤 : output/print
11. 📥 : input
12. 🔼 : greater-than
13. 🔽 : less-than
14. 🤝 : logical AND
15. 🔀 : logical OR
16. 🚫 : logical NOT
17. 🔥 : true
18. ❄️ : false

## Extra lexer emojis (recognized by lexer, mostly reserved for extension)

1. ⏳ : WAIT
2. 🔚 : STOP
3. ♻️ / ♻ : RETRY
4. 📝 : LOG
5. 🔔 : ALERT
6. 📂 : OPEN
7. 📁 : CLOSE
8. 🧾 : FILE
9. ❌ : DELETE
10. ✏️ / ✏ : WRITE
11. 📄 : READ
12. ⏰ : TIME
13. 🕒 : SCHEDULE
14. ⏭️ / ⏭ : NEXT
15. ⏮️ / ⏮ : PREV
16. 🔢 : INT_TYPE
17. 🔤 : STRING_TYPE
18. ⚠️ / ⚠ : ERROR token marker

## 8) Full Example Program (Noob Friendly)

```text
▶️
🧱 {
  # declare variables
  📛 n 🟰 5;
  📛 fact 🟰 1;

  # factorial loop
  🔁 (n 🔼 0) 🧱 {
    fact 🟰 fact * n;
    n 🟰 n - 1;
  }

  # print result
  📤 fact;
}
⏹️
```

Expected output:

```text
120
```

## 9) Unicode/Hex Conversion Workflow

Folder: [CSE-3212-Compiler-Project/emoji_converter_data](CSE-3212-Compiler-Project/emoji_converter_data)

Files:

1. [CSE-3212-Compiler-Project/emoji_converter_data/input_all_lexer_emoji.txt](CSE-3212-Compiler-Project/emoji_converter_data/input_all_lexer_emoji.txt)
2. [CSE-3212-Compiler-Project/emoji_converter_data/output_emoji_unicode.txt](CSE-3212-Compiler-Project/emoji_converter_data/output_emoji_unicode.txt)
3. [CSE-3212-Compiler-Project/emoji_converter_data/output_emoji_hex.txt](CSE-3212-Compiler-Project/emoji_converter_data/output_emoji_hex.txt)
4. [CSE-3212-Compiler-Project/emoji_converter_data/converter.py](CSE-3212-Compiler-Project/emoji_converter_data/converter.py)

Run converter:

```bash
python3 emoji_converter_data/converter.py \
  -i emoji_converter_data/input_all_lexer_emoji.txt \
  -o emoji_converter_data/output_emoji_unicode.txt \
  --hex-output emoji_converter_data/output_emoji_hex.txt
```

Why this matters:

1. Prevents mistakes when writing UTF-8 escapes in lexer rules
2. Helps debug emoji mismatch issues
3. Helps distinguish similar VS16/non-VS16 forms

## 10) Common Mistakes And Fixes

1. Missing `;`
Fix: Add semicolon at end of statement.

2. Missing `🧱` before `{` block
Fix: Always write `🧱 { ... }`.

3. Wrong run command format
Fix: Use `make run FILE=tests/02_if_else.emoji`, not `make run ./emoji_flow_compiler ...`.

4. Emoji not recognized
Fix: Check UTF bytes in converter output and compare with lexer pattern.

5. Runtime prints unexpected value
Fix: Print intermediate variables with `📤 varName;` to trace logic.
