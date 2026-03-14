/* A Bison parser, made by GNU Bison 3.8.2.  */

/* Bison implementation for Yacc-like parsers in C

   Copyright (C) 1984, 1989-1990, 2000-2015, 2018-2021 Free Software Foundation,
   Inc.

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <https://www.gnu.org/licenses/>.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.

   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */

/* C LALR(1) parser skeleton written by Richard Stallman, by
   simplifying the original so-called "semantic" parser.  */

/* DO NOT RELY ON FEATURES THAT ARE NOT DOCUMENTED in the manual,
   especially those whose name start with YY_ or yy_.  They are
   private implementation details that can be changed or removed.  */

/* All symbols defined below should begin with yy or YY, to avoid
   infringing on user name space.  This should be done even for local
   variables, as they might otherwise be expanded by user macros.
   There are some unavoidable exceptions within include files to
   define necessary library symbols; they are noted "INFRINGES ON
   USER NAME SPACE" below.  */

/* Identify Bison output, and Bison version.  */
#define YYBISON 30802

/* Bison version string.  */
#define YYBISON_VERSION "3.8.2"

/* Skeleton name.  */
#define YYSKELETON_NAME "yacc.c"

/* Pure parsers.  */
#define YYPURE 0

/* Push parsers.  */
#define YYPUSH 0

/* Pull parsers.  */
#define YYPULL 1




/* First part of user prologue.  */
#line 1 "emoji_flow.y"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdarg.h>

extern int yylex(void);
extern int yylineno;
extern FILE *yyin;
extern FILE *output_file;
extern int line_num;

void yyerror(const char *s);

typedef enum {
    NODE_PROGRAM,
    NODE_BLOCK,
    NODE_DECL,
    NODE_ASSIGN,
    NODE_OUTPUT,
    NODE_LOG,
    NODE_ALERT,
    NODE_IF,
    NODE_LOOP,
    NODE_TASK_DEF,
    NODE_TASK_CALL,
    NODE_WAIT,
    NODE_TIME,
    NODE_STOP,
    NODE_RETRY,
    NODE_FILE_OPEN,
    NODE_FILE_WRITE,
    NODE_FILE_READ,
    NODE_FILE_CLOSE,
    NODE_FILE_DELETE,
    NODE_BINOP,
    NODE_UNOP,
    NODE_INT,
    NODE_STRING,
    NODE_BOOL,
    NODE_VAR,
    NODE_NOP
} NodeType;

typedef struct ASTNode {
    NodeType type;
    char *text;
    int ival;
    struct ASTNode *a;
    struct ASTNode *b;
    struct ASTNode *c;
    struct ASTNode *next;
} ASTNode;

typedef enum {
    VAL_NONE,
    VAL_INT,
    VAL_BOOL,
    VAL_STRING
} ValueType;

typedef struct {
    ValueType type;
    int ival;
    int bval;
    char *sval;
} Value;

typedef struct {
    char name[128];
    ValueType declared_type;
    int initialized;
    Value value;
} Symbol;

typedef struct {
    char name[128];
    ASTNode *block;
} TaskDef;

static ASTNode *g_root = NULL;
static Symbol g_symbols[512];
static int g_symbol_count = 0;
static TaskDef g_tasks[256];
static int g_task_count = 0;
static int g_semantic_error_count = 0;

static Value make_none(void);

static void semantic_error(const char *fmt, ...) {
    va_list args;
    fprintf(stderr, "Semantic error: ");
    va_start(args, fmt);
    vfprintf(stderr, fmt, args);
    va_end(args);
    fprintf(stderr, "\n");
    g_semantic_error_count++;
}

static Value make_error(void) {
    return make_none();
}

static char *dup_text(const char *s) {
    size_t len;
    char *out;
    if (!s) {
        return NULL;
    }
    len = strlen(s);
    out = (char *)malloc(len + 1);
    if (!out) {
        fprintf(stderr, "Out of memory while duplicating text\n");
        exit(1);
    }
    memcpy(out, s, len + 1);
    return out;
}

static ASTNode *new_node(NodeType type, const char *text, int ival, ASTNode *a, ASTNode *b, ASTNode *c) {
    ASTNode *node = (ASTNode *)malloc(sizeof(ASTNode));
    if (!node) {
        fprintf(stderr, "Out of memory while creating AST node\n");
        exit(1);
    }
    node->type = type;
    node->text = text ? dup_text(text) : NULL;
    node->ival = ival;
    node->a = a;
    node->b = b;
    node->c = c;
    node->next = NULL;
    return node;
}

static ASTNode *append_stmt(ASTNode *list, ASTNode *stmt) {
    ASTNode *cur;
    if (!stmt) {
        return list;
    }
    if (!list) {
        return stmt;
    }
    cur = list;
    while (cur->next) {
        cur = cur->next;
    }
    cur->next = stmt;
    return list;
}

static const char *node_name(NodeType type) {
    switch (type) {
        case NODE_PROGRAM: return "PROGRAM";
        case NODE_BLOCK: return "BLOCK";
        case NODE_DECL: return "DECL";
        case NODE_ASSIGN: return "ASSIGN";
        case NODE_OUTPUT: return "OUTPUT";
        case NODE_LOG: return "LOG";
        case NODE_ALERT: return "ALERT";
        case NODE_IF: return "IF";
        case NODE_LOOP: return "LOOP";
        case NODE_TASK_DEF: return "TASK_DEF";
        case NODE_TASK_CALL: return "TASK_CALL";
        case NODE_WAIT: return "WAIT";
        case NODE_TIME: return "TIME";
        case NODE_STOP: return "STOP";
        case NODE_RETRY: return "RETRY";
        case NODE_FILE_OPEN: return "FILE_OPEN";
        case NODE_FILE_WRITE: return "FILE_WRITE";
        case NODE_FILE_READ: return "FILE_READ";
        case NODE_FILE_CLOSE: return "FILE_CLOSE";
        case NODE_FILE_DELETE: return "FILE_DELETE";
        case NODE_BINOP: return "BINOP";
        case NODE_UNOP: return "UNOP";
        case NODE_INT: return "INT";
        case NODE_STRING: return "STRING";
        case NODE_BOOL: return "BOOL";
        case NODE_VAR: return "VAR";
        case NODE_NOP: return "NOP";
        default: return "UNKNOWN";
    }
}

static void print_indent(FILE *fp, int indent) {
    int i;
    for (i = 0; i < indent; i++) {
        fputc(' ', fp);
    }
}

static void print_ast(FILE *fp, ASTNode *node, int indent) {
    ASTNode *cur = node;
    while (cur) {
        print_indent(fp, indent);
        if (cur->text) {
            fprintf(fp, "%s(%s)\n", node_name(cur->type), cur->text);
        } else {
            fprintf(fp, "%s\n", node_name(cur->type));
        }

        if (cur->type == NODE_INT) {
            print_indent(fp, indent + 2);
            fprintf(fp, "value=%d\n", cur->ival);
        }
        if (cur->type == NODE_BOOL) {
            print_indent(fp, indent + 2);
            fprintf(fp, "value=%s\n", cur->ival ? "true" : "false");
        }

        if (cur->a) {
            print_indent(fp, indent + 2);
            fprintf(fp, "A:\n");
            print_ast(fp, cur->a, indent + 4);
        }
        if (cur->b) {
            print_indent(fp, indent + 2);
            fprintf(fp, "B:\n");
            print_ast(fp, cur->b, indent + 4);
        }
        if (cur->c) {
            print_indent(fp, indent + 2);
            fprintf(fp, "C:\n");
            print_ast(fp, cur->c, indent + 4);
        }
        cur = cur->next;
    }
}

static Value make_none(void) {
    Value v;
    v.type = VAL_NONE;
    v.ival = 0;
    v.bval = 0;
    v.sval = NULL;
    return v;
}

static Value make_int(int n) {
    Value v;
    v.type = VAL_INT;
    v.ival = n;
    v.bval = (n != 0);
    v.sval = NULL;
    return v;
}

static Value make_bool(int b) {
    Value v;
    v.type = VAL_BOOL;
    v.ival = b ? 1 : 0;
    v.bval = b ? 1 : 0;
    v.sval = NULL;
    return v;
}

static Value make_string(const char *s) {
    Value v;
    v.type = VAL_STRING;
    v.ival = 0;
    v.bval = (s && s[0] != '\0');
    v.sval = s ? dup_text(s) : dup_text("");
    return v;
}

static int value_as_int(Value v) {
    if (v.type == VAL_INT) return v.ival;
    if (v.type == VAL_BOOL) return v.bval ? 1 : 0;
    if (v.type == VAL_STRING) return (v.sval && v.sval[0] != '\0') ? 1 : 0;
    return 0;
}

static int value_as_bool(Value v) {
    if (v.type == VAL_BOOL) return v.bval;
    if (v.type == VAL_INT) return v.ival != 0;
    if (v.type == VAL_STRING) return v.sval && v.sval[0] != '\0';
    return 0;
}

static const char *value_type_name(ValueType t) {
    switch (t) {
        case VAL_INT: return "INT";
        case VAL_BOOL: return "BOOL";
        case VAL_STRING: return "STRING";
        default: return "NONE";
    }
}

static int find_symbol_index(const char *name) {
    int i;
    for (i = 0; i < g_symbol_count; i++) {
        if (strcmp(g_symbols[i].name, name) == 0) {
            return i;
        }
    }
    return -1;
}

static int can_assign(ValueType declared_type, ValueType rhs_type) {
    return declared_type == rhs_type;
}

static int declare_symbol(const char *name, ValueType declared_type, Value value) {
    int idx = find_symbol_index(name);

    if (idx >= 0) {
        semantic_error("redeclaration of variable '%s'", name);
        return 0;
    }

    if (g_symbol_count >= 512) {
        semantic_error("symbol table full while declaring '%s'", name);
        return 0;
    }

    if (!can_assign(declared_type, value.type)) {
        semantic_error(
            "cannot initialize variable '%s' of type %s with value of type %s",
            name,
            value_type_name(declared_type),
            value_type_name(value.type)
        );
        return 0;
    }

    idx = g_symbol_count++;
    strncpy(g_symbols[idx].name, name, sizeof(g_symbols[idx].name) - 1);
    g_symbols[idx].name[sizeof(g_symbols[idx].name) - 1] = '\0';
    g_symbols[idx].declared_type = declared_type;
    g_symbols[idx].initialized = 1;
    g_symbols[idx].value = value;
    return 1;
}

static int assign_symbol(const char *name, Value value) {
    int idx = find_symbol_index(name);
    if (idx < 0) {
        semantic_error("assignment to undeclared variable '%s'", name);
        return 0;
    }

    if (!can_assign(g_symbols[idx].declared_type, value.type)) {
        semantic_error(
            "cannot assign value of type %s to variable '%s' of type %s",
            value_type_name(value.type),
            name,
            value_type_name(g_symbols[idx].declared_type)
        );
        return 0;
    }

    g_symbols[idx].value = value;
    g_symbols[idx].initialized = 1;
    return 1;
}

static Value get_symbol(const char *name) {
    int idx = find_symbol_index(name);
    if (idx < 0) {
        semantic_error("use of undeclared variable '%s'", name);
        return make_error();
    }

    if (!g_symbols[idx].initialized) {
        semantic_error("use of uninitialized variable '%s'", name);
        return make_error();
    }

    return g_symbols[idx].value;
}

static int find_task_index(const char *name) {
    int i;
    for (i = 0; i < g_task_count; i++) {
        if (strcmp(g_tasks[i].name, name) == 0) {
            return i;
        }
    }
    return -1;
}

static void register_task(const char *name, ASTNode *block) {
    int idx = find_task_index(name);

    if (idx >= 0) {
        semantic_error("redeclaration of task '%s'", name);
        return;
    }

    if (g_task_count >= 256) {
        semantic_error("task table full while declaring '%s'", name);
        return;
    }

    idx = g_task_count++;
    strncpy(g_tasks[idx].name, name, sizeof(g_tasks[idx].name) - 1);
    g_tasks[idx].name[sizeof(g_tasks[idx].name) - 1] = '\0';
    g_tasks[idx].block = block;
}

static ASTNode *get_task_block(const char *name) {
    int idx = find_task_index(name);
    if (idx < 0) {
        semantic_error("call to undefined task '%s'", name);
        return NULL;
    }
    return g_tasks[idx].block;
}

static Value eval_expr(ASTNode *expr);
static void exec_stmt_list(ASTNode *stmt);

static void print_value(Value v) {
    switch (v.type) {
        case VAL_INT:
            printf("%d", v.ival);
            break;
        case VAL_BOOL:
            printf("%s", v.bval ? "true" : "false");
            break;
        case VAL_STRING:
            printf("%s", v.sval ? v.sval : "");
            break;
        default:
            printf("<none>");
            break;
    }
}

