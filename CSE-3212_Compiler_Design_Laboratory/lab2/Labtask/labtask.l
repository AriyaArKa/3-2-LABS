%{
    #include <stdio.h>
%}

%x COMMENT

%%
"do"             {printf("do\n"); BEGIN(COMMENT);  }
<COMMENT>"done"  {printf("done\n"); BEGIN(INITIAL); } 
<COMMENT>\n ;
<COMMENT>.  ;

[a-zA-Z0-9]*  {printf("%s",yytext);}


\n  {printf("\n");}
.   {printf("%s", yytext); ECHO;}
%%

int yywrap()
{
    return 1;
}

int main()
{
    yyin=freopen("input.c","r",stdin);
    yyout=freopen("out.txt","w",stdout);
    yylex();
    return 0;
}
