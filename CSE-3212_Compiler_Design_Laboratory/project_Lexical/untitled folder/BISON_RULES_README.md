# EMOJI-FLOW Bison Rules Guide (Beginner Version)

This document is intentionally outside your main repo folder.

Location:
- /Users/arkabrajaprasadnath/3-2/3-2 labs/compiler/project/BISON_RULES_README.md

Parser source used for this guide:
- [CSE-3212-Compiler-Project/emoji_flow_parser.y](CSE-3212-Compiler-Project/emoji_flow_parser.y)

Compiler entry points:
- Lexer: [CSE-3212-Compiler-Project/emoji_flow_lexer.l](CSE-3212-Compiler-Project/emoji_flow_lexer.l)
- Parser + runtime: [CSE-3212-Compiler-Project/emoji_flow_parser.y](CSE-3212-Compiler-Project/emoji_flow_parser.y)
- Build: [CSE-3212-Compiler-Project/Makefile](CSE-3212-Compiler-Project/Makefile)

## 1) First, understand the process in simple words

When you run a `.emoji` file:

1. Lexer reads raw characters and emojis
2. Lexer converts them into tokens like `START`, `OUTPUT`, `LOOP`
3. Parser matches token sequence with grammar rules
4. Parser builds executable statement structures
5. Runtime executes statements and prints final output

So this is not only parsing now. It is parse + execute.

## 2) Quick emoji syntax list

Core syntax you actively use:

1. ▶️ : program start
2. ⏹️ : program end
3. 🧱 : block marker (must be followed by `{ ... }`)
4. 📛 : variable declaration marker
5. 🟰 : assignment/equality token
6. 📤 : print/output
7. 📥 : input integer
8. 🤔 : if
9. 🔁❓ : else
10. 🔁 : loop
11. 🔼 : greater-than
12. 🔽 : less-than
13. 🤝 : logical AND
14. 🔀 : logical OR
15. 🚫 : logical NOT
16. 🔥 : true
17. ❄️ : false
18. ⚙️ : task statement
19. + - * / : arithmetic operators
20. ; : statement end

## 3) Rule-by-rule Bison explanation

Below, each section follows this structure:

- Grammar rule
- What it means
- Example code
- What output/runtime behavior you get

### 3.1 Rule: program

Grammar:

```bison
program : START workflow_block END
```

Meaning:
- Every valid file must start with ▶️ and end with ⏹️.

Example:

```text
▶️
🧱 {
  📤 "Hi";
}
⏹️
```

Behavior:
- Parser accepts full program
- Runtime executes block statements inside

### 3.2 Rule: workflow_block

Grammar:

```bison
workflow_block : BLOCK LBRACE statement_list RBRACE
```

Meaning:
- A block always starts with 🧱 then `{ ... }`.

Example:

```text
🧱 {
  📤 "Inside block";
}
```

Behavior:
- Statements are grouped and executed in sequence

### 3.3 Rule: statement_list

Grammar:

```bison
statement_list
  : /* empty */
  | statement_list statement
```

Meaning:
- A block can have zero statements or many statements.

Example:

```text
🧱 {
  📤 "A";
  📤 "B";
}
```

Behavior:
- Runtime executes `A` then `B`

### 3.4 Rule: statement

Grammar (simplified):

```bison
statement
  : task_stmt SEMI
  | declaration_stmt SEMI
  | assignment_stmt SEMI
  | io_stmt SEMI
  | loop_stmt
  | if_stmt
```

Meaning:
- Most statements need `;`
- `if` and `loop` are block statements, so no extra `;` after whole block

### 3.5 Rule: task_stmt

Grammar:

```bison
task_stmt : TASK IDENTIFIER
```

Example:

```text
⚙️ backup;
```

Behavior:
- Accepted as valid task marker
- No visible output unless you print something separately

### 3.6 Rule: declaration_stmt

Grammar:

```bison
declaration_stmt : ID IDENTIFIER EQ expression
```

Example:

```text
📛 count 🟰 5;
```

Behavior:
- Creates/stores variable `count = 5`

### 3.7 Rule: assignment_stmt

Grammar:

```bison
assignment_stmt : IDENTIFIER EQ expression
```

Example:

```text
count 🟰 count - 1;
```

Behavior:
- Updates existing variable value

### 3.8 Rule: io_stmt

Grammar:

```bison
io_stmt
  : OUTPUT expression
  | INPUT IDENTIFIER
```

Examples:

```text
📤 "Hello";
📤 count;
📥 n;
```

Behavior:
- `📤` prints real output to terminal
- `📥` asks for an integer input and stores it

### 3.9 Rule: loop_stmt

Grammar:

```bison
loop_stmt : LOOP LPAREN condition RPAREN workflow_block
```

Example:

```text
🔁 (count 🔼 0) 🧱 {
  📤 count;
  count 🟰 count - 1;
}
```