static Value eval_expr(ASTNode *expr) {
    Value left;
    Value right;
    if (!expr) {
        return make_none();
    }

    switch (expr->type) {
        case NODE_INT:
            return make_int(expr->ival);
        case NODE_BOOL:
            return make_bool(expr->ival);
        case NODE_STRING:
            return make_string(expr->text ? expr->text : "");
        case NODE_VAR:
            return get_symbol(expr->text ? expr->text : "");
        case NODE_UNOP:
            left = eval_expr(expr->a);
            if (expr->text && strcmp(expr->text, "NOT") == 0) {
                if (left.type != VAL_BOOL) {
                    semantic_error("operator NOT expects BOOL, got %s", value_type_name(left.type));
                    return make_error();
                }
                return make_bool(!value_as_bool(left));
            }
            if (expr->text && strcmp(expr->text, "NEG") == 0) {
                if (left.type != VAL_INT) {
                    semantic_error("unary minus expects INT, got %s", value_type_name(left.type));
                    return make_error();
                }
                return make_int(-left.ival);
            }
            return make_error();
        case NODE_BINOP:
            left = eval_expr(expr->a);
            right = eval_expr(expr->b);

            if (expr->text && strcmp(expr->text, "PLUS") == 0) {
                if (left.type == VAL_STRING && right.type == VAL_STRING) {
                    char buf[1024];
                    snprintf(buf, sizeof(buf), "%s%s",
                             left.sval ? left.sval : "",
                             right.sval ? right.sval : "");
                    return make_string(buf);
                }
                if (left.type == VAL_INT && right.type == VAL_INT) {
                    return make_int(left.ival + right.ival);
                }
                semantic_error("operator PLUS requires INT+INT or STRING+STRING, got %s and %s",
                               value_type_name(left.type), value_type_name(right.type));
                return make_error();
            }
            if (expr->text && strcmp(expr->text, "MINUS") == 0) {
                if (left.type != VAL_INT || right.type != VAL_INT) {
                    semantic_error("operator MINUS requires INT operands, got %s and %s",
                                   value_type_name(left.type), value_type_name(right.type));
                    return make_error();
                }
                return make_int(left.ival - right.ival);
            }
            if (expr->text && strcmp(expr->text, "MUL") == 0) {
                if (left.type != VAL_INT || right.type != VAL_INT) {
                    semantic_error("operator MUL requires INT operands, got %s and %s",
                                   value_type_name(left.type), value_type_name(right.type));
                    return make_error();
                }
                return make_int(left.ival * right.ival);
            }
            if (expr->text && strcmp(expr->text, "DIV") == 0) {
                if (left.type != VAL_INT || right.type != VAL_INT) {
                    semantic_error("operator DIV requires INT operands, got %s and %s",
                                   value_type_name(left.type), value_type_name(right.type));
                    return make_error();
                }
                if (right.ival == 0) {
                    semantic_error("division by zero");
                    return make_error();
                }
                return make_int(left.ival / right.ival);
            }
            if (expr->text && strcmp(expr->text, "GT") == 0) {
                if (left.type != VAL_INT || right.type != VAL_INT) {
                    semantic_error("operator GT requires INT operands, got %s and %s",
                                   value_type_name(left.type), value_type_name(right.type));
                    return make_error();
                }
                return make_bool(left.ival > right.ival);
            }
            if (expr->text && strcmp(expr->text, "LT") == 0) {
                if (left.type != VAL_INT || right.type != VAL_INT) {
                    semantic_error("operator LT requires INT operands, got %s and %s",
                                   value_type_name(left.type), value_type_name(right.type));
                    return make_error();
                }
                return make_bool(left.ival < right.ival);
            }
            if (expr->text && strcmp(expr->text, "EQ") == 0) {
                if (left.type != right.type) {
                    semantic_error("operator EQ requires matching operand types, got %s and %s",
                                   value_type_name(left.type), value_type_name(right.type));
                    return make_error();
                }
                if (left.type == VAL_STRING) {
                    return make_bool(strcmp(left.sval ? left.sval : "", right.sval ? right.sval : "") == 0);
                }
                if (left.type == VAL_INT) {
                    return make_bool(left.ival == right.ival);
                }
                if (left.type == VAL_BOOL) {
                    return make_bool(left.bval == right.bval);
                }
                semantic_error("operator EQ does not support type %s", value_type_name(left.type));
                return make_error();
            }
            if (expr->text && strcmp(expr->text, "AND") == 0) {
                if (left.type != VAL_BOOL || right.type != VAL_BOOL) {
                    semantic_error("operator AND requires BOOL operands, got %s and %s",
                                   value_type_name(left.type), value_type_name(right.type));
                    return make_error();
                }
                return make_bool(left.bval && right.bval);
            }
            if (expr->text && strcmp(expr->text, "OR") == 0) {
                if (left.type != VAL_BOOL || right.type != VAL_BOOL) {
                    semantic_error("operator OR requires BOOL operands, got %s and %s",
                                   value_type_name(left.type), value_type_name(right.type));
                    return make_error();
                }
                return make_bool(left.bval || right.bval);
            }
            return make_error();
        default:
            return make_error();
    }
}

static void exec_stmt(ASTNode *stmt) {
    Value v;
    int i;
    ASTNode *task_block;
    if (!stmt) {
        return;
    }

    switch (stmt->type) {
        case NODE_DECL:
            v = eval_expr(stmt->b);

            if (stmt->text && strcmp(stmt->text, "INT") == 0) {
                if (!declare_symbol(stmt->a && stmt->a->text ? stmt->a->text : "", VAL_INT, v)) {
                    break;
                }
            } else if (stmt->text && strcmp(stmt->text, "STRING") == 0) {
                if (!declare_symbol(stmt->a && stmt->a->text ? stmt->a->text : "", VAL_STRING, v)) {
                    break;
                }
            } else {
                if (!declare_symbol(stmt->a && stmt->a->text ? stmt->a->text : "", VAL_BOOL, v)) {
                    break;
                }
            }

            printf("[SET] %s = ", stmt->a && stmt->a->text ? stmt->a->text : "?");
            print_value(v);
            printf(" (%s)\n", value_type_name(v.type));
            break;

        case NODE_ASSIGN:
            v = eval_expr(stmt->b);
            if (!assign_symbol(stmt->a && stmt->a->text ? stmt->a->text : "", v)) {
                break;
            }
            printf("[SET] %s = ", stmt->a && stmt->a->text ? stmt->a->text : "?");
            print_value(v);
            printf(" (%s)\n", value_type_name(v.type));
            break;
        case NODE_OUTPUT:
            v = eval_expr(stmt->a);
            printf("[OUTPUT] ");
            print_value(v);
            printf("\n");
            break;
        case NODE_LOG:
            v = eval_expr(stmt->a);
            printf("[LOG] ");
            print_value(v);
            printf("\n");
            break;
        case NODE_ALERT:
            v = eval_expr(stmt->a);
            printf("[ALERT] ");
            print_value(v);
            printf("\n");
            break;
        case NODE_IF:
            v = eval_expr(stmt->a);
            if (v.type != VAL_BOOL) {
                semantic_error("IF condition must be BOOL, got %s", value_type_name(v.type));
                break;
            }
            if (value_as_bool(v)) {
                exec_stmt_list(stmt->b ? stmt->b->a : NULL);
            } else if (stmt->c) {
                exec_stmt_list(stmt->c->a);
            }
            break;
        case NODE_LOOP:
            v = eval_expr(stmt->a);
            if (v.type != VAL_INT) {
                semantic_error("LOOP iteration count must be INT, got %s", value_type_name(v.type));
                break;
            }
            if (v.ival < 0) {
                semantic_error("LOOP iteration count cannot be negative (%d)", v.ival);
                break;
            }
            for (i = 0; i < v.ival; i++) {
                exec_stmt_list(stmt->b ? stmt->b->a : NULL);
                if (g_semantic_error_count > 0) {
                    break;
                }
            }
            break;
        case NODE_TASK_DEF:
            register_task(stmt->text ? stmt->text : "", stmt->a);
            printf("[TASK DEF] %s\n", stmt->text ? stmt->text : "");
            break;
        case NODE_TASK_CALL:
            task_block = get_task_block(stmt->text ? stmt->text : "");
            if (task_block) {
                printf("[TASK CALL] %s\n", stmt->text ? stmt->text : "");
                exec_stmt_list(task_block->a);
            }
            break;
        case NODE_WAIT:
            v = eval_expr(stmt->a);
            if (v.type != VAL_INT) {
                semantic_error("WAIT expects INT duration, got %s", value_type_name(v.type));
                break;
            }
            printf("[WAIT] %d\n", v.ival);
            break;
        case NODE_TIME:
            v = eval_expr(stmt->a);
            printf("[TIME] ");
            print_value(v);
            printf("\n");
            break;
        case NODE_STOP:
            if (stmt->text) {
                printf("[STOP] %s\n", stmt->text);
            } else {
                printf("[STOP]\n");
            }
            break;
        case NODE_RETRY:
            printf("[RETRY]\n");
            break;
        case NODE_FILE_OPEN:
            v = eval_expr(stmt->a);
            if (v.type != VAL_STRING) {
                semantic_error("FILE OPEN expects STRING path, got %s", value_type_name(v.type));
                break;
            }
            printf("[FILE OPEN] ");
            print_value(v);
            printf("\n");
            break;
        case NODE_FILE_WRITE:
            v = eval_expr(stmt->a);
            if (v.type != VAL_STRING) {
                semantic_error("FILE WRITE expects STRING payload, got %s", value_type_name(v.type));
                break;
            }
            printf("[FILE WRITE] ");
            print_value(v);
            printf("\n");
            break;
        case NODE_FILE_READ:
            printf("[FILE READ]\n");
            break;
        case NODE_FILE_CLOSE:
            printf("[FILE CLOSE]\n");
            break;
        case NODE_FILE_DELETE:
            v = eval_expr(stmt->a);
            if (v.type != VAL_STRING) {
                semantic_error("FILE DELETE expects STRING path, got %s", value_type_name(v.type));
                break;
            }
            printf("[FILE DELETE] ");
            print_value(v);
            printf("\n");
            break;
        default:
            break;
    }
}

static void exec_stmt_list(ASTNode *stmt) {
    ASTNode *cur = stmt;
    while (cur) {
        exec_stmt(cur);
        if (g_semantic_error_count > 0) {
            return;
        }
        cur = cur->next;
    }
}

#line 811 "emoji_flow.tab.c"

# ifndef YY_CAST
#  ifdef __cplusplus
#   define YY_CAST(Type, Val) static_cast<Type> (Val)
#   define YY_REINTERPRET_CAST(Type, Val) reinterpret_cast<Type> (Val)
#  else
#   define YY_CAST(Type, Val) ((Type) (Val))
#   define YY_REINTERPRET_CAST(Type, Val) ((Type) (Val))
#  endif
# endif
# ifndef YY_NULLPTR
#  if defined __cplusplus
#   if 201103L <= __cplusplus
#    define YY_NULLPTR nullptr
#   else
#    define YY_NULLPTR 0
#   endif
#  else
#   define YY_NULLPTR ((void*)0)
#  endif
# endif

