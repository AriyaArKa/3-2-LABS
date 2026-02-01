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
