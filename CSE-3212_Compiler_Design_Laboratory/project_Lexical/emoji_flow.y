%{
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
%}

%define parse.error verbose

%code requires {
    typedef struct ASTNode ASTNode;
}

%union {
    int ival;
    char *sval;
    ASTNode *node;
}

%token START END BLOCK SEMI TASK RUN WAIT LOOP STOP RETRY
%token IF ELSE AND OR NOT GT LT EQ
%token INPUT OUTPUT LOG ALERT OPEN CLOSE FILE_TOK DELETE WRITE READ
%token TIME SCHEDULE NEXT PREV ID INT_TYPE STRING_TYPE TRUE_VAL FALSE_VAL
%token ASSIGN ERROR_TOK LPAREN RPAREN
%token LBRACE RBRACE COMMA PLUS MINUS MULTIPLY DIVIDE COLON NEWLINE

%token <ival> NUMBER
%token <sval> STRING_LIT
%token <sval> IDENTIFIER

%type <node> program stmt_list stmt block else_opt expr var_ref

%left OR
%left AND
%right NOT
%nonassoc GT LT EQ
%left PLUS MINUS
%left MULTIPLY DIVIDE
%right UMINUS

%%

program
    : START stmt_list END
      {
          g_root = new_node(NODE_PROGRAM, NULL, 0, new_node(NODE_BLOCK, NULL, 0, $2, NULL, NULL), NULL, NULL);
          $$ = g_root;
      }
    ;

stmt_list
    : /* empty */
      {
          $$ = NULL;
      }
    | stmt_list stmt
      {
          $$ = append_stmt($1, $2);
      }
    ;

stmt
    : INT_TYPE var_ref ASSIGN expr SEMI
      { $$ = new_node(NODE_DECL, "INT", 0, $2, $4, NULL); }
    | STRING_TYPE var_ref ASSIGN expr SEMI
      { $$ = new_node(NODE_DECL, "STRING", 0, $2, $4, NULL); }
    | TRUE_VAL var_ref ASSIGN expr SEMI
      { $$ = new_node(NODE_DECL, "BOOL", 0, $2, $4, NULL); }
    | FALSE_VAL var_ref ASSIGN expr SEMI
      { $$ = new_node(NODE_DECL, "BOOL", 0, $2, $4, NULL); }
    | var_ref ASSIGN expr SEMI
      { $$ = new_node(NODE_ASSIGN, NULL, 0, $1, $3, NULL); }
    | OUTPUT expr SEMI
      { $$ = new_node(NODE_OUTPUT, NULL, 0, $2, NULL, NULL); }
    | LOG expr SEMI
      { $$ = new_node(NODE_LOG, NULL, 0, $2, NULL, NULL); }
    | ALERT expr SEMI
      { $$ = new_node(NODE_ALERT, NULL, 0, $2, NULL, NULL); }
        | ERROR_TOK expr SEMI
            { $$ = new_node(NODE_ALERT, "ERROR", 0, $2, NULL, NULL); }
    | IF LPAREN expr RPAREN block else_opt
      { $$ = new_node(NODE_IF, NULL, 0, $3, $5, $6); }
    | LOOP LPAREN expr RPAREN block
      { $$ = new_node(NODE_LOOP, NULL, 0, $3, $5, NULL); }
    | TASK IDENTIFIER block
      { $$ = new_node(NODE_TASK_DEF, $2, 0, $3, NULL, NULL); }
    | RUN IDENTIFIER SEMI
      { $$ = new_node(NODE_TASK_CALL, $2, 0, NULL, NULL, NULL); }
    | SCHEDULE IDENTIFIER SEMI
      { $$ = new_node(NODE_TASK_CALL, $2, 0, NULL, NULL, NULL); }
    | NEXT IDENTIFIER SEMI
      { $$ = new_node(NODE_TASK_CALL, $2, 0, NULL, NULL, NULL); }
    | PREV SEMI
      { $$ = new_node(NODE_NOP, "PREV", 0, NULL, NULL, NULL); }
    | STOP IDENTIFIER SEMI
      { $$ = new_node(NODE_STOP, $2, 0, NULL, NULL, NULL); }
    | STOP SEMI
      { $$ = new_node(NODE_STOP, NULL, 0, NULL, NULL, NULL); }
    | RETRY SEMI
      { $$ = new_node(NODE_RETRY, NULL, 0, NULL, NULL, NULL); }
    | WAIT expr SEMI
      { $$ = new_node(NODE_WAIT, NULL, 0, $2, NULL, NULL); }
    | TIME expr SEMI
      { $$ = new_node(NODE_TIME, NULL, 0, $2, NULL, NULL); }
    | OPEN FILE_TOK expr SEMI
      { $$ = new_node(NODE_FILE_OPEN, NULL, 0, $3, NULL, NULL); }
    | WRITE FILE_TOK expr SEMI
      { $$ = new_node(NODE_FILE_WRITE, NULL, 0, $3, NULL, NULL); }
    | READ FILE_TOK SEMI
      { $$ = new_node(NODE_FILE_READ, NULL, 0, NULL, NULL, NULL); }
    | CLOSE FILE_TOK SEMI
      { $$ = new_node(NODE_FILE_CLOSE, NULL, 0, NULL, NULL, NULL); }
    | DELETE FILE_TOK expr SEMI
      { $$ = new_node(NODE_FILE_DELETE, NULL, 0, $3, NULL, NULL); }
    ;

