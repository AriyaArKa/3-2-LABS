/* 
   EXAMPLE 3: Positive and Negative Integers
   This program recognizes positive and negative integers
*/

%%
[+]?[0-9]+          { printf("Positive integer: %s\n", yytext); }
[-][0-9]+           { printf("Negative integer: %s\n", yytext); }
[ \t]               { /* skip whitespace */ }
\n                  { /* skip newlines */ }
.                   { printf("Invalid character: %s\n", yytext); }
%%

int yywrap() {
    return 1;
}

int main() {
    printf("=== Positive and Negative Integer Recognition ===\n");
    printf("Enter integers (positive, negative, or with +):\n");
    printf("Examples: 123, -456, +789\n");
    printf("(Ctrl+Z then Enter to quit on Windows)\n\n");
    yylex();
    printf("\nRecognition complete\n");
    return 0;
}
