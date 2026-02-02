# Compiler Project Proposal

## Project Title:

**EMOJI-FLOW 🌈** — An Emoji-Based Workflow & Automation Programming Language

---

## Submitted To:

- Md. Badiuzzaman Shuvo, Lecturer, CSE, KUET
- Subah Nawar, Lecturer, CSE, KUET

## Submitted By:

- **Arka Braja Prasad Nath**
- Roll No: **2107055**
- Year: Third, Semester: Second
- Department of CSE, KUET

## Date of Submission:

February 2, 2026

---

## 1. Project Description

This project proposes the development of a lexical analyzer using Flex for **EMOJI-FLOW**, a novel emoji-based domain-specific programming language designed to represent workflow automation, task execution, conditional pipelines, and variable manipulation using emojis.

The language is inspired by CI/CD pipelines, shell scripting, and automation tools such as GitHub Actions and Zapier.

### Key Features:

- Fully emoji-based programming language
- Lexical analyzer implemented using Flex
- Tokenizes emoji programs into meaningful tokens
- Supports variables (declaration, assignment, usage)
- Easy to extend to parser, interpreter, or YAML/Script generation

### Objectives:

- Create a unique and visually expressive language using emojis.
- Implement a lexical analyzer that converts emoji programs into tokens.
- Provide a demo showing real workflows, variable usage, task execution, conditionals, loops, and input/output.
- Make the project GitHub-friendly, attracting developer attention.

---

## 2. Theme Explanation

EMOJI-FLOW is a domain-specific language (DSL) for workflow automation.
It allows developers to describe tasks, variables, sequences, conditions, loops, and I/O operations visually using emojis.

### Advantages:

- Emojis are universal, visual, and compact, making programs easy to read.
- Simplifies representation of workflows for novices and non-programmers.
- Supports variables, enabling calculations, counters, and data storage.
- Can later be extended to generate real scripts (Bash, Python, YAML).

**Example Inspiration:** GitHub Actions workflow, shell scripts, Zapier pipelines.

---

## 3. Token List (≥45 tokens)

| Category                    | Emoji | Token    | Meaning                    |
| --------------------------- | ----- | -------- | -------------------------- |
| **Program Structure**       | ▶️    | START    | Workflow start             |
|                             | ⏹️    | END      | Workflow end               |
|                             | 🧱    | BLOCK    | Code block                 |
|                             | ;     | SEMI     | End statement              |
| **Tasks & Actions**         | ⚙️    | TASK     | Define task                |
|                             | ▶     | RUN      | Execute task               |
|                             | ⏳    | WAIT     | Delay                      |
|                             | 🔁    | LOOP     | Repeat                     |
|                             | 🔚    | STOP     | Stop execution             |
|                             | ♻️    | RETRY    | Retry task                 |
| **Conditions & Logic**      | 🤔    | IF       | Condition                  |
|                             | 🔁❓  | ELSE     | Else                       |
|                             | 🤝    | AND      | Logical AND                |
|                             | 🔀    | OR       | Logical OR                 |
|                             | 🚫    | NOT      | Logical NOT                |
|                             | 🔼    | GT       | Greater than               |
|                             | 🔽    | LT       | Less than                  |
|                             | 🟰    | EQ       | Equal                      |
| **Input / Output**          | 📥    | INPUT    | Read input                 |
|                             | 📤    | OUTPUT   | Print output               |
|                             | 📝    | LOG      | Log message                |
|                             | 🔔    | ALERT    | Notification               |
| **File & System**           | 📂    | OPEN     | Open file                  |
|                             | 📁    | CLOSE    | Close file                 |
|                             | 🧾    | FILE     | File                       |
|                             | ❌    | DELETE   | Delete                     |
|                             | ✏️    | WRITE    | Write                      |
|                             | 📄    | READ     | Read                       |
| **Time & Control**          | ⏰    | TIME     | Time value                 |
|                             | 🕒    | SCHEDULE | Schedule task              |
|                             | ⏭️    | NEXT     | Next step                  |
|                             | ⏮️    | PREV     | Previous step              |
| **Identifiers & Variables** | 📛    | ID       | Identifier / variable name |
|                             | 🔢    | INT      | Integer type variable      |
|                             | 🔤    | STRING   | String type variable       |
|                             | 🔥    | TRUE     | Boolean true               |
|                             | ❄️    | FALSE    | Boolean false              |
|                             | 🟰    | ASSIGN   | Assignment operator        |
| **Errors**                  | ⚠️    | ERROR    | Invalid token              |