#include "emoji_flow.tab.h"
/* Symbol kind.  */
enum yysymbol_kind_t
{
  YYSYMBOL_YYEMPTY = -2,
  YYSYMBOL_YYEOF = 0,                      /* "end of file"  */
  YYSYMBOL_YYerror = 1,                    /* error  */
  YYSYMBOL_YYUNDEF = 2,                    /* "invalid token"  */
  YYSYMBOL_START = 3,                      /* START  */
  YYSYMBOL_END = 4,                        /* END  */
  YYSYMBOL_BLOCK = 5,                      /* BLOCK  */
  YYSYMBOL_SEMI = 6,                       /* SEMI  */
  YYSYMBOL_TASK = 7,                       /* TASK  */
  YYSYMBOL_RUN = 8,                        /* RUN  */
  YYSYMBOL_WAIT = 9,                       /* WAIT  */
  YYSYMBOL_LOOP = 10,                      /* LOOP  */
  YYSYMBOL_STOP = 11,                      /* STOP  */
  YYSYMBOL_RETRY = 12,                     /* RETRY  */
  YYSYMBOL_IF = 13,                        /* IF  */
  YYSYMBOL_ELSE = 14,                      /* ELSE  */
  YYSYMBOL_AND = 15,                       /* AND  */
  YYSYMBOL_OR = 16,                        /* OR  */
  YYSYMBOL_NOT = 17,                       /* NOT  */
  YYSYMBOL_GT = 18,                        /* GT  */
  YYSYMBOL_LT = 19,                        /* LT  */
  YYSYMBOL_EQ = 20,                        /* EQ  */
  YYSYMBOL_INPUT = 21,                     /* INPUT  */
  YYSYMBOL_OUTPUT = 22,                    /* OUTPUT  */
  YYSYMBOL_LOG = 23,                       /* LOG  */
  YYSYMBOL_ALERT = 24,                     /* ALERT  */
  YYSYMBOL_OPEN = 25,                      /* OPEN  */
  YYSYMBOL_CLOSE = 26,                     /* CLOSE  */
  YYSYMBOL_FILE_TOK = 27,                  /* FILE_TOK  */
  YYSYMBOL_DELETE = 28,                    /* DELETE  */
  YYSYMBOL_WRITE = 29,                     /* WRITE  */
  YYSYMBOL_READ = 30,                      /* READ  */
  YYSYMBOL_TIME = 31,                      /* TIME  */
  YYSYMBOL_SCHEDULE = 32,                  /* SCHEDULE  */
  YYSYMBOL_NEXT = 33,                      /* NEXT  */
  YYSYMBOL_PREV = 34,                      /* PREV  */
  YYSYMBOL_ID = 35,                        /* ID  */
  YYSYMBOL_INT_TYPE = 36,                  /* INT_TYPE  */
  YYSYMBOL_STRING_TYPE = 37,               /* STRING_TYPE  */
  YYSYMBOL_TRUE_VAL = 38,                  /* TRUE_VAL  */
  YYSYMBOL_FALSE_VAL = 39,                 /* FALSE_VAL  */
  YYSYMBOL_ASSIGN = 40,                    /* ASSIGN  */
  YYSYMBOL_ERROR_TOK = 41,                 /* ERROR_TOK  */
  YYSYMBOL_LPAREN = 42,                    /* LPAREN  */
  YYSYMBOL_RPAREN = 43,                    /* RPAREN  */
  YYSYMBOL_LBRACE = 44,                    /* LBRACE  */
  YYSYMBOL_RBRACE = 45,                    /* RBRACE  */
  YYSYMBOL_COMMA = 46,                     /* COMMA  */
  YYSYMBOL_PLUS = 47,                      /* PLUS  */
  YYSYMBOL_MINUS = 48,                     /* MINUS  */
  YYSYMBOL_MULTIPLY = 49,                  /* MULTIPLY  */
  YYSYMBOL_DIVIDE = 50,                    /* DIVIDE  */
  YYSYMBOL_COLON = 51,                     /* COLON  */
  YYSYMBOL_NEWLINE = 52,                   /* NEWLINE  */
  YYSYMBOL_NUMBER = 53,                    /* NUMBER  */
  YYSYMBOL_STRING_LIT = 54,                /* STRING_LIT  */
  YYSYMBOL_IDENTIFIER = 55,                /* IDENTIFIER  */
  YYSYMBOL_UMINUS = 56,                    /* UMINUS  */
  YYSYMBOL_YYACCEPT = 57,                  /* $accept  */
  YYSYMBOL_program = 58,                   /* program  */
  YYSYMBOL_stmt_list = 59,                 /* stmt_list  */
  YYSYMBOL_stmt = 60,                      /* stmt  */
  YYSYMBOL_block = 61,                     /* block  */
  YYSYMBOL_else_opt = 62,                  /* else_opt  */
  YYSYMBOL_var_ref = 63,                   /* var_ref  */
  YYSYMBOL_expr = 64                       /* expr  */
};
typedef enum yysymbol_kind_t yysymbol_kind_t;




#ifdef short
# undef short
#endif

/* On compilers that do not define __PTRDIFF_MAX__ etc., make sure
   <limits.h> and (if available) <stdint.h> are included
   so that the code can choose integer types of a good width.  */

#ifndef __PTRDIFF_MAX__
# include <limits.h> /* INFRINGES ON USER NAME SPACE */
# if defined __STDC_VERSION__ && 199901 <= __STDC_VERSION__
#  include <stdint.h> /* INFRINGES ON USER NAME SPACE */
#  define YY_STDINT_H
# endif
#endif

/* Narrow types that promote to a signed type and that can represent a
   signed or unsigned integer of at least N bits.  In tables they can
   save space and decrease cache pressure.  Promoting to a signed type
   helps avoid bugs in integer arithmetic.  */

#ifdef __INT_LEAST8_MAX__
typedef __INT_LEAST8_TYPE__ yytype_int8;
#elif defined YY_STDINT_H
typedef int_least8_t yytype_int8;
#else
typedef signed char yytype_int8;
#endif

#ifdef __INT_LEAST16_MAX__
typedef __INT_LEAST16_TYPE__ yytype_int16;
#elif defined YY_STDINT_H
typedef int_least16_t yytype_int16;
#else
typedef short yytype_int16;
#endif

/* Work around bug in HP-UX 11.23, which defines these macros
   incorrectly for preprocessor constants.  This workaround can likely
   be removed in 2023, as HPE has promised support for HP-UX 11.23
   (aka HP-UX 11i v2) only through the end of 2022; see Table 2 of
   <https://h20195.www2.hpe.com/V2/getpdf.aspx/4AA4-7673ENW.pdf>.  */
#ifdef __hpux
# undef UINT_LEAST8_MAX
# undef UINT_LEAST16_MAX
# define UINT_LEAST8_MAX 255
# define UINT_LEAST16_MAX 65535
#endif

#if defined __UINT_LEAST8_MAX__ && __UINT_LEAST8_MAX__ <= __INT_MAX__
typedef __UINT_LEAST8_TYPE__ yytype_uint8;
#elif (!defined __UINT_LEAST8_MAX__ && defined YY_STDINT_H \
       && UINT_LEAST8_MAX <= INT_MAX)
typedef uint_least8_t yytype_uint8;
#elif !defined __UINT_LEAST8_MAX__ && UCHAR_MAX <= INT_MAX
typedef unsigned char yytype_uint8;
#else
typedef short yytype_uint8;
#endif

#if defined __UINT_LEAST16_MAX__ && __UINT_LEAST16_MAX__ <= __INT_MAX__
typedef __UINT_LEAST16_TYPE__ yytype_uint16;
#elif (!defined __UINT_LEAST16_MAX__ && defined YY_STDINT_H \
       && UINT_LEAST16_MAX <= INT_MAX)
typedef uint_least16_t yytype_uint16;
#elif !defined __UINT_LEAST16_MAX__ && USHRT_MAX <= INT_MAX
typedef unsigned short yytype_uint16;
#else
typedef int yytype_uint16;
#endif

#ifndef YYPTRDIFF_T
# if defined __PTRDIFF_TYPE__ && defined __PTRDIFF_MAX__
#  define YYPTRDIFF_T __PTRDIFF_TYPE__
#  define YYPTRDIFF_MAXIMUM __PTRDIFF_MAX__
# elif defined PTRDIFF_MAX
#  ifndef ptrdiff_t
#   include <stddef.h> /* INFRINGES ON USER NAME SPACE */
#  endif
#  define YYPTRDIFF_T ptrdiff_t
#  define YYPTRDIFF_MAXIMUM PTRDIFF_MAX
# else
#  define YYPTRDIFF_T long
#  define YYPTRDIFF_MAXIMUM LONG_MAX
# endif
#endif

#ifndef YYSIZE_T
# ifdef __SIZE_TYPE__
#  define YYSIZE_T __SIZE_TYPE__
# elif defined size_t
#  define YYSIZE_T size_t
# elif defined __STDC_VERSION__ && 199901 <= __STDC_VERSION__
#  include <stddef.h> /* INFRINGES ON USER NAME SPACE */
#  define YYSIZE_T size_t
# else
#  define YYSIZE_T unsigned
# endif
#endif

#define YYSIZE_MAXIMUM                                  \
  YY_CAST (YYPTRDIFF_T,                                 \
           (YYPTRDIFF_MAXIMUM < YY_CAST (YYSIZE_T, -1)  \
            ? YYPTRDIFF_MAXIMUM                         \
            : YY_CAST (YYSIZE_T, -1)))

#define YYSIZEOF(X) YY_CAST (YYPTRDIFF_T, sizeof (X))


/* Stored state numbers (used for stacks). */
typedef yytype_uint8 yy_state_t;

/* State numbers in computations.  */
typedef int yy_state_fast_t;

#ifndef YY_
# if defined YYENABLE_NLS && YYENABLE_NLS
#  if ENABLE_NLS
#   include <libintl.h> /* INFRINGES ON USER NAME SPACE */
#   define YY_(Msgid) dgettext ("bison-runtime", Msgid)
#  endif
# endif
# ifndef YY_
#  define YY_(Msgid) Msgid
# endif
#endif


#ifndef YY_ATTRIBUTE_PURE
# if defined __GNUC__ && 2 < __GNUC__ + (96 <= __GNUC_MINOR__)
#  define YY_ATTRIBUTE_PURE __attribute__ ((__pure__))
# else
#  define YY_ATTRIBUTE_PURE
# endif
#endif

#ifndef YY_ATTRIBUTE_UNUSED
# if defined __GNUC__ && 2 < __GNUC__ + (7 <= __GNUC_MINOR__)
#  define YY_ATTRIBUTE_UNUSED __attribute__ ((__unused__))
# else
#  define YY_ATTRIBUTE_UNUSED
# endif
#endif

/* Suppress unused-variable warnings by "using" E.  */
#if ! defined lint || defined __GNUC__
# define YY_USE(E) ((void) (E))
#else
# define YY_USE(E) /* empty */
#endif

/* Suppress an incorrect diagnostic about yylval being uninitialized.  */
#if defined __GNUC__ && ! defined __ICC && 406 <= __GNUC__ * 100 + __GNUC_MINOR__
# if __GNUC__ * 100 + __GNUC_MINOR__ < 407
#  define YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN                           \
    _Pragma ("GCC diagnostic push")                                     \
    _Pragma ("GCC diagnostic ignored \"-Wuninitialized\"")
# else
#  define YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN                           \
    _Pragma ("GCC diagnostic push")                                     \
    _Pragma ("GCC diagnostic ignored \"-Wuninitialized\"")              \
    _Pragma ("GCC diagnostic ignored \"-Wmaybe-uninitialized\"")
# endif
# define YY_IGNORE_MAYBE_UNINITIALIZED_END      \
    _Pragma ("GCC diagnostic pop")
#else
# define YY_INITIAL_VALUE(Value) Value
#endif
#ifndef YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN
# define YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN
# define YY_IGNORE_MAYBE_UNINITIALIZED_END
#endif
#ifndef YY_INITIAL_VALUE
# define YY_INITIAL_VALUE(Value) /* Nothing. */
#endif

#if defined __cplusplus && defined __GNUC__ && ! defined __ICC && 6 <= __GNUC__
# define YY_IGNORE_USELESS_CAST_BEGIN                          \
    _Pragma ("GCC diagnostic push")                            \
    _Pragma ("GCC diagnostic ignored \"-Wuseless-cast\"")
# define YY_IGNORE_USELESS_CAST_END            \
    _Pragma ("GCC diagnostic pop")
#endif
#ifndef YY_IGNORE_USELESS_CAST_BEGIN
# define YY_IGNORE_USELESS_CAST_BEGIN
# define YY_IGNORE_USELESS_CAST_END
#endif


#define YY_ASSERT(E) ((void) (0 && (E)))

#if 1

/* The parser invokes alloca or malloc; define the necessary symbols.  */

# ifdef YYSTACK_USE_ALLOCA
#  if YYSTACK_USE_ALLOCA
#   ifdef __GNUC__
#    define YYSTACK_ALLOC __builtin_alloca
#   elif defined __BUILTIN_VA_ARG_INCR
#    include <alloca.h> /* INFRINGES ON USER NAME SPACE */
#   elif defined _AIX
#    define YYSTACK_ALLOC __alloca
#   elif defined _MSC_VER
#    include <malloc.h> /* INFRINGES ON USER NAME SPACE */
#    define alloca _alloca
#   else
#    define YYSTACK_ALLOC alloca
#    if ! defined _ALLOCA_H && ! defined EXIT_SUCCESS
#     include <stdlib.h> /* INFRINGES ON USER NAME SPACE */
      /* Use EXIT_SUCCESS as a witness for stdlib.h.  */
#     ifndef EXIT_SUCCESS
#      define EXIT_SUCCESS 0
#     endif
#    endif
#   endif
#  endif
# endif

# ifdef YYSTACK_ALLOC
   /* Pacify GCC's 'empty if-body' warning.  */
#  define YYSTACK_FREE(Ptr) do { /* empty */; } while (0)
#  ifndef YYSTACK_ALLOC_MAXIMUM
    /* The OS might guarantee only one guard page at the bottom of the stack,
       and a page size can be as small as 4096 bytes.  So we cannot safely
       invoke alloca (N) if N exceeds 4096.  Use a slightly smaller number
       to allow for a few compiler-allocated temporary stack slots.  */
#   define YYSTACK_ALLOC_MAXIMUM 4032 /* reasonable circa 2006 */
#  endif
# else
#  define YYSTACK_ALLOC YYMALLOC
#  define YYSTACK_FREE YYFREE
#  ifndef YYSTACK_ALLOC_MAXIMUM
#   define YYSTACK_ALLOC_MAXIMUM YYSIZE_MAXIMUM
#  endif
#  if (defined __cplusplus && ! defined EXIT_SUCCESS \
       && ! ((defined YYMALLOC || defined malloc) \
             && (defined YYFREE || defined free)))
