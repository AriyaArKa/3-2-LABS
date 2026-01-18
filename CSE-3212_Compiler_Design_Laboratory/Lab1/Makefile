# Makefile for Flex Examples - CSE 3212 Lab

# Compiler settings
FLEX = flex
GCC = gcc
RM = rm -f

# Source files
SOURCES = 01_hello.l 02_numbers_words.l 03_signed_integers.l 04_definitions.l 05_c_lexer.l

# Generated files
GENERATED = lex.yy.c lex.yy.h

# Executables
EXECS = 01_hello 02_numbers_words 03_signed_integers 04_definitions 05_c_lexer

# Default target
.PHONY: all clean distclean help

all: $(EXECS)
	@echo "✓ All examples compiled successfully!"

# Generic rule for building executables
%: %.l
	@echo "Compiling $<..."
	@$(FLEX) $<
	@$(GCC) lex.yy.c -o $@
	@echo "✓ $@ built"

# Run targets
run_%: %
	@echo "Running $<..."
	@./$<

# Clean generated C files
clean:
	@echo "Cleaning generated C files..."
	@$(RM) $(GENERATED)
	@echo "✓ Cleaned"

# Remove all build artifacts
distclean: clean
	@echo "Removing all executables..."
	@$(RM) $(EXECS)
	@$(RM) *.exe
	@echo "✓ Distcleaned"

# Help target
help:
	@echo "CSE 3212 Flex Lab - Makefile Help"
	@echo ""
	@echo "Usage:"
	@echo "  make all              - Compile all examples"
	@echo "  make 01_hello         - Compile example 1"
	@echo "  make run_01_hello     - Run example 1"
	@echo "  make clean            - Remove generated C files"
	@echo "  make distclean        - Remove all build artifacts"
	@echo "  make help             - Show this help"
	@echo ""
	@echo "Examples:"
	@echo "  make 01_hello && make run_01_hello"
	@echo "  make 02_numbers_words && make run_02_numbers_words"
	@echo "  make 05_c_lexer && make run_05_c_lexer"
