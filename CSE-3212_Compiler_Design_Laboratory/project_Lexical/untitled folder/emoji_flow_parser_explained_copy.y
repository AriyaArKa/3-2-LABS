%{
/*
 * EXPLAINED COPY OF EMOJI-FLOW PARSER
 * ------------------------------------------------------------
 * This file is a teaching version of parser.y.
 * It contains extra inline comments to explain:
 * 1) Why each block exists
 * 2) How parser + runtime execution work together
 *
 * Important: This copy is placed outside the compiler project folder
 * to keep the main project files clean.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int yylex(void);
void yyerror(const char *msg);
extern int line_num;
extern int col_num;
extern FILE *yyin;
extern FILE *output_file;

/* Enable with: ./emoji_flow_compiler <file> --trace-tokens */
int debug_tokens = 0;

/* ---------------- Runtime data model ----------------
 * Bison grammar alone validates syntax.
 * To run programs, we need runtime data structures:
 * - Value: numbers/strings/booleans
 * - Expr: arithmetic/identifier expressions
 * - Cond: boolean conditions
 * - Stmt/StmtList: executable statements and block lists
 */
typedef enum {
    VAL_NUM,
    VAL_STR,
    VAL_BOOL
} ValueType;

typedef struct {
    ValueType type;
    int num;
    char *str;
    int boolean;
} Value;

typedef enum {
    EX_NUM,
    EX_STR,
    EX_VAR,
    EX_ADD,
    EX_SUB,
    EX_MUL,
    EX_DIV
} ExprType;

typedef struct Expr {
    ExprType type;
    int num;
    char *text;
    struct Expr *left;
    struct Expr *right;
} Expr;

typedef enum {
    C_TRUE,
    C_FALSE,
    C_GT,
    C_LT,
    C_EQ,
    C_NOT,
    C_AND,
    C_OR
} CondType;

typedef struct Cond {
    CondType type;
    Expr *left_expr;
    Expr *right_expr;
    struct Cond *left_cond;
    struct Cond *right_cond;
} Cond;

typedef struct Stmt Stmt;
typedef struct StmtList StmtList;

typedef enum {
    ST_TASK,
    ST_DECL,
    ST_ASSIGN,
    ST_OUTPUT,
    ST_INPUT,
    ST_LOOP,
    ST_IF
} StmtType;

struct Stmt {
    StmtType type;
    union {
        struct {
            char *task_name;
        } task;

        struct {
            char *name;
            Expr *expr;
        } set;

        struct {
            Expr *expr;
        } output;

        struct {
            char *name;
        } input;

        struct {
            Cond *cond;
            StmtList *body;
        } loop;

        struct {
            Cond *cond;
            StmtList *then_block;
            StmtList *else_block;
        } if_stmt;
    } data;
};

struct StmtList {
    Stmt *stmt;
    StmtList *next;
};

typedef struct {
    char *name;
    Value value;
} Variable;

/* Safety limits for runtime execution. */
#define MAX_VARS 256
#define LOOP_GUARD_LIMIT 100000

static Variable g_vars[MAX_VARS];
static int g_var_count = 0;
static StmtList *g_program = NULL;

/* ---------------- Helpers ----------------
 * These small constructors keep grammar actions short and readable.
 */
static char *dup_str(const char *s)
{
    size_t n = strlen(s);
    char *out = (char *)malloc(n + 1);
    if (!out) {
        fprintf(stderr, "Out of memory\n");
        exit(EXIT_FAILURE);
    }
    memcpy(out, s, n + 1);
    return out;
}

static Value value_num(int x)
{
    Value v;
    v.type = VAL_NUM;
    v.num = x;
    v.str = NULL;
    v.boolean = (x != 0);
    return v;
}

static Value value_bool(int b)
{
    Value v;
    v.type = VAL_BOOL;
    v.num = b ? 1 : 0;
    v.str = NULL;
    v.boolean = b ? 1 : 0;
    return v;
}

static Value value_str(char *s)
{
    Value v;
    v.type = VAL_STR;
    v.num = 0;
    v.str = s;
    v.boolean = (s && s[0] != '\0');
    return v;
}

static Expr *mk_expr(ExprType t, Expr *l, Expr *r, int n, char *txt)
{
    Expr *e = (Expr *)malloc(sizeof(Expr));
    if (!e) {
        fprintf(stderr, "Out of memory\n");
        exit(EXIT_FAILURE);
    }
    e->type = t;
    e->left = l;
    e->right = r;
    e->num = n;
    e->text = txt;
    return e;
}

static Cond *mk_cond_expr(CondType t, Expr *l, Expr *r)
{
    Cond *c = (Cond *)malloc(sizeof(Cond));
    if (!c) {
        fprintf(stderr, "Out of memory\n");
        exit(EXIT_FAILURE);
    }
    c->type = t;
    c->left_expr = l;
    c->right_expr = r;
    c->left_cond = NULL;
    c->right_cond = NULL;
    return c;
}

static Cond *mk_cond_logic(CondType t, Cond *l, Cond *r)
{
    Cond *c = (Cond *)malloc(sizeof(Cond));
    if (!c) {
        fprintf(stderr, "Out of memory\n");
        exit(EXIT_FAILURE);
    }
    c->type = t;
    c->left_expr = NULL;
    c->right_expr = NULL;
    c->left_cond = l;
    c->right_cond = r;
    return c;
}

static Stmt *mk_stmt(StmtType t)
{
    Stmt *s = (Stmt *)malloc(sizeof(Stmt));
    if (!s) {
        fprintf(stderr, "Out of memory\n");
        exit(EXIT_FAILURE);
    }
    s->type = t;
    return s;
}

static StmtList *append_stmt(StmtList *list, Stmt *stmt)
{
    StmtList *node;
    StmtList *cur;

    if (!stmt) {
        return list;
    }

    node = (StmtList *)malloc(sizeof(StmtList));
    if (!node) {
        fprintf(stderr, "Out of memory\n");
        exit(EXIT_FAILURE);
    }
    node->stmt = stmt;
    node->next = NULL;

    if (!list) {
        return node;
    }

    cur = list;
    while (cur->next) {
        cur = cur->next;
    }
    cur->next = node;
    return list;
}

static int find_var(const char *name)
{
    int i;
    for (i = 0; i < g_var_count; ++i) {
        if (strcmp(g_vars[i].name, name) == 0) {
            return i;
        }
    }
    return -1;
}

static void set_var(const char *name, Value v)
{
    int idx = find_var(name);
    if (idx >= 0) {
        g_vars[idx].value = v;
        return;
    }

    if (g_var_count >= MAX_VARS) {
        fprintf(stderr, "[RUNTIME ERROR] Variable table full\n");
        exit(EXIT_FAILURE);
    }

    g_vars[g_var_count].name = dup_str(name);
    g_vars[g_var_count].value = v;
    g_var_count++;
}

static Value get_var(const char *name)
{
    int idx = find_var(name);
    if (idx < 0) {
        fprintf(stderr, "[RUNTIME ERROR] Undefined variable '%s'\n", name);
        return value_num(0);
    }
    return g_vars[idx].value;
}

static int to_num(Value v)
{
    /* Converts bool/string/number-like runtime values to integer context. */
    if (v.type == VAL_STR) {
        fprintf(stderr, "[RUNTIME ERROR] Numeric operation on string\n");
        return 0;
    }
    if (v.type == VAL_BOOL) {
        return v.boolean ? 1 : 0;
    }
    return v.num;
}

static Value eval_expr(Expr *e)
{
    /* Recursive expression evaluation for +, -, *, / and variable lookups. */
    Value a;
    Value b;
    int x;
    int y;

    if (!e) {
        return value_num(0);
    }

    switch (e->type) {
    case EX_NUM:
        return value_num(e->num);
    case EX_STR:
        return value_str(e->text);
    case EX_VAR:
        return get_var(e->text);
    case EX_ADD:
        a = eval_expr(e->left);
        b = eval_expr(e->right);
        return value_num(to_num(a) + to_num(b));
    case EX_SUB:
        a = eval_expr(e->left);
        b = eval_expr(e->right);
        return value_num(to_num(a) - to_num(b));
    case EX_MUL:
        a = eval_expr(e->left);
        b = eval_expr(e->right);
        return value_num(to_num(a) * to_num(b));
    case EX_DIV:
        a = eval_expr(e->left);
        b = eval_expr(e->right);
        x = to_num(a);
        y = to_num(b);
        if (y == 0) {
            fprintf(stderr, "[RUNTIME ERROR] Division by zero\n");
            return value_num(0);
        }
        return value_num(x / y);
    }

    return value_num(0);
}

static int eval_cond(Cond *c)
{
    /* Recursive boolean evaluation for if/loop conditions. */
    Value a;
    Value b;
    if (!c) {
        return 0;
    }

    switch (c->type) {
    case C_TRUE:
        return 1;
    case C_FALSE:
        return 0;
    case C_GT:
        a = eval_expr(c->left_expr);
        b = eval_expr(c->right_expr);
        return to_num(a) > to_num(b);
    case C_LT:
        a = eval_expr(c->left_expr);
        b = eval_expr(c->right_expr);
        return to_num(a) < to_num(b);
    case C_EQ:
        a = eval_expr(c->left_expr);
        b = eval_expr(c->right_expr);
        if (a.type == VAL_STR && b.type == VAL_STR) {
            return strcmp(a.str ? a.str : "", b.str ? b.str : "") == 0;
        }
        return to_num(a) == to_num(b);
    case C_NOT:
        return !eval_cond(c->left_cond);
    case C_AND:
        return eval_cond(c->left_cond) && eval_cond(c->right_cond);
    case C_OR:
        return eval_cond(c->left_cond) || eval_cond(c->right_cond);
    }
    return 0;
}

static void exec_stmt_list(StmtList *list);

static void exec_stmt(Stmt *s)
{
    /* Executes exactly one statement node built by parser actions. */
    Value v;
    int x;
    int guard;

    if (!s) {
        return;
    }

    switch (s->type) {
    case ST_TASK:
        /* Task is accepted as executable marker; no side effects by default. */
        break;

    case ST_DECL:
    case ST_ASSIGN:
        v = eval_expr(s->data.set.expr);
        set_var(s->data.set.name, v);
        break;

    case ST_OUTPUT:
        v = eval_expr(s->data.output.expr);
        if (v.type == VAL_STR) {
            printf("%s\n", v.str ? v.str : "");
        } else {
            printf("%d\n", to_num(v));
        }
        break;

    case ST_INPUT:
        printf("%s = ", s->data.input.name);
        fflush(stdout);
        if (scanf("%d", &x) != 1) {
            fprintf(stderr, "[RUNTIME ERROR] Failed to read integer input\n");
            x = 0;
        }
        set_var(s->data.input.name, value_num(x));
        break;

    case ST_LOOP:
        guard = 0;
        while (eval_cond(s->data.loop.cond)) {
            exec_stmt_list(s->data.loop.body);
            guard++;
            if (guard > LOOP_GUARD_LIMIT) {
                fprintf(stderr, "[RUNTIME ERROR] Loop guard triggered (possible infinite loop)\n");
                break;
            }
        }
        break;

    case ST_IF:
        if (eval_cond(s->data.if_stmt.cond)) {
            exec_stmt_list(s->data.if_stmt.then_block);
        } else {
            exec_stmt_list(s->data.if_stmt.else_block);
        }
        break;
    }
}

static void exec_stmt_list(StmtList *list)
{
    /* Executes a block: statements run top-to-bottom in source order. */
    StmtList *cur = list;
    while (cur) {
        exec_stmt(cur->stmt);
        cur = cur->next;
    }
}
%}

