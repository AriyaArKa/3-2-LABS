.PHONY: all clean run

all: lex.yy.c test run

lex.yy.c: lab2.l
	flex lab2.l

test: lex.yy.c
	gcc lex.yy.c -o test

run: test
	.\test
	@echo.
	@echo ===== OUTPUT =====
	@type out.txt

clean:
	del lex.yy.c test out.txt 2>nul