#   include <stdlib.h> /* INFRINGES ON USER NAME SPACE */
#   ifndef EXIT_SUCCESS
#    define EXIT_SUCCESS 0
#   endif
#  endif
#  ifndef YYMALLOC
#   define YYMALLOC malloc
#   if ! defined malloc && ! defined EXIT_SUCCESS
void *malloc (YYSIZE_T); /* INFRINGES ON USER NAME SPACE */
#   endif
#  endif
#  ifndef YYFREE
#   define YYFREE free
#   if ! defined free && ! defined EXIT_SUCCESS
void free (void *); /* INFRINGES ON USER NAME SPACE */
#   endif
#  endif
# endif
#endif /* 1 */

#if (! defined yyoverflow \
     && (! defined __cplusplus \
         || (defined YYSTYPE_IS_TRIVIAL && YYSTYPE_IS_TRIVIAL)))

/* A type that is properly aligned for any stack member.  */
union yyalloc
{
  yy_state_t yyss_alloc;
  YYSTYPE yyvs_alloc;
};

/* The size of the maximum gap between one aligned stack and the next.  */
# define YYSTACK_GAP_MAXIMUM (YYSIZEOF (union yyalloc) - 1)

/* The size of an array large to enough to hold all stacks, each with
   N elements.  */
# define YYSTACK_BYTES(N) \
     ((N) * (YYSIZEOF (yy_state_t) + YYSIZEOF (YYSTYPE)) \
      + YYSTACK_GAP_MAXIMUM)

# define YYCOPY_NEEDED 1

/* Relocate STACK from its old location to the new one.  The
   local variables YYSIZE and YYSTACKSIZE give the old and new number of
   elements in the stack, and YYPTR gives the new location of the
   stack.  Advance YYPTR to a properly aligned location for the next
   stack.  */
# define YYSTACK_RELOCATE(Stack_alloc, Stack)                           \
    do                                                                  \
      {                                                                 \
        YYPTRDIFF_T yynewbytes;                                         \
        YYCOPY (&yyptr->Stack_alloc, Stack, yysize);                    \
        Stack = &yyptr->Stack_alloc;                                    \
        yynewbytes = yystacksize * YYSIZEOF (*Stack) + YYSTACK_GAP_MAXIMUM; \
        yyptr += yynewbytes / YYSIZEOF (*yyptr);                        \
      }                                                                 \
    while (0)

#endif

#if defined YYCOPY_NEEDED && YYCOPY_NEEDED
/* Copy COUNT objects from SRC to DST.  The source and destination do
   not overlap.  */
# ifndef YYCOPY
#  if defined __GNUC__ && 1 < __GNUC__
#   define YYCOPY(Dst, Src, Count) \
      __builtin_memcpy (Dst, Src, YY_CAST (YYSIZE_T, (Count)) * sizeof (*(Src)))
#  else
#   define YYCOPY(Dst, Src, Count)              \
      do                                        \
        {                                       \
          YYPTRDIFF_T yyi;                      \
          for (yyi = 0; yyi < (Count); yyi++)   \
            (Dst)[yyi] = (Src)[yyi];            \
        }                                       \
      while (0)
#  endif
# endif
#endif /* !YYCOPY_NEEDED */

/* YYFINAL -- State number of the termination state.  */
#define YYFINAL  4
/* YYLAST -- Last index in YYTABLE.  */
#define YYLAST   426

/* YYNTOKENS -- Number of terminals.  */
#define YYNTOKENS  57
/* YYNNTS -- Number of nonterminals.  */
#define YYNNTS  8
/* YYNRULES -- Number of rules.  */
#define YYNRULES  52
/* YYNSTATES -- Number of states.  */
#define YYNSTATES  136

/* YYMAXUTOK -- Last valid token kind.  */
#define YYMAXUTOK   311


/* YYTRANSLATE(TOKEN-NUM) -- Symbol number corresponding to TOKEN-NUM
   as returned by yylex, with out-of-bounds checking.  */
#define YYTRANSLATE(YYX)                                \
  (0 <= (YYX) && (YYX) <= YYMAXUTOK                     \
   ? YY_CAST (yysymbol_kind_t, yytranslate[YYX])        \
   : YYSYMBOL_YYUNDEF)

/* YYTRANSLATE[TOKEN-NUM] -- Symbol number corresponding to TOKEN-NUM
   as returned by yylex.  */
static const yytype_int8 yytranslate[] =
{
       0,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     1,     2,     3,     4,
       5,     6,     7,     8,     9,    10,    11,    12,    13,    14,
      15,    16,    17,    18,    19,    20,    21,    22,    23,    24,
      25,    26,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    39,    40,    41,    42,    43,    44,
      45,    46,    47,    48,    49,    50,    51,    52,    53,    54,
      55,    56
};

#if YYDEBUG
/* YYRLINE[YYN] -- Source line where rule number YYN was defined.  */
static const yytype_int16 yyrline[] =
{
       0,   777,   777,   786,   789,   796,   798,   800,   802,   804,
     806,   808,   810,   812,   814,   816,   818,   820,   822,   824,
     826,   828,   830,   832,   834,   836,   838,   840,   842,   844,
     846,   851,   858,   863,   869,   873,   880,   882,   884,   886,
     888,   890,   892,   894,   896,   898,   900,   902,   904,   906,
     908,   910,   912
};
#endif

/** Accessing symbol of state STATE.  */
#define YY_ACCESSING_SYMBOL(State) YY_CAST (yysymbol_kind_t, yystos[State])

#if 1
/* The user-facing name of the symbol whose (internal) number is
   YYSYMBOL.  No bounds checking.  */
static const char *yysymbol_name (yysymbol_kind_t yysymbol) YY_ATTRIBUTE_UNUSED;

/* YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
   First, the terminals, then, starting at YYNTOKENS, nonterminals.  */
static const char *const yytname[] =
{
  "\"end of file\"", "error", "\"invalid token\"", "START", "END",
  "BLOCK", "SEMI", "TASK", "RUN", "WAIT", "LOOP", "STOP", "RETRY", "IF",
  "ELSE", "AND", "OR", "NOT", "GT", "LT", "EQ", "INPUT", "OUTPUT", "LOG",
  "ALERT", "OPEN", "CLOSE", "FILE_TOK", "DELETE", "WRITE", "READ", "TIME",
  "SCHEDULE", "NEXT", "PREV", "ID", "INT_TYPE", "STRING_TYPE", "TRUE_VAL",
  "FALSE_VAL", "ASSIGN", "ERROR_TOK", "LPAREN", "RPAREN", "LBRACE",
  "RBRACE", "COMMA", "PLUS", "MINUS", "MULTIPLY", "DIVIDE", "COLON",
  "NEWLINE", "NUMBER", "STRING_LIT", "IDENTIFIER", "UMINUS", "$accept",
  "program", "stmt_list", "stmt", "block", "else_opt", "var_ref", "expr", YY_NULLPTR
};

static const char *
yysymbol_name (yysymbol_kind_t yysymbol)
{
  return yytname[yysymbol];
}
#endif

#define YYPACT_NINF (-46)

#define yypact_value_is_default(Yyn) \
  ((Yyn) == YYPACT_NINF)

#define YYTABLE_NINF (-1)

#define yytable_value_is_error(Yyn) \
  ((Yyn) == YYTABLE_NINF)

/* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
   STATE-NUM.  */
static const yytype_int16 yypact[] =
{
       6,   -46,     3,    75,   -46,   -46,   -45,   -44,     0,   -29,
      -5,     8,   -27,     0,     0,     0,   -11,    -8,    -7,     1,
      10,     0,   -37,   -14,    37,   -10,   -33,   -33,   -33,   -33,
       0,   -46,   -46,     9,     7,    46,     0,   -46,   -46,     0,
       0,   -46,   -46,   -46,   109,     0,   -46,    50,   -46,     0,
     148,   154,   165,     0,    51,     0,     0,    52,   171,    54,
      59,   -46,   -46,    26,    36,    40,    41,   210,     0,   -46,
     -46,   -46,   340,   314,   -46,   -46,     0,     0,     0,     0,
       0,     0,     0,     0,     0,   350,   -46,   358,   -46,   -46,
     -46,   216,   -46,   227,   233,   -46,   -46,   -46,   -46,     0,
       0,     0,     0,   -46,   272,   110,   -46,   340,    14,   376,
     376,   376,   -42,   -42,   -46,   -46,     7,     7,   -46,   -46,
     -46,   278,   289,   295,   334,   -46,   -46,   -46,    81,   -46,
     -46,   -46,   -46,     7,   -46,   -46
};

/* YYDEFACT[STATE-NUM] -- Default reduction number in state STATE-NUM.
   Performed when YYTABLE does not specify something else to do.  Zero
   means the default is an error.  */
static const yytype_int8 yydefact[] =
{
       0,     3,     0,     0,     1,     2,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,    34,     4,     0,     0,     0,     0,    38,    39,     0,
       0,    36,    37,    40,     0,     0,    22,     0,    23,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,    20,    35,     0,     0,     0,     0,     0,     0,     3,
      16,    17,    42,     0,    43,    24,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,    21,     0,    10,    11,
      12,     0,    29,     0,     0,    28,    25,    18,    19,     0,
       0,     0,     0,    13,     0,     0,    41,    51,    52,    48,
      49,    50,    44,    45,    46,    47,     0,     0,    26,    30,
      27,     0,     0,     0,     0,     9,    31,    15,    33,     5,
       6,     7,     8,     0,    14,    32
};

/* YYPGOTO[NTERM-NUM].  */
static const yytype_int8 yypgoto[] =
{
     -46,   -46,    20,   -46,   -39,   -46,    -3,    -9
};

/* YYDEFGOTO[NTERM-NUM].  */
static const yytype_uint8 yydefgoto[] =
{
       0,     2,     3,    32,    70,   134,    43,    44
};

/* YYTABLE[YYPACT[STATE-NUM]] -- What to do in state STATE-NUM.  If
   positive, shift that token.  If negative, reduce the rule whose
   number is the opposite.  If YYTABLE_NINF, syntax error.  */
static const yytype_int16 yytable[] =
{
      33,    46,    25,     4,    50,    51,    52,    83,    84,     1,
      34,    35,    58,    45,    48,    49,    53,    36,    59,    54,
      55,    67,    31,    63,    64,    65,    66,    72,    56,    76,
      73,    74,    78,    79,    80,    25,    85,    57,    37,    38,
      87,    60,    39,    61,    91,    62,    93,    94,    40,    68,
      47,    69,    71,    41,    42,    31,    86,    92,    95,   104,
      97,    81,    82,    83,    84,    98,    99,   107,   108,   109,
     110,   111,   112,   113,   114,   115,   100,   127,   128,     5,
     101,   102,     6,     7,     8,     9,    10,    11,    12,   105,
     121,   122,   123,   124,   135,   133,     0,    13,    14,    15,
      16,    17,    33,    18,    19,    20,    21,    22,    23,    24,
      25,    26,    27,    28,    29,    75,    30,     6,     7,     8,
       9,    10,    11,    12,    76,    77,     0,    78,    79,    80,
      31,     0,    13,    14,    15,    16,    17,     0,    18,    19,
      20,    21,    22,    23,    24,    25,    26,    27,    28,    29,
       0,    30,     0,     0,    88,   126,    81,    82,    83,    84,
      89,     0,     0,    76,    77,    31,    78,    79,    80,    76,
      77,    90,    78,    79,    80,     0,     0,    96,     0,     0,
      76,    77,     0,    78,    79,    80,    76,    77,     0,    78,
      79,    80,     0,     0,     0,    81,    82,    83,    84,     0,
       0,    81,    82,    83,    84,     0,     0,     0,     0,     0,
       0,     0,    81,    82,    83,    84,   103,     0,    81,    82,
      83,    84,   118,     0,     0,    76,    77,     0,    78,    79,
      80,    76,    77,   119,    78,    79,    80,     0,     0,   120,
       0,     0,    76,    77,     0,    78,    79,    80,    76,    77,
       0,    78,    79,    80,     0,     0,     0,    81,    82,    83,
      84,     0,     0,    81,    82,    83,    84,     0,     0,     0,
       0,     0,     0,     0,    81,    82,    83,    84,   125,     0,
      81,    82,    83,    84,   129,     0,     0,    76,    77,     0,
      78,    79,    80,    76,    77,   130,    78,    79,    80,     0,
       0,   131,     0,     0,    76,    77,     0,    78,    79,    80,
      76,    77,     0,    78,    79,    80,     0,     0,     0,    81,
      82,    83,    84,     0,     0,    81,    82,    83,    84,    76,
      77,     0,    78,    79,    80,     0,    81,    82,    83,    84,
     132,     0,    81,    82,    83,    84,     0,     0,     0,    76,
      77,     0,    78,    79,    80,     0,     0,   106,    78,    79,
      80,    81,    82,    83,    84,    76,    77,     0,    78,    79,
      80,     0,     0,    76,    77,     0,    78,    79,    80,     0,
       0,    81,    82,    83,    84,     0,     0,    81,    82,    83,
      84,     0,     0,   116,    -1,    -1,    -1,    81,    82,    83,
      84,   117,     0,     0,     0,    81,    82,    83,    84,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,    81,    82,    83,    84
};

