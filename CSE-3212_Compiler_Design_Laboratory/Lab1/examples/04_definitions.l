/* 
   EXAMPLE 4: Using Definitions
   Demonstrates how to use predefined patterns for cleaner code
*/

%{
#include <stdio.h>
#include <string.h>
%}

/* DEFINITIONS SECTION */
DIGIT       [0-9]
LETTER      [a-zA-Z]
ID          {LETTER}({LETTER}|{DIGIT}|_)*
DECIMAL     {DIGIT}+\.{DIGIT}+
INTEGER     {DIGIT}+

%%
/* RULES SECTION */

/* Keywords - check for these first */
if|else|while|for|int|float|char|void  { printf("KEYWORD: %s\n", yytext); }

/* Identifiers */
{ID}        { printf("IDENTIFIER: %s\n", yytext); }

/* Numbers */
{DECIMAL}   { printf("DECIMAL: %s\n", yytext); }
{INTEGER}   { printf("INTEGER: %s\n", yytext); }

/* Operators */
[+\-*/=]    { printf("OPERATOR: %s\n", yytext); }

/* Punctuation */
[{}();,]    { printf("PUNCTUATION: %s\n", yytext); }

/* Whitespace */
[ \t]       { /* skip */ }
\n          { printf("NEWLINE\n"); }

/* Unknown characters */
.           { printf("UNKNOWN: %s\n", yytext); }

%%

int yywrap() {
    return 1;
}

int main() {
    printf("=== Lexical Analyzer with Definitions ===\n");
    printf("Sample C code:\n");
    printf("int x = 10;\n");
    printf("if(x > 5) y = x + 3.14;\n\n");
    yylex();
    return 0;
}
