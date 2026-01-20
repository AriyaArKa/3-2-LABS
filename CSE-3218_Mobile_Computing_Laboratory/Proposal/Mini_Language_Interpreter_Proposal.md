# Compiler Project Proposal: Mini Programming Language Interpreter

## Executive Summary
This project implements a complete compiler toolchain for a custom mini programming language using **Flex** (lexical analyzer) and **Bison** (syntax analyzer). The language supports variables, functions, control flow, and expression evaluation, demonstrating all major phases of compiler design.

---

## Project Overview

### Objective
Build an interpreter for a statically-typed mini language that can parse and execute programs with proper scoping and error handling.

### Language Name
**SimpleScript** - A simple yet feature-rich imperative language

---

## Core Language Features

### 1. **Data Types**
- `int`, `float`, `bool`, `string`
- Dynamic type conversion with error handling

### 2. **Variables & Declarations**
```
int x = 10;
float y = 3.14;
string name = "Hello";
bool flag = true;
```

### 3. **Operators**
- Arithmetic: `+`, `-`, `*`, `/`, `%`
- Comparison: `==`, `!=`, `<`, `>`, `<=`, `>=`
- Logical: `&&`, `||`, `!`
- Assignment: `=`, `+=`, `-=`, `*=`, `/=`

### 4. **Control Flow**
```
if (condition) { ... }
else if (condition) { ... }
else { ... }

while (condition) { ... }
for (int i = 0; i < 10; i++) { ... }

break;
continue;
```

### 5. **Functions**
```
int add(int a, int b) {
    return a + b;
}

void printValue(int x) {
    print(x);
}
```

### 6. **Built-in Functions**
- `print(value)` - Output to console
- `input()` - Read from user
- `len(string)` - String length
- `sqrt(number)` - Square root

---

## Technical Architecture

### Phase 1: Lexical Analysis (Flex)
**File:** `lexer.l`

**Tokens to recognize:**
- Keywords: `int`, `float`, `bool`, `string`, `if`, `else`, `while`, `for`, `function`, `return`, `break`, `continue`, `print`, `input`
- Identifiers: `[a-zA-Z_][a-zA-Z0-9_]*`
- Numbers: integers and floating-point
- Strings: quoted literals
- Operators: `+`, `-`, `*`, `/`, `%`, `==`, `!=`, `<`, `>`, `<=`, `>=`, `&&`, `||`, `!`, `=`, `+=`, `-=`, etc.
- Delimiters: `{`, `}`, `(`, `)`, `;`, `,`
- Whitespace and comments (single-line: `//`, multi-line: `/* */`)

### Phase 2: Syntax Analysis (Bison)
**File:** `parser.y`

**Grammar rules:**
- Program → Declarations Functions Statements
- Function → Type ID ( Parameters ) { Statements }
- Statement → Declaration | Assignment | IfStatement | WhileLoop | ForLoop | FunctionCall | ReturnStatement | Block
- Expression → BinaryOp | UnaryOp | Literal | Variable | FunctionCall
- Precedence & Associativity rules for operators

### Phase 3: Semantic Analysis
**Features:**
- Symbol table management (variables, functions, scope)
- Type checking and compatibility
- Function parameter validation
- Scope management (local vs global)

### Phase 4: Execution
**Features:**
- Abstract Syntax Tree (AST) traversal
- Runtime value storage
- Function call stack
- Error handling and reporting

---

## Example Program

```
int fibonacci(int n) {
    if (n <= 1) return n;
    return fibonacci(n - 1) + fibonacci(n - 2);
}

int main() {
    int result = fibonacci(10);
    print("Fibonacci(10) = ");
    print(result);
    return 0;
}
```

---

## Deliverables

### 1. **Flex File** (`lexer.l`)
- Complete tokenization of language
- ~150-200 lines
- Handles all keywords, operators, literals, comments

### 2. **Bison File** (`parser.y`)
- Complete grammar rules
- AST node definitions
- ~300-400 lines

### 3. **C/C++ Implementation** (`main.c` / `interpreter.c`)
- AST structure definitions
- Symbol table implementation
- Interpreter/evaluator logic
- ~500-700 lines

### 4. **Test Cases**
- Arithmetic operations
- Variable scoping
- Function calls and recursion
- Control flow (if, loops)
- Error handling

### 5. **Documentation**
- Language specification
- Compiler design decisions
- Build instructions
- User guide

---

## Technical Implementation Details

### Build System
```bash
flex lexer.l
bison -d parser.y
gcc -o interpreter lex.yy.c parser.tab.c interpreter.c -lm
```

### AST Node Structure (C)
```c
typedef struct {
    NodeType type;
    union {
        int intVal;
        float floatVal;
        char* stringVal;
        struct BinaryOp* binOp;
        struct FunctionCall* funcCall;
    } value;
} ASTNode;
```

### Symbol Table
- Hash table or linked list for variable storage
- Support for nested scopes
- Type information tracking

---

## Learning Outcomes

1. **Lexical Analysis**: Tokenization, regular expressions, pattern matching
2. **Syntax Analysis**: Grammar design, parse trees, conflict resolution
3. **Semantic Analysis**: Type systems, scope management, symbol tables
4. **Code Execution**: AST evaluation, runtime environment, function calls
5. **Error Handling**: Meaningful error messages, error recovery
6. **Software Engineering**: Large-scale C/Bison project management

---

## Scalability & Future Enhancements

- Arrays and structures
- Object-oriented features (classes, inheritance)
- Module system
- Optimization passes
- Code generation to bytecode
- Debugger support

---

## Competitive Advantages

✅ **Complete pipeline** - Shows all compiler phases  
✅ **Practical relevance** - Similar to real language design  
✅ **Scalable** - Can add features incrementally  
✅ **Demonstrable** - Can run actual programs  
✅ **Interview-ready** - Impresses employers and graduate schools  

---

## Timeline

- **Week 1**: Flex lexer design and implementation
- **Week 2**: Bison grammar and parser
- **Week 3**: Semantic analysis and symbol table
- **Week 4**: Interpreter implementation and testing
- **Week 5**: Documentation and optimization

---

## Conclusion

This project provides hands-on experience with all major compiler design concepts while producing a genuinely useful artifact. The skills developed directly apply to language design, software development, and computer science theory.
