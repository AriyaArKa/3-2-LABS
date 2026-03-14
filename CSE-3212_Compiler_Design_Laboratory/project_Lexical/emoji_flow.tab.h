/* A Bison parser, made by GNU Bison 3.8.2.  */

/* Bison interface for Yacc-like parsers in C

   Copyright (C) 1984, 1989-1990, 2000-2015, 2018-2021 Free Software Foundation,
   Inc.

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <https://www.gnu.org/licenses/>.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.

   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */

/* DO NOT RELY ON FEATURES THAT ARE NOT DOCUMENTED in the manual,
   especially those whose name start with YY_ or yy_.  They are
   private implementation details that can be changed or removed.  */

#ifndef YY_YY_EMOJI_FLOW_TAB_H_INCLUDED
# define YY_YY_EMOJI_FLOW_TAB_H_INCLUDED
/* Debug traces.  */
#ifndef YYDEBUG
# define YYDEBUG 0
#endif
#if YYDEBUG
extern int yydebug;
#endif
/* "%code requires" blocks.  */
#line 743 "emoji_flow.y"

    typedef struct ASTNode ASTNode;

#line 53 "emoji_flow.tab.h"

/* Token kinds.  */
#ifndef YYTOKENTYPE
# define YYTOKENTYPE
  enum yytokentype
  {
    YYEMPTY = -2,
    YYEOF = 0,                     /* "end of file"  */
    YYerror = 256,                 /* error  */
    YYUNDEF = 257,                 /* "invalid token"  */
    START = 258,                   /* START  */
    END = 259,                     /* END  */
    BLOCK = 260,                   /* BLOCK  */
    SEMI = 261,                    /* SEMI  */
    TASK = 262,                    /* TASK  */
    RUN = 263,                     /* RUN  */
    WAIT = 264,                    /* WAIT  */
    LOOP = 265,                    /* LOOP  */
    STOP = 266,                    /* STOP  */
    RETRY = 267,                   /* RETRY  */
    IF = 268,                      /* IF  */
    ELSE = 269,                    /* ELSE  */
    AND = 270,                     /* AND  */
    OR = 271,                      /* OR  */
    NOT = 272,                     /* NOT  */
    GT = 273,                      /* GT  */
    LT = 274,                      /* LT  */
    EQ = 275,                      /* EQ  */
    INPUT = 276,                   /* INPUT  */
    OUTPUT = 277,                  /* OUTPUT  */
    LOG = 278,                     /* LOG  */
    ALERT = 279,                   /* ALERT  */
    OPEN = 280,                    /* OPEN  */
    CLOSE = 281,                   /* CLOSE  */
    FILE_TOK = 282,                /* FILE_TOK  */
    DELETE = 283,                  /* DELETE  */
    WRITE = 284,                   /* WRITE  */
    READ = 285,                    /* READ  */
    TIME = 286,                    /* TIME  */
    SCHEDULE = 287,                /* SCHEDULE  */
    NEXT = 288,                    /* NEXT  */
    PREV = 289,                    /* PREV  */
    ID = 290,                      /* ID  */
    INT_TYPE = 291,                /* INT_TYPE  */
    STRING_TYPE = 292,             /* STRING_TYPE  */
    TRUE_VAL = 293,                /* TRUE_VAL  */
    FALSE_VAL = 294,               /* FALSE_VAL  */
    ASSIGN = 295,                  /* ASSIGN  */
    ERROR_TOK = 296,               /* ERROR_TOK  */
    LPAREN = 297,                  /* LPAREN  */
    RPAREN = 298,                  /* RPAREN  */
    LBRACE = 299,                  /* LBRACE  */
    RBRACE = 300,                  /* RBRACE  */
    COMMA = 301,                   /* COMMA  */
    PLUS = 302,                    /* PLUS  */
    MINUS = 303,                   /* MINUS  */
    MULTIPLY = 304,                /* MULTIPLY  */
    DIVIDE = 305,                  /* DIVIDE  */
    COLON = 306,                   /* COLON  */
    NEWLINE = 307,                 /* NEWLINE  */
    NUMBER = 308,                  /* NUMBER  */
    STRING_LIT = 309,              /* STRING_LIT  */
    IDENTIFIER = 310,              /* IDENTIFIER  */
    UMINUS = 311                   /* UMINUS  */
  };
  typedef enum yytokentype yytoken_kind_t;
#endif

/* Value type.  */
#if ! defined YYSTYPE && ! defined YYSTYPE_IS_DECLARED
union YYSTYPE
{
#line 747 "emoji_flow.y"

    int ival;
    char *sval;
    ASTNode *node;

#line 132 "emoji_flow.tab.h"

};
typedef union YYSTYPE YYSTYPE;
# define YYSTYPE_IS_TRIVIAL 1
# define YYSTYPE_IS_DECLARED 1
#endif


extern YYSTYPE yylval;


int yyparse (void);


#endif /* !YY_YY_EMOJI_FLOW_TAB_H_INCLUDED  */
