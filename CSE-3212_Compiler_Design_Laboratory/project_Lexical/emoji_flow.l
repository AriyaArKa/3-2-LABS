%{

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "emoji_flow.tab.h"

int line_num = 1;
int col_num = 1;
FILE *output_file;


void output_token(int token, const char* token_name, const char* lexeme) {
    fprintf(output_file, "<%s, %s>\n", token_name, lexeme);
    printf("Line %d, Col %d: %-15s -> %s\n", line_num, col_num, lexeme, token_name);
}

%}

%option noyywrap
%option yylineno

/* UTF-8 multibyte character patterns */
UTF8_CONT   [\x80-\xBF]
UTF8_2BYTE  [\xC2-\xDF]{UTF8_CONT}
UTF8_3BYTE  [\xE0-\xEF]{UTF8_CONT}{2}
UTF8_4BYTE  [\xF0-\xF4]{UTF8_CONT}{3}

%%

    /* ============ Program Structure ============ */

    /* START: ▶️ (U+25B6 U+FE0F)*/
"\xE2\x96\xB6\xEF\xB8\x8F"      { output_token(START, "START", yytext); col_num += 2; return START; }
"\xE2\x96\xB6"                  { output_token(RUN, "RUN", yytext); col_num += 1; return RUN; }

    /* END: ⏹️ (U+23F9 U+FE0F) */
"\xE2\x8F\xB9\xEF\xB8\x8F"      { output_token(END, "END", yytext); col_num += 2; return END; }
"\xE2\x8F\xB9"                  { output_token(END, "END", yytext); col_num += 1; return END; }

    /* BLOCK: 🧱 (U+1F9F1) */
"\xF0\x9F\xA7\xB1"              { output_token(BLOCK, "BLOCK", yytext); col_num += 1; return BLOCK; }

    /* ============ Tasks & Actions ============ */

    /* TASK: ⚙️ (U+2699 U+FE0F) */
"\xE2\x9A\x99\xEF\xB8\x8F"      { output_token(TASK, "TASK", yytext); col_num += 2; return TASK; }
"\xE2\x9A\x99"                  { output_token(TASK, "TASK", yytext); col_num += 1; return TASK; }

    /* WAIT: ⏳ (U+23F3) */
"\xE2\x8F\xB3"                  { output_token(WAIT, "WAIT", yytext); col_num += 1; return WAIT; }

    /* LOOP: 🔁 (U+1F501) */
"\xF0\x9F\x94\x81"              { output_token(LOOP, "LOOP", yytext); col_num += 1; return LOOP; }

    /* STOP: 🔚 (U+1F51A) */
"\xF0\x9F\x94\x9A"              { output_token(STOP, "STOP", yytext); col_num += 1; return STOP; }

    /* RETRY: ♻️ (U+267B U+FE0F) */
"\xE2\x99\xBB\xEF\xB8\x8F"      { output_token(RETRY, "RETRY", yytext); col_num += 2; return RETRY; }
"\xE2\x99\xBB"                  { output_token(RETRY, "RETRY", yytext); col_num += 1; return RETRY; }

    /* ============ Conditions & Logic ============ */

    /* IF: 🤔 (U+1F914) */
"\xF0\x9F\xA4\x94"              { output_token(IF, "IF", yytext); col_num += 1; return IF; }

    /* ELSE: 🔁❓ (U+1F501 U+2753) - LOOP followed by question mark */
"\xF0\x9F\x94\x81\xE2\x9D\x93"  { output_token(ELSE, "ELSE", yytext); col_num += 2; return ELSE; }

    /* AND: 🤝 (U+1F91D) */
"\xF0\x9F\xA4\x9D"              { output_token(AND, "AND", yytext); col_num += 1; return AND; }

    /* OR: 🔀 (U+1F500) */
"\xF0\x9F\x94\x80"              { output_token(OR, "OR", yytext); col_num += 1; return OR; }

    /* NOT: 🚫 (U+1F6AB) */
"\xF0\x9F\x9A\xAB"              { output_token(NOT, "NOT", yytext); col_num += 1; return NOT; }

    /* GT: 🔼 (U+1F53C) */
"\xF0\x9F\x94\xBC"              { output_token(GT, "GT", yytext); col_num += 1; return GT; }

    /* LT: 🔽 (U+1F53D) */