block
    : LBRACE stmt_list RBRACE
      {
          $$ = new_node(NODE_BLOCK, NULL, 0, $2, NULL, NULL);
      }
    ;

else_opt
    : ELSE block
      {
          $$ = $2;
      }
    | /* empty */
      {
          $$ = NULL;
      }
    ;

var_ref
    : IDENTIFIER
      {
          $$ = new_node(NODE_VAR, $1, 0, NULL, NULL, NULL);
      }
    | ID IDENTIFIER
      {
          $$ = new_node(NODE_VAR, $2, 0, NULL, NULL, NULL);
      }
    ;

expr
    : NUMBER
      { $$ = new_node(NODE_INT, NULL, $1, NULL, NULL, NULL); }
    | STRING_LIT
      { $$ = new_node(NODE_STRING, $1, 0, NULL, NULL, NULL); }
    | TRUE_VAL
      { $$ = new_node(NODE_BOOL, NULL, 1, NULL, NULL, NULL); }
    | FALSE_VAL
      { $$ = new_node(NODE_BOOL, NULL, 0, NULL, NULL, NULL); }
    | var_ref
      { $$ = $1; }
    | LPAREN expr RPAREN
      { $$ = $2; }
    | NOT expr
      { $$ = new_node(NODE_UNOP, "NOT", 0, $2, NULL, NULL); }
    | MINUS expr %prec UMINUS
      { $$ = new_node(NODE_UNOP, "NEG", 0, $2, NULL, NULL); }
    | expr PLUS expr
      { $$ = new_node(NODE_BINOP, "PLUS", 0, $1, $3, NULL); }
    | expr MINUS expr
      { $$ = new_node(NODE_BINOP, "MINUS", 0, $1, $3, NULL); }
    | expr MULTIPLY expr
      { $$ = new_node(NODE_BINOP, "MUL", 0, $1, $3, NULL); }
    | expr DIVIDE expr
      { $$ = new_node(NODE_BINOP, "DIV", 0, $1, $3, NULL); }
    | expr GT expr
      { $$ = new_node(NODE_BINOP, "GT", 0, $1, $3, NULL); }
    | expr LT expr
      { $$ = new_node(NODE_BINOP, "LT", 0, $1, $3, NULL); }
    | expr EQ expr
      { $$ = new_node(NODE_BINOP, "EQ", 0, $1, $3, NULL); }
    | expr AND expr
      { $$ = new_node(NODE_BINOP, "AND", 0, $1, $3, NULL); }
    | expr OR expr
      { $$ = new_node(NODE_BINOP, "OR", 0, $1, $3, NULL); }
    ;

%%

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
