%{
    #include <stdio.h>
    int keywords = 0, loops = 0, identifiers = 0, operators = 0, braces = 0, comments = 0;
%}

%%
"for"          { ++keywords; printf("KEYWORD: for\n"); }
"do"           { ++keywords; printf("KEYWORD: do\n"); }
"while"        { ++keywords; printf("KEYWORD: while\n"); }
"//".*         { ++comments; printf("COMMENT: %s\n", yytext); }
"--ignore"     { ++comments; printf("IGNORE: %s\n", yytext); }
"done"         { ++keywords; printf("KEYWORD: done\n"); }

[a-zA-Z_][a-zA-Z0-9_]*  { ++identifiers; printf("ID: %s\n", yytext); }

[+\-*/=<>!&|]  { ++operators; printf("OPERATOR: %s\n", yytext); }

[{}()\[\];]    { ++braces; printf("BRACE: %s\n", yytext); }

[0-9]+         { printf("NUMBER: %s\n", yytext); }

\n             { }
[ \t]          { }

.              { }

%%

int yywrap()
{
    return 1;
}

int main()
{
    yyin = freopen("test_loop.c", "r", stdin);
    yyout = freopen("out2.txt", "w", stdout);
    yylex();
    
    printf("\n===== STATISTICS =====\n");
    printf("Keywords: %d\n", keywords);
    printf("Identifiers: %d\n", identifiers);
    printf("Operators: %d\n", operators);
    printf("Braces: %d\n", braces);
    printf("Comments/Ignores: %d\n", comments);
    printf("Total Tokens: %d\n", keywords + identifiers + operators + braces + comments);
    
    return 0;
}