static const yytype_int16 yycheck[] =
{
       3,     6,    35,     0,    13,    14,    15,    49,    50,     3,
      55,    55,    21,    42,     6,    42,    27,    17,    55,    27,
      27,    30,    55,    26,    27,    28,    29,    36,    27,    15,
      39,    40,    18,    19,    20,    35,    45,    27,    38,    39,
      49,    55,    42,     6,    53,    55,    55,    56,    48,    40,
      55,    44,     6,    53,    54,    55,     6,     6,     6,    68,
       6,    47,    48,    49,    50,     6,    40,    76,    77,    78,
      79,    80,    81,    82,    83,    84,    40,   116,   117,     4,
      40,    40,     7,     8,     9,    10,    11,    12,    13,    69,
      99,   100,   101,   102,   133,    14,    -1,    22,    23,    24,
      25,    26,   105,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    39,     6,    41,     7,     8,     9,
      10,    11,    12,    13,    15,    16,    -1,    18,    19,    20,
      55,    -1,    22,    23,    24,    25,    26,    -1,    28,    29,
      30,    31,    32,    33,    34,    35,    36,    37,    38,    39,
      -1,    41,    -1,    -1,     6,    45,    47,    48,    49,    50,
       6,    -1,    -1,    15,    16,    55,    18,    19,    20,    15,
      16,     6,    18,    19,    20,    -1,    -1,     6,    -1,    -1,
      15,    16,    -1,    18,    19,    20,    15,    16,    -1,    18,
      19,    20,    -1,    -1,    -1,    47,    48,    49,    50,    -1,
      -1,    47,    48,    49,    50,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    47,    48,    49,    50,     6,    -1,    47,    48,
      49,    50,     6,    -1,    -1,    15,    16,    -1,    18,    19,
      20,    15,    16,     6,    18,    19,    20,    -1,    -1,     6,
      -1,    -1,    15,    16,    -1,    18,    19,    20,    15,    16,
      -1,    18,    19,    20,    -1,    -1,    -1,    47,    48,    49,
      50,    -1,    -1,    47,    48,    49,    50,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    47,    48,    49,    50,     6,    -1,
      47,    48,    49,    50,     6,    -1,    -1,    15,    16,    -1,
      18,    19,    20,    15,    16,     6,    18,    19,    20,    -1,
      -1,     6,    -1,    -1,    15,    16,    -1,    18,    19,    20,
      15,    16,    -1,    18,    19,    20,    -1,    -1,    -1,    47,
      48,    49,    50,    -1,    -1,    47,    48,    49,    50,    15,
      16,    -1,    18,    19,    20,    -1,    47,    48,    49,    50,
       6,    -1,    47,    48,    49,    50,    -1,    -1,    -1,    15,
      16,    -1,    18,    19,    20,    -1,    -1,    43,    18,    19,
      20,    47,    48,    49,    50,    15,    16,    -1,    18,    19,
      20,    -1,    -1,    15,    16,    -1,    18,    19,    20,    -1,
      -1,    47,    48,    49,    50,    -1,    -1,    47,    48,    49,
      50,    -1,    -1,    43,    18,    19,    20,    47,    48,    49,
      50,    43,    -1,    -1,    -1,    47,    48,    49,    50,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    47,    48,    49,    50
};

/* YYSTOS[STATE-NUM] -- The symbol kind of the accessing symbol of
   state STATE-NUM.  */
static const yytype_int8 yystos[] =
{
       0,     3,    58,    59,     0,     4,     7,     8,     9,    10,
      11,    12,    13,    22,    23,    24,    25,    26,    28,    29,
      30,    31,    32,    33,    34,    35,    36,    37,    38,    39,
      41,    55,    60,    63,    55,    55,    17,    38,    39,    42,
      48,    53,    54,    63,    64,    42,     6,    55,     6,    42,
      64,    64,    64,    27,    27,    27,    27,    27,    64,    55,
      55,     6,    55,    63,    63,    63,    63,    64,    40,    44,
      61,     6,    64,    64,    64,     6,    15,    16,    18,    19,
      20,    47,    48,    49,    50,    64,     6,    64,     6,     6,
       6,    64,     6,    64,    64,     6,     6,     6,     6,    40,
      40,    40,    40,     6,    64,    59,    43,    64,    64,    64,
      64,    64,    64,    64,    64,    64,    43,    43,     6,     6,
       6,    64,    64,    64,    64,     6,    45,    61,    61,     6,
       6,     6,     6,    14,    62,    61
};

/* YYR1[RULE-NUM] -- Symbol kind of the left-hand side of rule RULE-NUM.  */
static const yytype_int8 yyr1[] =
{
       0,    57,    58,    59,    59,    60,    60,    60,    60,    60,
      60,    60,    60,    60,    60,    60,    60,    60,    60,    60,
      60,    60,    60,    60,    60,    60,    60,    60,    60,    60,
      60,    61,    62,    62,    63,    63,    64,    64,    64,    64,
      64,    64,    64,    64,    64,    64,    64,    64,    64,    64,
      64,    64,    64
};

/* YYR2[RULE-NUM] -- Number of symbols on the right-hand side of rule RULE-NUM.  */
static const yytype_int8 yyr2[] =
{
       0,     2,     3,     0,     2,     5,     5,     5,     5,     4,
       3,     3,     3,     3,     6,     5,     3,     3,     3,     3,
       2,     3,     2,     2,     3,     3,     4,     4,     3,     3,
       4,     3,     2,     0,     1,     2,     1,     1,     1,     1,
       1,     3,     2,     2,     3,     3,     3,     3,     3,     3,
       3,     3,     3
};


enum { YYENOMEM = -2 };

#define yyerrok         (yyerrstatus = 0)
#define yyclearin       (yychar = YYEMPTY)

#define YYACCEPT        goto yyacceptlab
#define YYABORT         goto yyabortlab
#define YYERROR         goto yyerrorlab
#define YYNOMEM         goto yyexhaustedlab


#define YYRECOVERING()  (!!yyerrstatus)

#define YYBACKUP(Token, Value)                                    \
  do                                                              \
    if (yychar == YYEMPTY)                                        \
      {                                                           \
        yychar = (Token);                                         \
        yylval = (Value);                                         \
        YYPOPSTACK (yylen);                                       \
        yystate = *yyssp;                                         \
        goto yybackup;                                            \
      }                                                           \
    else                                                          \
      {                                                           \
        yyerror (YY_("syntax error: cannot back up")); \
        YYERROR;                                                  \
      }                                                           \
  while (0)

/* Backward compatibility with an undocumented macro.
   Use YYerror or YYUNDEF. */
#define YYERRCODE YYUNDEF


/* Enable debugging if requested.  */
#if YYDEBUG

# ifndef YYFPRINTF
#  include <stdio.h> /* INFRINGES ON USER NAME SPACE */
#  define YYFPRINTF fprintf
# endif

# define YYDPRINTF(Args)                        \
do {                                            \
  if (yydebug)                                  \
    YYFPRINTF Args;                             \
} while (0)




# define YY_SYMBOL_PRINT(Title, Kind, Value, Location)                    \
do {                                                                      \
  if (yydebug)                                                            \
    {                                                                     \
      YYFPRINTF (stderr, "%s ", Title);                                   \
      yy_symbol_print (stderr,                                            \
                  Kind, Value); \
      YYFPRINTF (stderr, "\n");                                           \
    }                                                                     \
} while (0)


/*-----------------------------------.
| Print this symbol's value on YYO.  |
`-----------------------------------*/

static void
yy_symbol_value_print (FILE *yyo,
                       yysymbol_kind_t yykind, YYSTYPE const * const yyvaluep)
{
  FILE *yyoutput = yyo;
  YY_USE (yyoutput);
  if (!yyvaluep)
    return;
  YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN
  YY_USE (yykind);
  YY_IGNORE_MAYBE_UNINITIALIZED_END
}


/*---------------------------.
| Print this symbol on YYO.  |
`---------------------------*/

static void
yy_symbol_print (FILE *yyo,
                 yysymbol_kind_t yykind, YYSTYPE const * const yyvaluep)
{
  YYFPRINTF (yyo, "%s %s (",
             yykind < YYNTOKENS ? "token" : "nterm", yysymbol_name (yykind));

  yy_symbol_value_print (yyo, yykind, yyvaluep);
  YYFPRINTF (yyo, ")");
}

/*------------------------------------------------------------------.
| yy_stack_print -- Print the state stack from its BOTTOM up to its |
| TOP (included).                                                   |
`------------------------------------------------------------------*/

static void
yy_stack_print (yy_state_t *yybottom, yy_state_t *yytop)
{
  YYFPRINTF (stderr, "Stack now");
  for (; yybottom <= yytop; yybottom++)
    {
      int yybot = *yybottom;
      YYFPRINTF (stderr, " %d", yybot);
    }
  YYFPRINTF (stderr, "\n");
}

# define YY_STACK_PRINT(Bottom, Top)                            \
do {                                                            \
  if (yydebug)                                                  \
    yy_stack_print ((Bottom), (Top));                           \
} while (0)


/*------------------------------------------------.
| Report that the YYRULE is going to be reduced.  |
`------------------------------------------------*/

static void
yy_reduce_print (yy_state_t *yyssp, YYSTYPE *yyvsp,
                 int yyrule)
{
  int yylno = yyrline[yyrule];
  int yynrhs = yyr2[yyrule];
  int yyi;
  YYFPRINTF (stderr, "Reducing stack by rule %d (line %d):\n",
             yyrule - 1, yylno);
  /* The symbols being reduced.  */
  for (yyi = 0; yyi < yynrhs; yyi++)
    {
      YYFPRINTF (stderr, "   $%d = ", yyi + 1);
      yy_symbol_print (stderr,
                       YY_ACCESSING_SYMBOL (+yyssp[yyi + 1 - yynrhs]),
                       &yyvsp[(yyi + 1) - (yynrhs)]);
      YYFPRINTF (stderr, "\n");
    }
}

# define YY_REDUCE_PRINT(Rule)          \
do {                                    \
  if (yydebug)                          \
    yy_reduce_print (yyssp, yyvsp, Rule); \
} while (0)

/* Nonzero means print parse trace.  It is left uninitialized so that
   multiple parsers can coexist.  */
int yydebug;
#else /* !YYDEBUG */
# define YYDPRINTF(Args) ((void) 0)
# define YY_SYMBOL_PRINT(Title, Kind, Value, Location)
# define YY_STACK_PRINT(Bottom, Top)
# define YY_REDUCE_PRINT(Rule)
#endif /* !YYDEBUG */


/* YYINITDEPTH -- initial size of the parser's stacks.  */
#ifndef YYINITDEPTH
# define YYINITDEPTH 200
#endif

/* YYMAXDEPTH -- maximum size the stacks can grow to (effective only
   if the built-in stack extension method is used).

   Do not make this value too large; the results are undefined if
   YYSTACK_ALLOC_MAXIMUM < YYSTACK_BYTES (YYMAXDEPTH)
   evaluated with infinite-precision integer arithmetic.  */

#ifndef YYMAXDEPTH
# define YYMAXDEPTH 10000
#endif


/* Context of a parse error.  */
typedef struct
{
  yy_state_t *yyssp;
  yysymbol_kind_t yytoken;
} yypcontext_t;

/* Put in YYARG at most YYARGN of the expected tokens given the
   current YYCTX, and return the number of tokens stored in YYARG.  If
   YYARG is null, return the number of expected tokens (guaranteed to
   be less than YYNTOKENS).  Return YYENOMEM on memory exhaustion.
   Return 0 if there are more than YYARGN expected tokens, yet fill
   YYARG up to YYARGN. */
static int
yypcontext_expected_tokens (const yypcontext_t *yyctx,
                            yysymbol_kind_t yyarg[], int yyargn)
{
  /* Actual size of YYARG. */
  int yycount = 0;
  int yyn = yypact[+*yyctx->yyssp];
  if (!yypact_value_is_default (yyn))
    {
      /* Start YYX at -YYN if negative to avoid negative indexes in
         YYCHECK.  In other words, skip the first -YYN actions for
         this state because they are default actions.  */
      int yyxbegin = yyn < 0 ? -yyn : 0;
      /* Stay within bounds of both yycheck and yytname.  */
      int yychecklim = YYLAST - yyn + 1;
      int yyxend = yychecklim < YYNTOKENS ? yychecklim : YYNTOKENS;
      int yyx;
      for (yyx = yyxbegin; yyx < yyxend; ++yyx)
        if (yycheck[yyx + yyn] == yyx && yyx != YYSYMBOL_YYerror
            && !yytable_value_is_error (yytable[yyx + yyn]))
          {
            if (!yyarg)
              ++yycount;
            else if (yycount == yyargn)
              return 0;
            else
              yyarg[yycount++] = YY_CAST (yysymbol_kind_t, yyx);
          }
    }
  if (yyarg && yycount == 0 && 0 < yyargn)
    yyarg[0] = YYSYMBOL_YYEMPTY;
  return yycount;
}




