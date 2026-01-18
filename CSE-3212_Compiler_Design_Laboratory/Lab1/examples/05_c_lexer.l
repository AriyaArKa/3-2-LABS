/* 
   EXAMPLE 5: Complete C-like Lexical Analyzer
   This is a practical example for tokenizing C-like code
*/

%{
#include <stdio.h>
int line_number = 1;
%}

DIGIT       [0-9]
LETTER      [a-zA-Z_]
ID          {LETTER}({LETTER}|{DIGIT})*
DECIMAL     {DIGIT}+\.{DIGIT}+
INTEGER     {DIGIT}+
WHITESPACE  [ \t]

%%

/* Comments */
\/\/.*      { printf("Line %d: COMMENT: %s\n", line_number, yytext); }

/* Keywords */
if|else|while|for|do|switch|case|default|int|float|char|void|return|struct|union { 
    printf("Line %d: KEYWORD: %s\n", line_number, yytext); 
}

/* Identifiers */
{ID}        { printf("Line %d: IDENTIFIER: %s\n", line_number, yytext); }

/* Numbers */
{DECIMAL}   { printf("Line %d: REAL: %s\n", line_number, yytext); }
{INTEGER}   { printf("Line %d: INTEGER: %s\n", line_number, yytext); }

/* Strings */
\"[^"]*\"   { printf("Line %d: STRING: %s\n", line_number, yytext); }

/* Operators and Punctuation */
"+="        { printf("Line %d: OPERATOR: %s\n", line_number, yytext); }
"-="        { printf("Line %d: OPERATOR: %s\n", line_number, yytext); }
"=="        { printf("Line %d: OPERATOR: %s\n", line_number, yytext); }
"!="        { printf("Line %d: OPERATOR: %s\n", line_number, yytext); }
"<="        { printf("Line %d: OPERATOR: %s\n", line_number, yytext); }
">="        { printf("Line %d: OPERATOR: %s\n", line_number, yytext); }
"&&"        { printf("Line %d: OPERATOR: %s\n", line_number, yytext); }
"||"        { printf("Line %d: OPERATOR: %s\n", line_number, yytext); }
[+\-*/%=<>!&|] { printf("Line %d: OPERATOR: %s\n", line_number, yytext); }

/* Punctuation */
[{}();,.]   { printf("Line %d: PUNCTUATION: %s\n", line_number, yytext); }

/* Whitespace */
{WHITESPACE}+ { /* skip */ }

/* Newline */
\n          { line_number++; }

/* Unknown */
.           { printf("Line %d: UNKNOWN: %s\n", line_number, yytext); }

%%

int yywrap() {
    return 1;
}

int main() {
    printf("=== C-like Lexical Analyzer ===\n");
    printf("Enter C code (Ctrl+Z then Enter to quit):\n\n");
    yylex();
    printf("\nAnalysis complete\n");
    return 0;
}
