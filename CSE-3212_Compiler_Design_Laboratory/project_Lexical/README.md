# EMOJI-FLOW Compiler (Built From Scratch)

This project is an emoji-based compiler front-end built with Flex + Bison + GCC on Windows.

Current pipeline:

1. Lexical analysis (tokenization).
2. Syntax analysis (grammar parsing).
3. AST generation (`parse_tree.txt`).
4. Strict semantic checking with hard errors.
5. Semantic execution trace (only when semantic checks pass).

---

## 1) How It Was Built From Scratch

### Step 1: Start from lexer-only processing

The initial stage used `emoji_flow.l` to scan emoji source code and emit tokens.

Key outputs at this stage:

1. Token stream in `output/*_tokens.txt`.
2. Console trace showing token/line/column.

### Step 2: Add parser integration

`emoji_flow.y` was added and connected to the lexer.

Work done:

1. Define `%token` declarations that match lexer return values.
2. Include generated parser header in lexer (`emoji_flow.tab.h`).
3. Remove standalone lexer `main()` so parser `main()` controls full flow.

### Step 3: Build full toolchain on Windows

`build.ps1` was upgraded to run the complete compilation pipeline:

1. Bison generates `emoji_flow.tab.c` + `emoji_flow.tab.h`.
2. Flex generates `lex.yy.c`.
3. GCC compiles both into `emoji_parser.exe`.

WinFlexBison tool resolution was stabilized in the script to avoid legacy `flex` path issues.

### Step 4: Replace token-stream parser with real grammar

The parser was upgraded to real grammar rules for:

1. Declarations and assignments.
2. Arithmetic/logical expressions.
3. `if/else`, loops, and blocks.
4. Task definition/invocation.
5. File operation statements.

### Step 5: Build AST and parse-tree output

AST node construction was added inside grammar actions.

Result:

1. Parse tree is exported to `parse_tree.txt`.
2. Tree includes node categories (`DECL`, `ASSIGN`, `IF`, `LOOP`, `TASK_DEF`, `BINOP`, etc.).

### Step 6: Add semantic execution trace

After successful parse, statement execution was added to show behavior:

1. Variable updates (`[SET]`).
2. Conditional and loop behavior.
3. Task registration and calls.
4. File operation traces.

### Step 7: Upgrade to strict semantic checking (latest)

Semantic layer now enforces hard rules instead of warnings.

Implemented checks:

1. Undeclared variable use is an error.
2. Assignment to undeclared variable is an error.
3. Redeclaration of variable/task is an error.
4. Type mismatch on declaration/assignment is an error.
5. Invalid operand types in expressions are errors.
6. Invalid condition/loop/wait operand types are errors.
7. Undefined task call is an error.
8. Division by zero is an error.

If semantic errors occur:

1. Execution trace stops.
2. Program exits with non-zero status.
3. Token file includes `# Semantic result: FAILED (...)`.

---

## 2) Project File Roles

### `emoji_flow.l`

1. Converts emoji lexemes into parser tokens.
2. Populates semantic values (`NUMBER`, `STRING_LIT`, `IDENTIFIER`) via `yylval`.
3. Writes token output for each run.

### `emoji_flow.y`

1. Defines full grammar and precedence.
2. Builds AST nodes during parse actions.
3. Performs strict semantic checking and execution trace.
4. Writes parse tree to `parse_tree.txt`.

### `build.ps1`

1. `build`: regenerate parser/lexer and compile executable.
2. `generate`: run executable on all sample files.
3. `all`: do `build` + `generate`.
4. `clean`: remove generated artifacts.

---

## 3) End-to-End Processing Flow

1. Read source file from `samples/*.emoji`.
2. Lexer tokenizes and emits token stream to output file.
3. Parser validates grammar and builds AST.
4. Parse tree is written to `parse_tree.txt`.
5. Semantic phase validates declarations/types/operations.
6. If no semantic errors: execution trace is printed.
7. If semantic errors exist: stop and return failure.

---

## 4) Commands

Build compiler only:

```powershell
powershell -ExecutionPolicy Bypass -File .\build.ps1 build
```

Build and run all sample programs:

```powershell
powershell -ExecutionPolicy Bypass -File .\build.ps1 all
```

Run one file manually:

```powershell
.\emoji_parser.exe .\samples\advanced_operations.emoji .\output\advanced_operations_tokens.txt
```

Regenerate outputs without rebuilding:

```powershell
powershell -ExecutionPolicy Bypass -File .\build.ps1 generate
```

Clean generated files:

```powershell
powershell -ExecutionPolicy Bypass -File .\build.ps1 clean
```

---

## 5) Generated Outputs

1. `emoji_parser.exe` - compiled front-end executable.
2. `emoji_flow.tab.c`, `emoji_flow.tab.h` - Bison-generated parser artifacts.
3. `lex.yy.c` - Flex-generated scanner artifact.
4. `output/*_tokens.txt` - token stream + parse/semantic result markers.
5. `parse_tree.txt` - AST dump for the latest run.

---

## 6) Current Scope

Implemented now:

1. Full front-end compiler pipeline with strict semantics.
2. AST-based parse tree output.
3. Interpreter-style execution trace.

Not implemented yet:

1. Machine code or bytecode generation.
2. Optimizer passes.
3. Full runtime system.
