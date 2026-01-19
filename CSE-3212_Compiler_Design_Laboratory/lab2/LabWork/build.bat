@echo off
echo Compiling with Flex...
flex lab2.l

echo Compiling with GCC...
gcc lex.yy.c -o test

echo Running test...
test

echo.
echo ===== OUTPUT =====
type out.txt

pause