%code requires {
typedef struct Expr Expr;
typedef struct Cond Cond;
typedef struct Stmt Stmt;
typedef struct StmtList StmtList;
}

%union {
    /*
     * yylval payload types:
     * - lexer fills these fields
     * - parser actions read them as $1, $2, ...
     */
    int ival;
    char *sval;
    Expr *expr;
    Cond *cond;
    Stmt *stmt;
    StmtList *stmt_list;
}

/*
 * Declare all lexer tokens so:
 * 1) parser knows token names
 * 2) lexer can include generated parser header safely
 */
%token START END BLOCK SEMI TASK RUN WAIT LOOP STOP RETRY IF ELSE AND OR NOT GT LT EQ INPUT OUTPUT LOG ALERT OPEN CLOSE FILE_TOK DELETE WRITE READ TIME SCHEDULE NEXT PREV ID INT_TYPE STRING_TYPE TRUE_VAL FALSE_VAL ASSIGN ERROR_TOK COMMA COLON NEWLINE
%token LPAREN RPAREN LBRACE RBRACE PLUS MINUS MULTIPLY DIVIDE

%token <ival> NUMBER
%token <sval> STRING_LIT IDENTIFIER

%left OR
%left AND
%right NOT
%left GT LT EQ
%left PLUS MINUS
%left MULTIPLY DIVIDE

