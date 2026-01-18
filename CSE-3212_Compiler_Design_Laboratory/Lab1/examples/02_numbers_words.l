/* 
   EXAMPLE 2: Recognizing Numbers and Words
   This program recognizes integers, decimals, and words
*/

%%
[0-9]+              { printf("Integer: %s\n", yytext); }
[0-9]+\.[0-9]+      { printf("Decimal: %s\n", yytext); }
[a-zA-Z]+           { printf("Word: %s\n", yytext); }
[ \t]               { /* skip spaces and tabs */ }
\n                  { /* skip newlines */ }
.                   { printf("Unknown: %s\n", yytext); }
%%

int yywrap() {
    return 1;
}

int main() {
    printf("=== Number and Word Recognition ===\n");
    printf("Enter text:\n");
    yylex();
    printf("\nAnalysis complete\n");
    return 0;
}