"\xF0\x9F\x94\xBD"              { output_token(LT, "LT", yytext); col_num += 1; return LT; }

    /* EQ/ASSIGN: 🟰 (U+1F7F0) */
"\xF0\x9F\x9F\xB0"              { output_token(EQ, "EQ", yytext); col_num += 1; return EQ; }

    /* ============ Input / Output ============ */

    /* INPUT: 📥 (U+1F4E5) */
"\xF0\x9F\x93\xA5"              { output_token(INPUT, "INPUT", yytext); col_num += 1; return INPUT; }

    /* OUTPUT: 📤 (U+1F4E4) */
"\xF0\x9F\x93\xA4"              { output_token(OUTPUT, "OUTPUT", yytext); col_num += 1; return OUTPUT; }

    /* LOG: 📝 (U+1F4DD) */
"\xF0\x9F\x93\x9D"              { output_token(LOG, "LOG", yytext); col_num += 1; return LOG; }

    /* ALERT: 🔔 (U+1F514) */
"\xF0\x9F\x94\x94"              { output_token(ALERT, "ALERT", yytext); col_num += 1; return ALERT; }

    /* ============ File & System ============ */

    /* OPEN: 📂 (U+1F4C2) */
"\xF0\x9F\x93\x82"              { output_token(OPEN, "OPEN", yytext); col_num += 1; return OPEN; }

    /* CLOSE: 📁 (U+1F4C1) */
"\xF0\x9F\x93\x81"              { output_token(CLOSE, "CLOSE", yytext); col_num += 1; return CLOSE; }

    /* FILE: 🧾 (U+1F9FE) */
"\xF0\x9F\xA7\xBE"              { output_token(FILE_TOK, "FILE", yytext); col_num += 1; return FILE_TOK; }

    /* DELETE: ❌ (U+274C) */
"\xE2\x9D\x8C"                  { output_token(DELETE, "DELETE", yytext); col_num += 1; return DELETE; }

    /* WRITE: ✏️ (U+270F U+FE0F) */
"\xE2\x9C\x8F\xEF\xB8\x8F"      { output_token(WRITE, "WRITE", yytext); col_num += 2; return WRITE; }
"\xE2\x9C\x8F"                  { output_token(WRITE, "WRITE", yytext); col_num += 1; return WRITE; }

    /* READ: 📄 (U+1F4C4) */
"\xF0\x9F\x93\x84"              { output_token(READ, "READ", yytext); col_num += 1; return READ; }

    /* ============ Time & Control ============ */

    /* TIME: ⏰ (U+23F0) */
"\xE2\x8F\xB0"                  { output_token(TIME, "TIME", yytext); col_num += 1; return TIME; }

    /* SCHEDULE: 🕒 (U+1F552) */
"\xF0\x9F\x95\x92"              { output_token(SCHEDULE, "SCHEDULE", yytext); col_num += 1; return SCHEDULE; }

    /* NEXT: ⏭️ (U+23ED U+FE0F) */
"\xE2\x8F\xAD\xEF\xB8\x8F"      { output_token(NEXT, "NEXT", yytext); col_num += 2; return NEXT; }
"\xE2\x8F\xAD"                  { output_token(NEXT, "NEXT", yytext); col_num += 1; return NEXT; }

    /* PREV: ⏮️ (U+23EE U+FE0F) */
"\xE2\x8F\xAE\xEF\xB8\x8F"      { output_token(PREV, "PREV", yytext); col_num += 2; return PREV; }
"\xE2\x8F\xAE"                  { output_token(PREV, "PREV", yytext); col_num += 1; return PREV; }

    /* ============ Identifiers & Variables ============ */

    /* ID marker: 📛 (U+1F4DB) */
"\xF0\x9F\x93\x9B"              { output_token(ID, "ID", yytext); col_num += 1; return ID; }

    /* INT type: 🔢 (U+1F522) */
"\xF0\x9F\x94\xA2"              { output_token(INT_TYPE, "INT_TYPE", yytext); col_num += 1; return INT_TYPE; }

    /* STRING type: 🔤 (U+1F524) */
"\xF0\x9F\x94\xA4"              { output_token(STRING_TYPE, "STRING_TYPE", yytext); col_num += 1; return STRING_TYPE; }

    /* TRUE: 🔥 (U+1F525) */