%type <stmt_list> workflow_block statement_list optional_else program
%type <stmt> statement task_stmt declaration_stmt assignment_stmt io_stmt loop_stmt if_stmt
%type <expr> expression term factor
%type <cond> condition

%start program

%%
/*
 * Grammar rules below do two jobs at once:
 * 1) validate syntax shape
 * 2) build runtime nodes (AST-like statement structures)
 */
program
    : START workflow_block END
      {
          g_program = $2;
          $$ = $2;
      }
    ;

workflow_block
    : BLOCK LBRACE statement_list RBRACE
      {
          $$ = $3;
      }
    ;

statement_list
    : /* empty */
      {
          $$ = NULL;
      }
    | statement_list statement
      {
          $$ = append_stmt($1, $2);
      }
    ;

statement
    : task_stmt SEMI
      {
          $$ = $1;
      }
    | declaration_stmt SEMI
      {
          $$ = $1;
      }
    | assignment_stmt SEMI
      {
          $$ = $1;
      }
    | io_stmt SEMI
      {
          $$ = $1;
      }
    | loop_stmt
      {
          $$ = $1;
      }
    | if_stmt
      {
          $$ = $1;
      }
        | error SEMI
      {
                    /* Friendly recovery: skip bad statement and continue parsing file. */
          yyerror("Recovered from a bad statement");
          yyerrok;
          $$ = NULL;
      }
    ;