Behavior:
- Repeats block while condition is true
- Includes loop guard in runtime to avoid infinite loop crash

### 3.10 Rule: if_stmt + optional_else

Grammar:

```bison
if_stmt : IF LPAREN condition RPAREN workflow_block optional_else
optional_else
  : /* empty */
  | ELSE workflow_block
```

Example:

```text
🤔 (x 🔼 5) 🧱 {
  📤 "big";
} 🔁❓ 🧱 {
  📤 "small";
}
```

Behavior:
- Executes one block depending on condition result

### 3.11 Rule: condition

Grammar (forms):

```bison
condition
  : expression GT expression
  | expression LT expression
  | expression EQ expression
  | TRUE_VAL
  | FALSE_VAL
  | NOT condition
  | condition AND condition
  | condition OR condition
  | LPAREN condition RPAREN
```

Meaning:
- Supports comparisons and boolean logic

Examples:

```text
x 🔼 10
x 🔽 3
x 🟰 y
🚫 (x 🔼 10)
(x 🔼 0) 🤝 (y 🔽 9)
```

Behavior:
- Produces true/false for `if` and `loop`

### 3.12 Rule: expression, term, factor

Grammar summary:

```bison
expression : expression PLUS term | expression MINUS term | term
term       : term MULTIPLY factor | term DIVIDE factor | factor
factor     : NUMBER | STRING_LIT | IDENTIFIER | LPAREN expression RPAREN
```

Meaning:
- Arithmetic precedence works naturally:
- `*` and `/` first
- then `+` and `-`

Examples:

```text
a + b * 2
(n - 1) / 2
```

Behavior:
- Evaluates numeric expressions for assignment/output/conditions

## 4) If code is this, output will be this

### Example A: simple print

Code:

```text
▶️
🧱 {
  📤 "Hello EMOJI-FLOW";
}
⏹️
```

Output:

```text
Hello EMOJI-FLOW
```

### Example B: if/else

Code:

```text
▶️
🧱 {
  📛 x 🟰 7;
  🤔 (x 🔼 5) 🧱 {
    📤 "x is greater than 5";
  } 🔁❓ 🧱 {
    📤 "x is not greater than 5";
  }
}
⏹️
```

Output:

```text
x is greater than 5
```

### Example C: countdown loop

Code:

```text
▶️
🧱 {
  📛 i 🟰 5;
  🔁 (i 🔼 0) 🧱 {
    📤 i;
    i 🟰 i - 1;
  }
  📤 "Loop finished";
}
⏹️
```

Output:

```text
5
4
3
2
1
Loop finished
```

### Example D: fibonacci-like sample

Code file:
- [CSE-3212-Compiler-Project/tests/04_fibonacci_like.emoji](CSE-3212-Compiler-Project/tests/04_fibonacci_like.emoji)

Output:

```text
Fibonacci-like sequence:
0
1
1
2
3
5
8
13
```

## 5) Exact command usage

From [CSE-3212-Compiler-Project](CSE-3212-Compiler-Project):

1. Build
```bash
make
```

2. Run default
```bash
make run
```

3. Run specific file
```bash
make run FILE=tests/02_if_else.emoji
```

4. Run all tests
```bash
make test
```

5. Direct run (without make helper)
```bash
./emoji_flow_compiler tests/03_counter_loop.emoji
```

## 6) Most common errors (noob section)

1. Missing semicolon `;`
- Symptom: syntax error near end of line
- Fix: add `;` after simple statements

2. Missing 🧱 before `{`
- Symptom: parser rejects block start
- Fix: always write `🧱 { ... }`

3. Wrong make command format
- Wrong: `make run ./emoji_flow_compiler tests/01_minimal.emoji`
- Right: `make run FILE=tests/01_minimal.emoji`

4. Wrong emoji variant
- Symptom: token not recognized
- Fix: check UTF mapping in [CSE-3212-Compiler-Project/emoji_converter_data/output_emoji_unicode.txt](CSE-3212-Compiler-Project/emoji_converter_data/output_emoji_unicode.txt)

5. Runtime input issue
- `📥` expects integer currently
- Enter numbers like `10`, not text words

## 7) Final takeaway

If you remember only 3 things:

1. Program shell is always `▶️ ... 🧱 { ... } ... ⏹️`
2. `📤` gives real output now
3. Grammar rules decide what is legal; runtime decides what gets printed

## 8) Deep Dive: The Big C Block In parser.y (Why Needed + How It Works)

Reference source:
- [CSE-3212-Compiler-Project/emoji_flow_parser.y](CSE-3212-Compiler-Project/emoji_flow_parser.y)

You shared the large top section from `parser.y` (before grammar rules). That section is the runtime engine of your compiler. Without it, Bison can only validate syntax but cannot execute statements.

### 8.1 Includes and external declarations

Code group:

```c
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int yylex(void);
void yyerror(const char *msg);
extern int line_num;
extern int col_num;
extern FILE *yyin;
extern FILE *output_file;
```