#ifndef yystrlen
# if defined __GLIBC__ && defined _STRING_H
#  define yystrlen(S) (YY_CAST (YYPTRDIFF_T, strlen (S)))
# else
/* Return the length of YYSTR.  */
static YYPTRDIFF_T
yystrlen (const char *yystr)
{
  YYPTRDIFF_T yylen;
  for (yylen = 0; yystr[yylen]; yylen++)
    continue;
  return yylen;
}
# endif
#endif

#ifndef yystpcpy
# if defined __GLIBC__ && defined _STRING_H && defined _GNU_SOURCE
#  define yystpcpy stpcpy
# else
/* Copy YYSRC to YYDEST, returning the address of the terminating '\0' in
   YYDEST.  */
static char *
yystpcpy (char *yydest, const char *yysrc)
{
  char *yyd = yydest;
  const char *yys = yysrc;

  while ((*yyd++ = *yys++) != '\0')
    continue;

  return yyd - 1;
}
# endif
#endif

#ifndef yytnamerr
/* Copy to YYRES the contents of YYSTR after stripping away unnecessary
   quotes and backslashes, so that it's suitable for yyerror.  The
   heuristic is that double-quoting is unnecessary unless the string
   contains an apostrophe, a comma, or backslash (other than
   backslash-backslash).  YYSTR is taken from yytname.  If YYRES is
   null, do not copy; instead, return the length of what the result
   would have been.  */
static YYPTRDIFF_T
yytnamerr (char *yyres, const char *yystr)
{
  if (*yystr == '"')
    {
      YYPTRDIFF_T yyn = 0;
      char const *yyp = yystr;
      for (;;)
        switch (*++yyp)
          {
          case '\'':
          case ',':
            goto do_not_strip_quotes;

          case '\\':
            if (*++yyp != '\\')
              goto do_not_strip_quotes;
            else
              goto append;

          append:
          default:
            if (yyres)
              yyres[yyn] = *yyp;
            yyn++;
            break;

          case '"':
            if (yyres)
              yyres[yyn] = '\0';
            return yyn;
          }
    do_not_strip_quotes: ;
    }

  if (yyres)
    return yystpcpy (yyres, yystr) - yyres;
  else
    return yystrlen (yystr);
}
#endif


static int
yy_syntax_error_arguments (const yypcontext_t *yyctx,
                           yysymbol_kind_t yyarg[], int yyargn)
{
  /* Actual size of YYARG. */
  int yycount = 0;
  /* There are many possibilities here to consider:
     - If this state is a consistent state with a default action, then
       the only way this function was invoked is if the default action
       is an error action.  In that case, don't check for expected
       tokens because there are none.
     - The only way there can be no lookahead present (in yychar) is if
       this state is a consistent state with a default action.  Thus,
       detecting the absence of a lookahead is sufficient to determine
       that there is no unexpected or expected token to report.  In that
       case, just report a simple "syntax error".
     - Don't assume there isn't a lookahead just because this state is a
       consistent state with a default action.  There might have been a
       previous inconsistent state, consistent state with a non-default
       action, or user semantic action that manipulated yychar.
     - Of course, the expected token list depends on states to have
       correct lookahead information, and it depends on the parser not
       to perform extra reductions after fetching a lookahead from the
       scanner and before detecting a syntax error.  Thus, state merging
       (from LALR or IELR) and default reductions corrupt the expected
       token list.  However, the list is correct for canonical LR with
       one exception: it will still contain any token that will not be
       accepted due to an error action in a later state.
  */
  if (yyctx->yytoken != YYSYMBOL_YYEMPTY)
    {
      int yyn;
      if (yyarg)
        yyarg[yycount] = yyctx->yytoken;
      ++yycount;
      yyn = yypcontext_expected_tokens (yyctx,
                                        yyarg ? yyarg + 1 : yyarg, yyargn - 1);
      if (yyn == YYENOMEM)
        return YYENOMEM;
      else
        yycount += yyn;
    }
  return yycount;
}

/* Copy into *YYMSG, which is of size *YYMSG_ALLOC, an error message
   about the unexpected token YYTOKEN for the state stack whose top is
   YYSSP.

   Return 0 if *YYMSG was successfully written.  Return -1 if *YYMSG is
   not large enough to hold the message.  In that case, also set
   *YYMSG_ALLOC to the required number of bytes.  Return YYENOMEM if the
   required number of bytes is too large to store.  */
static int
yysyntax_error (YYPTRDIFF_T *yymsg_alloc, char **yymsg,
                const yypcontext_t *yyctx)
{
  enum { YYARGS_MAX = 5 };
  /* Internationalized format string. */
  const char *yyformat = YY_NULLPTR;
  /* Arguments of yyformat: reported tokens (one for the "unexpected",
     one per "expected"). */
  yysymbol_kind_t yyarg[YYARGS_MAX];
  /* Cumulated lengths of YYARG.  */
  YYPTRDIFF_T yysize = 0;

  /* Actual size of YYARG. */
  int yycount = yy_syntax_error_arguments (yyctx, yyarg, YYARGS_MAX);
  if (yycount == YYENOMEM)
    return YYENOMEM;

  switch (yycount)
    {
#define YYCASE_(N, S)                       \
      case N:                               \
        yyformat = S;                       \
        break
    default: /* Avoid compiler warnings. */
      YYCASE_(0, YY_("syntax error"));
      YYCASE_(1, YY_("syntax error, unexpected %s"));
      YYCASE_(2, YY_("syntax error, unexpected %s, expecting %s"));
      YYCASE_(3, YY_("syntax error, unexpected %s, expecting %s or %s"));
      YYCASE_(4, YY_("syntax error, unexpected %s, expecting %s or %s or %s"));
      YYCASE_(5, YY_("syntax error, unexpected %s, expecting %s or %s or %s or %s"));
#undef YYCASE_
    }

  /* Compute error message size.  Don't count the "%s"s, but reserve
     room for the terminator.  */
  yysize = yystrlen (yyformat) - 2 * yycount + 1;
  {
    int yyi;
    for (yyi = 0; yyi < yycount; ++yyi)
      {
        YYPTRDIFF_T yysize1
          = yysize + yytnamerr (YY_NULLPTR, yytname[yyarg[yyi]]);
        if (yysize <= yysize1 && yysize1 <= YYSTACK_ALLOC_MAXIMUM)
          yysize = yysize1;
        else
          return YYENOMEM;
      }
  }

  if (*yymsg_alloc < yysize)
    {
      *yymsg_alloc = 2 * yysize;
      if (! (yysize <= *yymsg_alloc
             && *yymsg_alloc <= YYSTACK_ALLOC_MAXIMUM))
        *yymsg_alloc = YYSTACK_ALLOC_MAXIMUM;
      return -1;
    }

  /* Avoid sprintf, as that infringes on the user's name space.
     Don't have undefined behavior even if the translation
     produced a string with the wrong number of "%s"s.  */
  {
    char *yyp = *yymsg;
    int yyi = 0;
    while ((*yyp = *yyformat) != '\0')
      if (*yyp == '%' && yyformat[1] == 's' && yyi < yycount)
        {
          yyp += yytnamerr (yyp, yytname[yyarg[yyi++]]);
          yyformat += 2;
        }
      else
        {
          ++yyp;
          ++yyformat;
        }
  }
  return 0;
}


/*-----------------------------------------------.
| Release the memory associated to this symbol.  |
`-----------------------------------------------*/

static void
yydestruct (const char *yymsg,
            yysymbol_kind_t yykind, YYSTYPE *yyvaluep)
{
  YY_USE (yyvaluep);
  if (!yymsg)
    yymsg = "Deleting";
  YY_SYMBOL_PRINT (yymsg, yykind, yyvaluep, yylocationp);

  YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN
  YY_USE (yykind);
  YY_IGNORE_MAYBE_UNINITIALIZED_END
}


/* Lookahead token kind.  */
int yychar;

/* The semantic value of the lookahead symbol.  */
YYSTYPE yylval;
/* Number of syntax errors so far.  */
int yynerrs;




/*----------.
| yyparse.  |
`----------*/