task_stmt
    : TASK IDENTIFIER
      {
          /* Create executable task node. */
          Stmt *s = mk_stmt(ST_TASK);
          s->data.task.task_name = $2;
          $$ = s;
      }
    ;

declaration_stmt
    : ID IDENTIFIER EQ expression
      {
          /* Variable declaration: 📛 name 🟰 expression; */
          Stmt *s = mk_stmt(ST_DECL);
          s->data.set.name = $2;
          s->data.set.expr = $4;
          $$ = s;
      }
    ;

assignment_stmt
    : IDENTIFIER EQ expression
      {
          /* Variable assignment: name 🟰 expression; */
          Stmt *s = mk_stmt(ST_ASSIGN);
          s->data.set.name = $1;
          s->data.set.expr = $3;
          $$ = s;
      }
    ;

io_stmt
    : OUTPUT expression
      {
          /* Output statement: 📤 expression; */
          Stmt *s = mk_stmt(ST_OUTPUT);
          s->data.output.expr = $2;
          $$ = s;
      }
    | INPUT IDENTIFIER
      {
          /* Input statement: 📥 identifier; */
          Stmt *s = mk_stmt(ST_INPUT);
          s->data.input.name = $2;
          $$ = s;
      }
    ;

loop_stmt
    : LOOP LPAREN condition RPAREN workflow_block
      {
          /* Loop statement: 🔁 (condition) 🧱 { ... } */
          Stmt *s = mk_stmt(ST_LOOP);
          s->data.loop.cond = $3;
          s->data.loop.body = $5;
          $$ = s;
      }
    ;