**Total tokens: 45+ ✅**

This version now includes variables (declaration and assignment) and their use in workflows.

---

## 4. Technical Implementation Details

### 4.1 Example Token Definitions

The lexical analyzer uses specific token constants defined in the C header section:

```c
#define START       256
#define END         257
#define BLOCK       258
#define SEMI        259
#define TASK        260
#define RUN         261
#define WAIT        262
#define LOOP        263
#define STOP        264
#define RETRY       265
#define IF          266
#define ELSE        267
#define AND         268
#define OR          269
#define NOT         270
// ... more tokens up to 45+
```

Each token represents a unique program element, starting from 256 to avoid conflicts with ASCII characters.

### 4.2 UTF-8 Macro Definitions

Since emojis are multi-byte UTF-8 characters, we define UTF-8 byte patterns:

```c
UTF8_CONT   [\x80-\xBF]           // Continuation bytes
UTF8_2BYTE  [\xC2-\xDF]{UTF8_CONT}    // 2-byte sequences
UTF8_3BYTE  [\xE0-\xEF]{UTF8_CONT}{2} // 3-byte sequences
UTF8_4BYTE  [\xF0-\xF4]{UTF8_CONT}{3} // 4-byte sequences
```

These patterns help Flex recognize the structure of UTF-8 encoded emoji characters.

### 4.3 Understanding a Rule (Example)

Here's how a complete lexer rule works:

```c
/* START: ▶️ (U+25B6 U+FE0F) */
"\xE2\x96\xB6\xEF\xB8\x8F"      {
    output_token(START, "START", yytext);
    col_num += 2;
    return START;
}
```

**Breakdown:**

- `/* START: ▶️ (U+25B6 U+FE0F) */` - Comment showing emoji and Unicode codepoints
- `"\xE2\x96\xB6\xEF\xB8\x8F"` - UTF-8 hex byte sequence for ▶️
- `output_token()` - Function to log and output the token
- `col_num += 2` - Track column position (emoji takes 2 character positions)
- `return START` - Return token constant to parser

### 4.4 How Emojis Are Converted to Hex Bytes

The conversion process follows these steps:

**Step 1: Unicode Codepoints**

- ▶️ = U+25B6 (Play Button) + U+FE0F (Variation Selector-16)

**Step 2: UTF-8 Encoding**

- U+25B6 → Binary: `0010 0101 1011 0110`
- 3-byte UTF-8: `1110xxxx 10xxxxxx 10xxxxxx`
- Result: `1110**0010** 10**011010** 10**110110**`
- Hex: `E2 96 B6`

- U+FE0F → UTF-8: `EF B8 8F`

**Step 3: C String Format**

- Combined: `\xE2\x96\xB6\xEF\xB8\x8F`

**Example Conversion Table:**
| Emoji | Unicode | UTF-8 Bytes | C Pattern |
|-------|-------------|----------------|------------------------------|
| ▶️ | U+25B6+FE0F | E2 96 B6 EF B8 8F | `\xE2\x96\xB6\xEF\xB8\x8F` |
| 🤔 | U+1F914 | F0 9F A4 94 | `\xF0\x9F\xA4\x94` |
| 📝 | U+1F4DD | F0 9F 93 9D | `\xF0\x9F\x93\x9D` |
| ⏳ | U+23F3 | E2 8F B3 | `\xE2\x8F\xB3` |

### 4.5 Program Structure Tokens

The EMOJI-FLOW language uses specific structural tokens:

**Control Flow Structure:**

```
▶️                    // START - Begin program execution
🧱 {                  // BLOCK - Start code block
    // ... tasks ...
}                     // End block
⏹️                    // END - Terminate program
```

**Task Definition Structure:**

```
⚙️ taskName           // TASK - Define a task
📛 count 🔢 = 5;      // Variable declaration (ID + INT_TYPE + ASSIGN)
▶ someTask;           // RUN - Execute task
```

**Conditional Structure:**

```
🤔 count 🔼 3 {       // IF + variable + GT + value
    📤 "Greater!";    // OUTPUT + STRING
} 🔁❓ {              // ELSE
    📤 "Lesser!";     // OUTPUT + STRING
}
```

**Loop Structure:**

```
🔁 count 🔽 10 {      // LOOP while count < 10
    📛 count = count + 1;  // Increment counter
    📝 "Processing...";    // LOG message
}
```

This structured approach ensures that EMOJI-FLOW programs maintain clear syntax while leveraging the visual expressiveness of emojis.