int
yyparse (void)
{
    yy_state_fast_t yystate = 0;
    /* Number of tokens to shift before error messages enabled.  */
    int yyerrstatus = 0;

    /* Refer to the stacks through separate pointers, to allow yyoverflow
       to reallocate them elsewhere.  */

    /* Their size.  */
    YYPTRDIFF_T yystacksize = YYINITDEPTH;

    /* The state stack: array, bottom, top.  */
    yy_state_t yyssa[YYINITDEPTH];
    yy_state_t *yyss = yyssa;
    yy_state_t *yyssp = yyss;

    /* The semantic value stack: array, bottom, top.  */
    YYSTYPE yyvsa[YYINITDEPTH];
    YYSTYPE *yyvs = yyvsa;
    YYSTYPE *yyvsp = yyvs;

  int yyn;
  /* The return value of yyparse.  */
  int yyresult;
  /* Lookahead symbol kind.  */
  yysymbol_kind_t yytoken = YYSYMBOL_YYEMPTY;
  /* The variables used to return semantic value and location from the
     action routines.  */
  YYSTYPE yyval;

  /* Buffer for error messages, and its allocated size.  */
  char yymsgbuf[128];
  char *yymsg = yymsgbuf;
  YYPTRDIFF_T yymsg_alloc = sizeof yymsgbuf;

#define YYPOPSTACK(N)   (yyvsp -= (N), yyssp -= (N))

  /* The number of symbols on the RHS of the reduced rule.
     Keep to zero when no symbol should be popped.  */
  int yylen = 0;

  YYDPRINTF ((stderr, "Starting parse\n"));

  yychar = YYEMPTY; /* Cause a token to be read.  */

  goto yysetstate;


/*------------------------------------------------------------.
| yynewstate -- push a new state, which is found in yystate.  |
`------------------------------------------------------------*/
yynewstate:
  /* In all cases, when you get here, the value and location stacks
     have just been pushed.  So pushing a state here evens the stacks.  */
  yyssp++;


/*--------------------------------------------------------------------.
| yysetstate -- set current state (the top of the stack) to yystate.  |
`--------------------------------------------------------------------*/
yysetstate:
  YYDPRINTF ((stderr, "Entering state %d\n", yystate));
  YY_ASSERT (0 <= yystate && yystate < YYNSTATES);
  YY_IGNORE_USELESS_CAST_BEGIN
  *yyssp = YY_CAST (yy_state_t, yystate);
  YY_IGNORE_USELESS_CAST_END
  YY_STACK_PRINT (yyss, yyssp);

  if (yyss + yystacksize - 1 <= yyssp)
#if !defined yyoverflow && !defined YYSTACK_RELOCATE
    YYNOMEM;
#else
    {
      /* Get the current used size of the three stacks, in elements.  */
      YYPTRDIFF_T yysize = yyssp - yyss + 1;

# if defined yyoverflow
      {
        /* Give user a chance to reallocate the stack.  Use copies of
           these so that the &'s don't force the real ones into
           memory.  */
        yy_state_t *yyss1 = yyss;
        YYSTYPE *yyvs1 = yyvs;

        /* Each stack pointer address is followed by the size of the
           data in use in that stack, in bytes.  This used to be a
           conditional around just the two extra args, but that might
           be undefined if yyoverflow is a macro.  */
        yyoverflow (YY_("memory exhausted"),
                    &yyss1, yysize * YYSIZEOF (*yyssp),
                    &yyvs1, yysize * YYSIZEOF (*yyvsp),
                    &yystacksize);
        yyss = yyss1;
        yyvs = yyvs1;
      }
# else /* defined YYSTACK_RELOCATE */
      /* Extend the stack our own way.  */
      if (YYMAXDEPTH <= yystacksize)
        YYNOMEM;
      yystacksize *= 2;
      if (YYMAXDEPTH < yystacksize)
        yystacksize = YYMAXDEPTH;

      {
        yy_state_t *yyss1 = yyss;
        union yyalloc *yyptr =
          YY_CAST (union yyalloc *,
                   YYSTACK_ALLOC (YY_CAST (YYSIZE_T, YYSTACK_BYTES (yystacksize))));
        if (! yyptr)
          YYNOMEM;
        YYSTACK_RELOCATE (yyss_alloc, yyss);
        YYSTACK_RELOCATE (yyvs_alloc, yyvs);
#  undef YYSTACK_RELOCATE
        if (yyss1 != yyssa)
          YYSTACK_FREE (yyss1);
      }
# endif

      yyssp = yyss + yysize - 1;
      yyvsp = yyvs + yysize - 1;

      YY_IGNORE_USELESS_CAST_BEGIN
      YYDPRINTF ((stderr, "Stack size increased to %ld\n",
                  YY_CAST (long, yystacksize)));
      YY_IGNORE_USELESS_CAST_END

      if (yyss + yystacksize - 1 <= yyssp)
        YYABORT;
    }
#endif /* !defined yyoverflow && !defined YYSTACK_RELOCATE */


  if (yystate == YYFINAL)
    YYACCEPT;

  goto yybackup;


/*-----------.
| yybackup.  |
`-----------*/
yybackup:
  /* Do appropriate processing given the current state.  Read a
     lookahead token if we need one and don't already have one.  */

  /* First try to decide what to do without reference to lookahead token.  */
  yyn = yypact[yystate];
  if (yypact_value_is_default (yyn))
    goto yydefault;

  /* Not known => get a lookahead token if don't already have one.  */

  /* YYCHAR is either empty, or end-of-input, or a valid lookahead.  */
  if (yychar == YYEMPTY)
    {
      YYDPRINTF ((stderr, "Reading a token\n"));
      yychar = yylex ();
    }

  if (yychar <= YYEOF)
    {
      yychar = YYEOF;
      yytoken = YYSYMBOL_YYEOF;
      YYDPRINTF ((stderr, "Now at end of input.\n"));
    }
  else if (yychar == YYerror)
    {
      /* The scanner already issued an error message, process directly
         to error recovery.  But do not keep the error token as
         lookahead, it is too special and may lead us to an endless
         loop in error recovery. */
      yychar = YYUNDEF;
      yytoken = YYSYMBOL_YYerror;
      goto yyerrlab1;
    }
  else
    {
      yytoken = YYTRANSLATE (yychar);
      YY_SYMBOL_PRINT ("Next token is", yytoken, &yylval, &yylloc);
    }

  /* If the proper action on seeing token YYTOKEN is to reduce or to
     detect an error, take that action.  */
  yyn += yytoken;
  if (yyn < 0 || YYLAST < yyn || yycheck[yyn] != yytoken)
    goto yydefault;
  yyn = yytable[yyn];
  if (yyn <= 0)
    {
      if (yytable_value_is_error (yyn))
        goto yyerrlab;
      yyn = -yyn;
      goto yyreduce;
    }

  /* Count tokens shifted since error; after three, turn off error
     status.  */
  if (yyerrstatus)
    yyerrstatus--;

  /* Shift the lookahead token.  */
  YY_SYMBOL_PRINT ("Shifting", yytoken, &yylval, &yylloc);
  yystate = yyn;
  YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN
  *++yyvsp = yylval;
  YY_IGNORE_MAYBE_UNINITIALIZED_END

  /* Discard the shifted token.  */
  yychar = YYEMPTY;
  goto yynewstate;


/*-----------------------------------------------------------.
| yydefault -- do the default action for the current state.  |
`-----------------------------------------------------------*/
yydefault:
  yyn = yydefact[yystate];
  if (yyn == 0)
    goto yyerrlab;
  goto yyreduce;


/*-----------------------------.
| yyreduce -- do a reduction.  |
`-----------------------------*/
yyreduce:
  /* yyn is the number of a rule to reduce with.  */
  yylen = yyr2[yyn];

  /* If YYLEN is nonzero, implement the default value of the action:
     '$$ = $1'.

     Otherwise, the following line sets YYVAL to garbage.
     This behavior is undocumented and Bison
     users should not rely upon it.  Assigning to YYVAL
     unconditionally makes the parser a bit smaller, and it avoids a
     GCC warning that YYVAL may be used uninitialized.  */
  yyval = yyvsp[1-yylen];


  YY_REDUCE_PRINT (yyn);
  switch (yyn)
    {
  case 2: /* program: START stmt_list END  */
#line 778 "emoji_flow.y"
      {
          g_root = new_node(NODE_PROGRAM, NULL, 0, new_node(NODE_BLOCK, NULL, 0, (yyvsp[-1].node), NULL, NULL), NULL, NULL);
          (yyval.node) = g_root;
      }
#line 2272 "emoji_flow.tab.c"
    break;

  case 3: /* stmt_list: %empty  */
#line 786 "emoji_flow.y"
      {
          (yyval.node) = NULL;
      }
#line 2280 "emoji_flow.tab.c"
    break;

  case 4: /* stmt_list: stmt_list stmt  */
#line 790 "emoji_flow.y"
      {
          (yyval.node) = append_stmt((yyvsp[-1].node), (yyvsp[0].node));
      }
#line 2288 "emoji_flow.tab.c"
    break;

  case 5: /* stmt: INT_TYPE var_ref ASSIGN expr SEMI  */
#line 797 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_DECL, "INT", 0, (yyvsp[-3].node), (yyvsp[-1].node), NULL); }
#line 2294 "emoji_flow.tab.c"
    break;

  case 6: /* stmt: STRING_TYPE var_ref ASSIGN expr SEMI  */
#line 799 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_DECL, "STRING", 0, (yyvsp[-3].node), (yyvsp[-1].node), NULL); }
#line 2300 "emoji_flow.tab.c"
    break;

  case 7: /* stmt: TRUE_VAL var_ref ASSIGN expr SEMI  */
#line 801 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_DECL, "BOOL", 0, (yyvsp[-3].node), (yyvsp[-1].node), NULL); }
#line 2306 "emoji_flow.tab.c"
    break;

  case 8: /* stmt: FALSE_VAL var_ref ASSIGN expr SEMI  */
#line 803 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_DECL, "BOOL", 0, (yyvsp[-3].node), (yyvsp[-1].node), NULL); }
#line 2312 "emoji_flow.tab.c"
    break;

  case 9: /* stmt: var_ref ASSIGN expr SEMI  */
#line 805 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_ASSIGN, NULL, 0, (yyvsp[-3].node), (yyvsp[-1].node), NULL); }
#line 2318 "emoji_flow.tab.c"
    break;

  case 10: /* stmt: OUTPUT expr SEMI  */
#line 807 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_OUTPUT, NULL, 0, (yyvsp[-1].node), NULL, NULL); }
#line 2324 "emoji_flow.tab.c"
    break;

  case 11: /* stmt: LOG expr SEMI  */
#line 809 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_LOG, NULL, 0, (yyvsp[-1].node), NULL, NULL); }
#line 2330 "emoji_flow.tab.c"
    break;

  case 12: /* stmt: ALERT expr SEMI  */
#line 811 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_ALERT, NULL, 0, (yyvsp[-1].node), NULL, NULL); }
#line 2336 "emoji_flow.tab.c"
    break;

  case 13: /* stmt: ERROR_TOK expr SEMI  */
#line 813 "emoji_flow.y"
            { (yyval.node) = new_node(NODE_ALERT, "ERROR", 0, (yyvsp[-1].node), NULL, NULL); }
#line 2342 "emoji_flow.tab.c"
    break;

  case 14: /* stmt: IF LPAREN expr RPAREN block else_opt  */
#line 815 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_IF, NULL, 0, (yyvsp[-3].node), (yyvsp[-1].node), (yyvsp[0].node)); }
#line 2348 "emoji_flow.tab.c"
    break;

  case 15: /* stmt: LOOP LPAREN expr RPAREN block  */
#line 817 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_LOOP, NULL, 0, (yyvsp[-2].node), (yyvsp[0].node), NULL); }
#line 2354 "emoji_flow.tab.c"
    break;

  case 16: /* stmt: TASK IDENTIFIER block  */
#line 819 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_TASK_DEF, (yyvsp[-1].sval), 0, (yyvsp[0].node), NULL, NULL); }
#line 2360 "emoji_flow.tab.c"
    break;

  case 17: /* stmt: RUN IDENTIFIER SEMI  */
#line 821 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_TASK_CALL, (yyvsp[-1].sval), 0, NULL, NULL, NULL); }
#line 2366 "emoji_flow.tab.c"
    break;

  case 18: /* stmt: SCHEDULE IDENTIFIER SEMI  */
#line 823 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_TASK_CALL, (yyvsp[-1].sval), 0, NULL, NULL, NULL); }
#line 2372 "emoji_flow.tab.c"
    break;

  case 19: /* stmt: NEXT IDENTIFIER SEMI  */
#line 825 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_TASK_CALL, (yyvsp[-1].sval), 0, NULL, NULL, NULL); }
#line 2378 "emoji_flow.tab.c"
    break;

  case 20: /* stmt: PREV SEMI  */
#line 827 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_NOP, "PREV", 0, NULL, NULL, NULL); }
#line 2384 "emoji_flow.tab.c"
    break;

  case 21: /* stmt: STOP IDENTIFIER SEMI  */
#line 829 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_STOP, (yyvsp[-1].sval), 0, NULL, NULL, NULL); }
#line 2390 "emoji_flow.tab.c"
    break;

  case 22: /* stmt: STOP SEMI  */
#line 831 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_STOP, NULL, 0, NULL, NULL, NULL); }
#line 2396 "emoji_flow.tab.c"
    break;

  case 23: /* stmt: RETRY SEMI  */
#line 833 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_RETRY, NULL, 0, NULL, NULL, NULL); }
#line 2402 "emoji_flow.tab.c"
    break;

  case 24: /* stmt: WAIT expr SEMI  */
#line 835 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_WAIT, NULL, 0, (yyvsp[-1].node), NULL, NULL); }
#line 2408 "emoji_flow.tab.c"
    break;

  case 25: /* stmt: TIME expr SEMI  */
#line 837 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_TIME, NULL, 0, (yyvsp[-1].node), NULL, NULL); }
#line 2414 "emoji_flow.tab.c"
    break;

  case 26: /* stmt: OPEN FILE_TOK expr SEMI  */
#line 839 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_FILE_OPEN, NULL, 0, (yyvsp[-1].node), NULL, NULL); }
#line 2420 "emoji_flow.tab.c"
    break;

  case 27: /* stmt: WRITE FILE_TOK expr SEMI  */
#line 841 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_FILE_WRITE, NULL, 0, (yyvsp[-1].node), NULL, NULL); }
#line 2426 "emoji_flow.tab.c"
    break;

  case 28: /* stmt: READ FILE_TOK SEMI  */
#line 843 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_FILE_READ, NULL, 0, NULL, NULL, NULL); }
#line 2432 "emoji_flow.tab.c"
    break;

  case 29: /* stmt: CLOSE FILE_TOK SEMI  */
#line 845 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_FILE_CLOSE, NULL, 0, NULL, NULL, NULL); }
#line 2438 "emoji_flow.tab.c"
    break;

  case 30: /* stmt: DELETE FILE_TOK expr SEMI  */
#line 847 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_FILE_DELETE, NULL, 0, (yyvsp[-1].node), NULL, NULL); }
#line 2444 "emoji_flow.tab.c"
    break;

  case 31: /* block: LBRACE stmt_list RBRACE  */
#line 852 "emoji_flow.y"
      {
          (yyval.node) = new_node(NODE_BLOCK, NULL, 0, (yyvsp[-1].node), NULL, NULL);
      }
#line 2452 "emoji_flow.tab.c"
    break;

  case 32: /* else_opt: ELSE block  */
#line 859 "emoji_flow.y"
      {
          (yyval.node) = (yyvsp[0].node);
      }
#line 2460 "emoji_flow.tab.c"
    break;

  case 33: /* else_opt: %empty  */
#line 863 "emoji_flow.y"
      {
          (yyval.node) = NULL;
      }
#line 2468 "emoji_flow.tab.c"
    break;

  case 34: /* var_ref: IDENTIFIER  */
#line 870 "emoji_flow.y"
      {
          (yyval.node) = new_node(NODE_VAR, (yyvsp[0].sval), 0, NULL, NULL, NULL);
      }
#line 2476 "emoji_flow.tab.c"
    break;

  case 35: /* var_ref: ID IDENTIFIER  */
#line 874 "emoji_flow.y"
      {
          (yyval.node) = new_node(NODE_VAR, (yyvsp[0].sval), 0, NULL, NULL, NULL);
      }
#line 2484 "emoji_flow.tab.c"
    break;

  case 36: /* expr: NUMBER  */
#line 881 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_INT, NULL, (yyvsp[0].ival), NULL, NULL, NULL); }
#line 2490 "emoji_flow.tab.c"
    break;

  case 37: /* expr: STRING_LIT  */
#line 883 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_STRING, (yyvsp[0].sval), 0, NULL, NULL, NULL); }
#line 2496 "emoji_flow.tab.c"
    break;

  case 38: /* expr: TRUE_VAL  */
#line 885 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_BOOL, NULL, 1, NULL, NULL, NULL); }
#line 2502 "emoji_flow.tab.c"
    break;

  case 39: /* expr: FALSE_VAL  */
#line 887 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_BOOL, NULL, 0, NULL, NULL, NULL); }
#line 2508 "emoji_flow.tab.c"
    break;

  case 40: /* expr: var_ref  */
#line 889 "emoji_flow.y"
      { (yyval.node) = (yyvsp[0].node); }
#line 2514 "emoji_flow.tab.c"
    break;

  case 41: /* expr: LPAREN expr RPAREN  */
#line 891 "emoji_flow.y"
      { (yyval.node) = (yyvsp[-1].node); }
#line 2520 "emoji_flow.tab.c"
    break;

  case 42: /* expr: NOT expr  */
#line 893 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_UNOP, "NOT", 0, (yyvsp[0].node), NULL, NULL); }
#line 2526 "emoji_flow.tab.c"
    break;

  case 43: /* expr: MINUS expr  */
#line 895 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_UNOP, "NEG", 0, (yyvsp[0].node), NULL, NULL); }
#line 2532 "emoji_flow.tab.c"
    break;

  case 44: /* expr: expr PLUS expr  */
#line 897 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_BINOP, "PLUS", 0, (yyvsp[-2].node), (yyvsp[0].node), NULL); }
#line 2538 "emoji_flow.tab.c"
    break;

  case 45: /* expr: expr MINUS expr  */
#line 899 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_BINOP, "MINUS", 0, (yyvsp[-2].node), (yyvsp[0].node), NULL); }
#line 2544 "emoji_flow.tab.c"
    break;

  case 46: /* expr: expr MULTIPLY expr  */
#line 901 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_BINOP, "MUL", 0, (yyvsp[-2].node), (yyvsp[0].node), NULL); }
#line 2550 "emoji_flow.tab.c"
    break;

  case 47: /* expr: expr DIVIDE expr  */
#line 903 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_BINOP, "DIV", 0, (yyvsp[-2].node), (yyvsp[0].node), NULL); }
#line 2556 "emoji_flow.tab.c"
    break;

  case 48: /* expr: expr GT expr  */
#line 905 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_BINOP, "GT", 0, (yyvsp[-2].node), (yyvsp[0].node), NULL); }
#line 2562 "emoji_flow.tab.c"
    break;

  case 49: /* expr: expr LT expr  */
#line 907 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_BINOP, "LT", 0, (yyvsp[-2].node), (yyvsp[0].node), NULL); }
#line 2568 "emoji_flow.tab.c"
    break;

  case 50: /* expr: expr EQ expr  */
#line 909 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_BINOP, "EQ", 0, (yyvsp[-2].node), (yyvsp[0].node), NULL); }
#line 2574 "emoji_flow.tab.c"
    break;

  case 51: /* expr: expr AND expr  */
#line 911 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_BINOP, "AND", 0, (yyvsp[-2].node), (yyvsp[0].node), NULL); }
#line 2580 "emoji_flow.tab.c"
    break;

  case 52: /* expr: expr OR expr  */
#line 913 "emoji_flow.y"
      { (yyval.node) = new_node(NODE_BINOP, "OR", 0, (yyvsp[-2].node), (yyvsp[0].node), NULL); }
#line 2586 "emoji_flow.tab.c"
    break;


#line 2590 "emoji_flow.tab.c"

      default: break;
    }
  /* User semantic actions sometimes alter yychar, and that requires
     that yytoken be updated with the new translation.  We take the
     approach of translating immediately before every use of yytoken.
     One alternative is translating here after every semantic action,
     but that translation would be missed if the semantic action invokes
     YYABORT, YYACCEPT, or YYERROR immediately after altering yychar or
     if it invokes YYBACKUP.  In the case of YYABORT or YYACCEPT, an
     incorrect destructor might then be invoked immediately.  In the
     case of YYERROR or YYBACKUP, subsequent parser actions might lead
     to an incorrect destructor call or verbose syntax error message
     before the lookahead is translated.  */
  YY_SYMBOL_PRINT ("-> $$ =", YY_CAST (yysymbol_kind_t, yyr1[yyn]), &yyval, &yyloc);

  YYPOPSTACK (yylen);
  yylen = 0;

  *++yyvsp = yyval;

  /* Now 'shift' the result of the reduction.  Determine what state
     that goes to, based on the state we popped back to and the rule
     number reduced by.  */
  {
    const int yylhs = yyr1[yyn] - YYNTOKENS;
    const int yyi = yypgoto[yylhs] + *yyssp;
    yystate = (0 <= yyi && yyi <= YYLAST && yycheck[yyi] == *yyssp
               ? yytable[yyi]
               : yydefgoto[yylhs]);
  }

  goto yynewstate;


/*--------------------------------------.
| yyerrlab -- here on detecting error.  |
`--------------------------------------*/
yyerrlab:
  /* Make sure we have latest lookahead translation.  See comments at
     user semantic actions for why this is necessary.  */
  yytoken = yychar == YYEMPTY ? YYSYMBOL_YYEMPTY : YYTRANSLATE (yychar);
  /* If not already recovering from an error, report this error.  */
  if (!yyerrstatus)
    {
      ++yynerrs;
      {
        yypcontext_t yyctx
          = {yyssp, yytoken};
        char const *yymsgp = YY_("syntax error");
        int yysyntax_error_status;
        yysyntax_error_status = yysyntax_error (&yymsg_alloc, &yymsg, &yyctx);
        if (yysyntax_error_status == 0)
          yymsgp = yymsg;
        else if (yysyntax_error_status == -1)
          {
            if (yymsg != yymsgbuf)
              YYSTACK_FREE (yymsg);
            yymsg = YY_CAST (char *,
                             YYSTACK_ALLOC (YY_CAST (YYSIZE_T, yymsg_alloc)));
            if (yymsg)
              {
                yysyntax_error_status
                  = yysyntax_error (&yymsg_alloc, &yymsg, &yyctx);
                yymsgp = yymsg;
              }
            else
              {
                yymsg = yymsgbuf;
                yymsg_alloc = sizeof yymsgbuf;
                yysyntax_error_status = YYENOMEM;
              }
          }
        yyerror (yymsgp);
        if (yysyntax_error_status == YYENOMEM)
          YYNOMEM;
      }
    }

  if (yyerrstatus == 3)
    {
      /* If just tried and failed to reuse lookahead token after an
         error, discard it.  */

      if (yychar <= YYEOF)
        {
          /* Return failure if at end of input.  */
          if (yychar == YYEOF)
            YYABORT;
        }
      else
        {
          yydestruct ("Error: discarding",
                      yytoken, &yylval);
          yychar = YYEMPTY;
        }
    }

  /* Else will try to reuse lookahead token after shifting the error
     token.  */
  goto yyerrlab1;


/*---------------------------------------------------.
| yyerrorlab -- error raised explicitly by YYERROR.  |
`---------------------------------------------------*/
yyerrorlab:
  /* Pacify compilers when the user code never invokes YYERROR and the
     label yyerrorlab therefore never appears in user code.  */
  if (0)
    YYERROR;
  ++yynerrs;

  /* Do not reclaim the symbols of the rule whose action triggered
     this YYERROR.  */
  YYPOPSTACK (yylen);
  yylen = 0;
  YY_STACK_PRINT (yyss, yyssp);
  yystate = *yyssp;
  goto yyerrlab1;


/*-------------------------------------------------------------.
| yyerrlab1 -- common code for both syntax error and YYERROR.  |
`-------------------------------------------------------------*/
yyerrlab1:
  yyerrstatus = 3;      /* Each real token shifted decrements this.  */

  /* Pop stack until we find a state that shifts the error token.  */
  for (;;)
    {
      yyn = yypact[yystate];
      if (!yypact_value_is_default (yyn))
        {
          yyn += YYSYMBOL_YYerror;
          if (0 <= yyn && yyn <= YYLAST && yycheck[yyn] == YYSYMBOL_YYerror)
            {
              yyn = yytable[yyn];
              if (0 < yyn)
                break;
            }
        }

      /* Pop the current state because it cannot handle the error token.  */
      if (yyssp == yyss)
        YYABORT;


      yydestruct ("Error: popping",
                  YY_ACCESSING_SYMBOL (yystate), yyvsp);
      YYPOPSTACK (1);
      yystate = *yyssp;
      YY_STACK_PRINT (yyss, yyssp);
    }

  YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN
  *++yyvsp = yylval;
  YY_IGNORE_MAYBE_UNINITIALIZED_END


  /* Shift the error token.  */
  YY_SYMBOL_PRINT ("Shifting", YY_ACCESSING_SYMBOL (yyn), yyvsp, yylsp);

  yystate = yyn;
  goto yynewstate;


/*-------------------------------------.
| yyacceptlab -- YYACCEPT comes here.  |
`-------------------------------------*/
yyacceptlab:
  yyresult = 0;
  goto yyreturnlab;


/*-----------------------------------.
| yyabortlab -- YYABORT comes here.  |
`-----------------------------------*/
yyabortlab:
  yyresult = 1;
  goto yyreturnlab;


/*-----------------------------------------------------------.
| yyexhaustedlab -- YYNOMEM (memory exhaustion) comes here.  |
`-----------------------------------------------------------*/
yyexhaustedlab:
  yyerror (YY_("memory exhausted"));
  yyresult = 2;
  goto yyreturnlab;


/*----------------------------------------------------------.
| yyreturnlab -- parsing is finished, clean up and return.  |
`----------------------------------------------------------*/
yyreturnlab:
  if (yychar != YYEMPTY)
    {
      /* Make sure we have latest lookahead translation.  See comments at
         user semantic actions for why this is necessary.  */
      yytoken = YYTRANSLATE (yychar);
      yydestruct ("Cleanup: discarding lookahead",
                  yytoken, &yylval);
    }
  /* Do not reclaim the symbols of the rule whose action triggered
     this YYABORT or YYACCEPT.  */
  YYPOPSTACK (yylen);
  YY_STACK_PRINT (yyss, yyssp);
  while (yyssp != yyss)
    {
      yydestruct ("Cleanup: popping",
                  YY_ACCESSING_SYMBOL (+*yyssp), yyvsp);
      YYPOPSTACK (1);
    }
#ifndef yyoverflow
  if (yyss != yyssa)
    YYSTACK_FREE (yyss);
#endif
  if (yymsg != yymsgbuf)
    YYSTACK_FREE (yymsg);
  return yyresult;
}

