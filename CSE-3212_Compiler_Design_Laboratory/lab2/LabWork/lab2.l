%{
    #include <stdio.h>
    int lines=0,chars=0;
%}

%%
\n  {++lines; ++chars;}
.   {++chars;}
%%

int yywrap()
{
    return 1;
}

int main()
{
    yyin=freopen("m.c","r",stdin);
    yyout=freopen("out.txt","w",stdout);
    yylex();
    printf("Lines: %d, Characters: %d\n",lines,chars);
    return 0;
}