Why needed:

1. `stdio.h`: print output and errors
2. `stdlib.h`: memory allocation and exit
3. `string.h`: string compare/copy
4. `yylex`: parser asks lexer for next token
5. `yyerror`: custom syntax error printing
6. `extern` vars: share line/column/input stream with lexer

How it works:

1. Lexer and parser are separate generated C files
2. `extern` lets parser access lexer state
3. Parser can show clear errors with exact position

### 8.2 Debug flag

Code:

```c
int debug_tokens = 0;
```

Why needed:

1. Normal users want clean program output
2. Developers sometimes need token traces to debug lexer

How it works:

1. Default is off, so only runtime output prints
2. `--trace-tokens` turns token logging on

### 8.3 Runtime value model (`ValueType`, `Value`)

Code idea:

```c
typedef enum { VAL_NUM, VAL_STR, VAL_BOOL } ValueType;
typedef struct { ValueType type; int num; char *str; int boolean; } Value;
```

Why needed:

1. Expressions can produce number, string, or boolean
2. Single runtime container is needed for all expression results

How it works:

1. `type` tells what data is active
2. Runtime checks type before arithmetic/condition logic

### 8.4 AST node types (`Expr`, `Cond`, `Stmt`, `StmtList`)

Why needed:

1. Parser reads tokens one by one; execution needs structured program form
2. AST stores program logic in tree/list form

How it works:

1. `Expr`: arithmetic/string/variable expressions
2. `Cond`: comparison/boolean conditions
3. `Stmt`: one executable statement
4. `StmtList`: linked list of statements inside block

### 8.5 Statement kinds (`StmtType`)

Code idea:

```c
ST_TASK, ST_DECL, ST_ASSIGN, ST_OUTPUT, ST_INPUT, ST_LOOP, ST_IF
```

Why needed:

1. Runtime must know which statement behavior to execute

How it works:

1. Parser creates `Stmt` with one of these types
2. `exec_stmt` switches on type and runs corresponding logic

### 8.6 Variable table (`Variable`, `g_vars`, limits)

Code idea:

```c
typedef struct { char *name; Value value; } Variable;
static Variable g_vars[MAX_VARS];
```

Why needed:

1. Variable declarations and assignments need storage
2. Runtime must remember values across statements

How it works:

1. `set_var` creates/updates variable
2. `get_var` fetches variable by name
3. `find_var` searches table linearly

### 8.7 Memory/helper creators (`mk_expr`, `mk_cond_*`, `mk_stmt`)

Why needed:

1. Grammar actions repeatedly construct AST nodes
2. Helper functions keep code short and safe

How it works:

1. Each helper allocates one node
2. Sets fields based on grammar action arguments
3. Returns node pointer used by higher-level rule

### 8.8 Expression evaluator (`eval_expr`)

Why needed:

1. `📤 x + 2`, `a * b`, and comparisons require computed values

How it works:

1. Recursively evaluates child nodes first
2. Applies operation (`+`, `-`, `*`, `/`)
3. Guards division by zero
4. Returns `Value`

### 8.9 Condition evaluator (`eval_cond`)

Why needed:

1. `if` and `loop` need true/false decision

How it works:

1. Evaluates expression comparisons (`GT`, `LT`, `EQ`)
2. Handles boolean literals and logical ops (`NOT`, `AND`, `OR`)

### 8.10 Executor (`exec_stmt`, `exec_stmt_list`)

Why needed:

1. Parser alone does not run program
2. Executor turns parsed AST into behavior

How it works:

1. For `ST_OUTPUT`: prints real output
2. For `ST_DECL`/`ST_ASSIGN`: updates variable table
3. For `ST_INPUT`: reads integer from stdin
4. For `ST_LOOP`: repeats while condition true
5. For `ST_IF`: executes then/else block

### 8.11 Loop guard (`LOOP_GUARD_LIMIT`)

Why needed:

1. Infinite loop can hang program forever

How it works:

1. Each loop iteration increments guard counter
2. Exits with runtime error if limit exceeded

### 8.12 `%union` and typed tokens

Code idea:

```bison
%union { int ival; char *sval; Expr *expr; Cond *cond; Stmt *stmt; StmtList *stmt_list; }
%token <ival> NUMBER
%token <sval> STRING_LIT IDENTIFIER
```

Why needed:

1. Parser needs to carry values, not just token names

How it works:

1. Lexer writes into `yylval`
2. Grammar rules receive typed values (`$1`, `$2`, ...)
3. Actions build AST/runtime structures from those values

### 8.13 Simple mental model

Use this model while reading parser code:

1. Declarations define data structures
2. Grammar rules build AST nodes
3. `yyparse()` creates full program tree
4. `exec_stmt_list(g_program)` executes the tree

That is why this big C section is needed: it converts a parser into a real executable compiler/interpreter behavior.