"\xF0\x9F\x94\xA5"              { output_token(TRUE_VAL, "TRUE", yytext); col_num += 1; return TRUE_VAL; }

    /* FALSE: ❄️ (U+2744 U+FE0F) */
"\xE2\x9D\x84\xEF\xB8\x8F"      { output_token(FALSE_VAL, "FALSE", yytext); col_num += 2; return FALSE_VAL; }
"\xE2\x9D\x84"                  { output_token(FALSE_VAL, "FALSE", yytext); col_num += 1; return FALSE_VAL; }

    /* ERROR: ⚠️ (U+26A0 U+FE0F) */
"\xE2\x9A\xA0\xEF\xB8\x8F"      { output_token(ERROR_TOK, "ERROR", yytext); col_num += 2; return ERROR_TOK; }
"\xE2\x9A\xA0"                  { output_token(ERROR_TOK, "ERROR", yytext); col_num += 1; return ERROR_TOK; }

    /* ============ Standard Tokens ============ */

    /* Semicolon */
";"                             { output_token(SEMI, "SEMI", yytext); col_num += 1; return SEMI; }

    /* Parentheses and Braces */
"("                             { output_token(LPAREN, "LPAREN", yytext); col_num += 1; return LPAREN; }
")"                             { output_token(RPAREN, "RPAREN", yytext); col_num += 1; return RPAREN; }
"{"                             { output_token(LBRACE, "LBRACE", yytext); col_num += 1; return LBRACE; }
"}"                             { output_token(RBRACE, "RBRACE", yytext); col_num += 1; return RBRACE; }

    /* Operators */
","                             { output_token(COMMA, "COMMA", yytext); col_num += 1; return COMMA; }
"+"                             { output_token(PLUS, "PLUS", yytext); col_num += 1; return PLUS; }
"-"                             { output_token(MINUS, "MINUS", yytext); col_num += 1; return MINUS; }
"*"                             { output_token(MULTIPLY, "MUL", yytext); col_num += 1; return MULTIPLY; }
"/"                             { output_token(DIVIDE, "DIV", yytext); col_num += 1; return DIVIDE; }
":"                             { output_token(COLON, "COLON", yytext); col_num += 1; return COLON; }
"="                             { output_token(ASSIGN, "ASSIGN", yytext); col_num += 1; return ASSIGN; }

    /* Numbers */
[0-9]+                          {
                                    yylval.ival = atoi(yytext);
                                    output_token(NUMBER, "NUMBER", yytext);
                                    col_num += yyleng;
                                    return NUMBER;
                                }


    /* String literals */
\"[^\"]*\"                      {
                                    int len = yyleng;
                                    char *stripped = (char *)malloc((size_t)len - 1);
                                    if (!stripped) {
                                        fprintf(stderr, "Out of memory while tokenizing string literal\n");
                                        exit(1);
                                    }
                                    memcpy(stripped, yytext + 1, (size_t)len - 2);
                                    stripped[len - 2] = '\0';
                                    yylval.sval = stripped;
                                    output_token(STRING_LIT, "STRING", yytext);
                                    col_num += yyleng;
                                    return STRING_LIT;
                                }

    /* Identifiers (alphanumeric after emoji markers) */
[a-zA-Z_][a-zA-Z0-9_]*          {
                                    size_t len = strlen(yytext);
                                    yylval.sval = (char *)malloc(len + 1);
                                    if (!yylval.sval) {
                                        fprintf(stderr, "Out of memory while tokenizing identifier\n");
                                        exit(1);
                                    }
                                    memcpy(yylval.sval, yytext, len + 1);
                                    output_token(IDENTIFIER, "IDENTIFIER", yytext);
                                    col_num += yyleng;
                                    return IDENTIFIER;
                                }

    /* Whitespace handling */
[ \t]+                          { col_num += yyleng; /* Skip whitespace */ }
\n                              { line_num++; col_num = 1; /* Track newlines */ }
\r                              { /* Ignore carriage return */ }

    /* Comments: 💬 text until newline */
"\xF0\x9F\x92\xAC"[^\n]*        { /* Skip comments */ }

    /* Unknown characters - report as error */
.                               { 
                                    fprintf(stderr, "Warning: Unknown character '%s' at line %d, col %d\n", 
                                            yytext, line_num, col_num);
                                    col_num += 1;
                                }

%%

/* Main is provided by emoji_flow.y so Bison controls the full pipeline. */