#line 916 "emoji_flow.y"


void yyerror(const char *s) {
    fprintf(stderr, "Parser error at line %d: %s\n", yylineno, s);
}

int main(int argc, char **argv) {
    if (argc < 2) {
        fprintf(stderr, "Usage: %s <input_file> [output_file]\n", argv[0]);
        fprintf(stderr, "  input_file  : EMOJI-FLOW source file (.emoji or .ef)\n");
        fprintf(stderr, "  output_file : Token output file (default: tokens.txt)\n");
        return 1;
    }

    FILE *input_file = fopen(argv[1], "r");
    if (!input_file) {
        fprintf(stderr, "Error: Cannot open input file '%s'\n", argv[1]);
        return 1;
    }

    const char *output_filename = (argc >= 3) ? argv[2] : "tokens.txt";
    output_file = fopen(output_filename, "w");
    if (!output_file) {
        fprintf(stderr, "Error: Cannot create output file '%s'\n", output_filename);
        fclose(input_file);
        return 1;
    }

    fprintf(output_file, "# EMOJI-FLOW Token Stream\n");
    fprintf(output_file, "# Generated from: %s\n", argv[1]);
    fprintf(output_file, "# Format: <TOKEN_TYPE, LEXEME>\n\n");

    printf("==============================================\n");
    printf("  EMOJI-FLOW Flex/Bison Processor v1.0\n");
    printf("  Input:  %s\n", argv[1]);
    printf("  Output: %s\n", output_filename);
    printf("==============================================\n\n");

    yyin = input_file;

    if (yyparse() != 0) {
        fprintf(stderr, "Parsing failed for '%s'\n", argv[1]);
        fclose(input_file);
        fclose(output_file);
        return 1;
    }

    fprintf(output_file, "\n# Parse result: SUCCESS\n");

    {
        FILE *ast_file = fopen("parse_tree.txt", "w");
        if (ast_file && g_root) {
            fprintf(ast_file, "# EMOJI-FLOW Parse Tree\n");
            fprintf(ast_file, "# Input: %s\n\n", argv[1]);
            print_ast(ast_file, g_root, 0);
            fclose(ast_file);
        }
    }

    if (g_root && g_root->a) {
        printf("\n==============================================\n");
        printf("  Semantic Execution Trace\n");
        printf("==============================================\n");
        exec_stmt_list(g_root->a->a);
    }

    if (g_semantic_error_count > 0) {
        fprintf(output_file, "\n# Semantic result: FAILED (%d error%s)\n",
                g_semantic_error_count,
                g_semantic_error_count == 1 ? "" : "s");
        fprintf(stderr, "Semantic phase failed with %d error%s\n",
                g_semantic_error_count,
                g_semantic_error_count == 1 ? "" : "s");
        fclose(input_file);
        fclose(output_file);
        return 1;
    }

    printf("\n==============================================\n");
    printf("  Compilation and execution complete!\n");
    printf("  Total lines processed: %d\n", line_num);
    printf("  Output written to: %s\n", output_filename);
    printf("  Parse tree written to: parse_tree.txt\n");
    printf("==============================================\n");

    fclose(input_file);
    fclose(output_file);
    return 0;
}
