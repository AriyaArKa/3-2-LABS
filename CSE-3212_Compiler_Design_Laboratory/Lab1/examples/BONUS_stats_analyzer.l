/* 
   BONUS EXAMPLE: Advanced Token Counting and Statistics
   This program demonstrates how to collect and display lexical analysis statistics
*/

%{
#include <stdio.h>
#include <string.h>

int line_count = 0;
int word_count = 0;
int digit_count = 0;
int operator_count = 0;
int comment_count = 0;
int total_chars = 0;
%}

%option yylineno

DIGIT       [0-9]
LETTER      [a-zA-Z_]
ID          {LETTER}({LETTER}|{DIGIT})*
DECIMAL     {DIGIT}+\.{DIGIT}+
INTEGER     {DIGIT}+
WHITESPACE  [ \t]

%%

/* Count keywords separately */
if|else|while|for|do|switch|case|default|int|float|char|void|return { 
    word_count++;
    total_chars += yyleng;
    printf("[%d] KEYWORD: %-10s\n", yylineno, yytext);
}

/* Count identifiers */
{ID}        { 
    word_count++;
    total_chars += yyleng;
    printf("[%d] ID: %-10s\n", yylineno, yytext);
}

/* Count numbers */
{DECIMAL}   { 
    digit_count++;
    total_chars += yyleng;
    printf("[%d] REAL: %s\n", yylineno, yytext);
}

{INTEGER}   { 
    digit_count++;
    total_chars += yyleng;
    printf("[%d] INTEGER: %s\n", yylineno, yytext);
}

/* Count operators */
"+="        { operator_count++; total_chars += yyleng; printf("[%d] OP: %s\n", yylineno, yytext); }
"-="        { operator_count++; total_chars += yyleng; printf("[%d] OP: %s\n", yylineno, yytext); }
"=="        { operator_count++; total_chars += yyleng; printf("[%d] OP: %s\n", yylineno, yytext); }
"!="        { operator_count++; total_chars += yyleng; printf("[%d] OP: %s\n", yylineno, yytext); }
"<="        { operator_count++; total_chars += yyleng; printf("[%d] OP: %s\n", yylineno, yytext); }
">="        { operator_count++; total_chars += yyleng; printf("[%d] OP: %s\n", yylineno, yytext); }
"&&"        { operator_count++; total_chars += yyleng; printf("[%d] OP: %s\n", yylineno, yytext); }
"||"        { operator_count++; total_chars += yyleng; printf("[%d] OP: %s\n", yylineno, yytext); }
[+\-*/%=<>!&|] { operator_count++; total_chars += yyleng; printf("[%d] OP: %s\n", yylineno, yytext); }

/* Count comments */
\/\/.*      { 
    comment_count++; 
    total_chars += yyleng;
    printf("[%d] COMMENT: %s\n", yylineno, yytext);
}

/* Count lines and skip whitespace */
{WHITESPACE}+ { total_chars += yyleng; }
\n          { 
    line_count++;
    total_chars += yyleng;
}

/* Punctuation */
[{}();,.]   { total_chars += yyleng; printf("[%d] PUNCT: %s\n", yylineno, yytext); }

/* Strings */
\"[^"]*\"   { total_chars += yyleng; printf("[%d] STRING: %s\n", yylineno, yytext); }

/* Unknown */
.           { total_chars += yyleng; }

%%

int yywrap() {
    return 1;
}

void print_statistics() {
    printf("\n");
    printf("==============================\n");
    printf("LEXICAL ANALYSIS STATISTICS\n");
    printf("==============================\n");
    printf("Lines processed:    %d\n", line_count);
    printf("Words/Identifiers:  %d\n", word_count);
    printf("Numbers recognized: %d\n", digit_count);
    printf("Operators found:    %d\n", operator_count);
    printf("Comments removed:   %d\n", comment_count);
    printf("Total characters:   %d\n", total_chars);
    printf("==============================\n");
}

int main() {
    printf("=== Advanced Lexical Analyzer with Statistics ===\n\n");
    printf("Enter C/C++ code (Ctrl+Z then Enter to quit on Windows):\n\n");
    yylex();
    print_statistics();
    return 0;
}
