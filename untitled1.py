# -*- coding: utf-8 -*-
"""
Created on Tue Jan 20 15:02:06 2026

@author: HP
"""

import math

# --------- Game Configuration ----------
MAX_PLAYER = 'X'   # AI
MIN_PLAYER = 'O'   # Human
EMPTY = '.'
GRID_SIZE = 3


# --------- Utility Functions ----------

def print_board(board):
    for row in board:
        print(" ".join(row))
    print()


def is_terminal(board):
    return check_winner(board) is not None or not any(EMPTY in row for row in board)


def utility(board):
    winner = check_winner(board)
    if winner == MAX_PLAYER:
        return 1
    elif winner == MIN_PLAYER:
        return -1
    else:
        return 0


def check_winner(board):
    # Rows and columns
    for i in range(GRID_SIZE):
        if all(board[i][j] == board[i][0] != EMPTY for j in range(GRID_SIZE)):
            return board[i][0]
        if all(board[j][i] == board[0][i] != EMPTY for j in range(GRID_SIZE)):
            return board[0][i]

    # Diagonals
    if all(board[i][i] == board[0][0] != EMPTY for i in range(GRID_SIZE)):
        return board[0][0]
    if all(board[i][GRID_SIZE - 1 - i] == board[0][GRID_SIZE - 1] != EMPTY for i in range(GRID_SIZE)):
        return board[0][GRID_SIZE - 1]

    return None


def actions(board):
    moves = []
    for i in range(GRID_SIZE):
        for j in range(GRID_SIZE):
            if board[i][j] == EMPTY:
                moves.append((i, j))
    return moves


def result(board, action, player):
    i, j = action
    new_board = [row[:] for row in board]
    new_board[i][j] = player
    return new_board


# --------- Minimax Functions ----------

def max_value(board):
    if is_terminal(board):
        return utility(board)

    v = -math.inf
    for a in actions(board):
        v = max(v, min_value(result(board, a, MAX_PLAYER)))
    return v


def min_value(board):
    if is_terminal(board):
        return utility(board)

    v = math.inf
    for a in actions(board):
        v = min(v, max_value(result(board, a, MIN_PLAYER)))
    return v


def minimax_decision(board):
    best_score = -math.inf
    best_move = None

    for a in actions(board):
        score = min_value(result(board, a, MAX_PLAYER))
        if score > best_score:
            best_score = score
            best_move = a

    return best_move


# --------- Grid Input from User ----------

def input_grid():
    print("Enter the board row by row (use X, O, .):")
    board = []
    for _ in range(GRID_SIZE):
        row = input().strip().split()
        board.append(row)
    return board


# --------- Main Game Loop ----------

def main():
    board = input_grid()
    print("\nInitial Board:")
    print_board(board)

    while not is_terminal(board):
        # AI Move
        move = minimax_decision(board)
        if move:
            board = result(board, move, MAX_PLAYER)
            print("AI plays:", move)
            print_board(board)

        if is_terminal(board):
            break

        # Human Move
        i, j = map(int, input("Enter your move (row col): ").split())
        if board[i][j] == EMPTY:
            board[i][j] = MIN_PLAYER
        else:
            print("Invalid move!")
        print_board(board)

    # Game Result
    winner = check_winner(board)
    if winner:
        print("Winner:", winner)
    else:
        print("Draw!")


if __name__ == "__main__":
    main()