if_stmt
    : IF LPAREN condition RPAREN workflow_block optional_else
      {
          /* If/else statement: 🤔 (...) block [🔁❓ block] */
          Stmt *s = mk_stmt(ST_IF);
          s->data.if_stmt.cond = $3;
          s->data.if_stmt.then_block = $5;
          s->data.if_stmt.else_block = $6;
          $$ = s;
      }
    ;

optional_else
    : /* empty */
      {
          $$ = NULL;
      }
    | ELSE workflow_block
      {
          $$ = $2;
      }
    ;

condition
    : expression GT expression
      {
          /* Comparison node: expression 🔼 expression */
          $$ = mk_cond_expr(C_GT, $1, $3);
      }
    | expression LT expression
      {
          /* Comparison node: expression 🔽 expression */
          $$ = mk_cond_expr(C_LT, $1, $3);
      }
    | expression EQ expression
      {
          /* Equality node: expression 🟰 expression */
          $$ = mk_cond_expr(C_EQ, $1, $3);
      }
    | TRUE_VAL
      {
          $$ = mk_cond_logic(C_TRUE, NULL, NULL);
      }
    | FALSE_VAL
      {
          $$ = mk_cond_logic(C_FALSE, NULL, NULL);
      }
    | NOT condition
      {
          $$ = mk_cond_logic(C_NOT, $2, NULL);
      }
    | condition AND condition
      {
          $$ = mk_cond_logic(C_AND, $1, $3);
      }
    | condition OR condition
      {
          $$ = mk_cond_logic(C_OR, $1, $3);
      }
    | LPAREN condition RPAREN
      {
          $$ = $2;
      }
    ;

expression
    : expression PLUS term
      {
          /* Build '+' expression node. */
          $$ = mk_expr(EX_ADD, $1, $3, 0, NULL);
      }
    | expression MINUS term
      {
          /* Build '-' expression node. */
          $$ = mk_expr(EX_SUB, $1, $3, 0, NULL);
      }
    | term
      {
          $$ = $1;
      }
    ;

term
    : term MULTIPLY factor
      {
          /* Build '*' expression node. */
          $$ = mk_expr(EX_MUL, $1, $3, 0, NULL);
      }
    | term DIVIDE factor
      {
          /* Build '/' expression node. */
          $$ = mk_expr(EX_DIV, $1, $3, 0, NULL);
      }
    | factor
      {
          $$ = $1;
      }
    ;

factor
    : NUMBER
      {
          $$ = mk_expr(EX_NUM, NULL, NULL, $1, NULL);
      }
    | STRING_LIT
      {
          $$ = mk_expr(EX_STR, NULL, NULL, 0, $1);
      }
    | IDENTIFIER
      {
          $$ = mk_expr(EX_VAR, NULL, NULL, 0, $1);
      }
    | LPAREN expression RPAREN
      {
          $$ = $2;
      }
    ;

%%

void yyerror(const char *msg)
{
    /* Central syntax error hook called by Bison. */
    fprintf(stderr, "[SYNTAX ERROR] %s at line %d, column %d\n", msg, line_num, col_num);
}

int main(int argc, char **argv)
{
    /* Main parse+execute flow:
     * 1) open input
     * 2) parse into runtime program structure
     * 3) execute statements if parse succeeded
     */
    int rc;
    if (argc < 2) {
        fprintf(stderr, "Usage: %s <input_file> [--trace-tokens]\n", argv[0]);
        return EXIT_FAILURE;
    }

    yyin = fopen(argv[1], "r");
    if (!yyin) {
        perror("Could not open input file");
        return EXIT_FAILURE;
    }

    if (argc > 2 && strcmp(argv[2], "--trace-tokens") == 0) {
        debug_tokens = 1;
    }

    output_file = stdout;

    rc = yyparse();
    if (rc == 0) {
        exec_stmt_list(g_program);
    }

    if (yyin && yyin != stdin) {
        fclose(yyin);
    }

    return rc == 0 ? EXIT_SUCCESS : EXIT_FAILURE;
}
