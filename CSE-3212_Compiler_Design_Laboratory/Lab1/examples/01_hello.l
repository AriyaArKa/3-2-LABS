/* 
   EXAMPLE 1: Hello World with Flex
   This is the simplest Flex program.
   It matches any character and prints a message.
*/

%%
. { printf("Character found\n"); }
\n { printf("Newline\n"); }
%%

int yywrap() {
    return 1;  /* Tell flex we're done at EOF */
}

int main() {
    printf("=== Simple Flex Program ===\n");
    printf("Type text (Ctrl+Z then Enter to quit on Windows):\n");
    yylex();  /* Start the lexer */
    printf("Program ended\n");
    return 0;
}
