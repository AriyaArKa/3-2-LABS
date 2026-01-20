import math
import copy

MAX = 'X'
MIN = 'O'
EMPTY = '_'


# ---------- TERMINAL TEST ----------
def terminal_test(state):
    return check_winner(state) is not None or not any(EMPTY in row for row in state)


# ---------- UTILITY FUNCTION ----------
def utility(state):
    winner = check_winner(state)
    if winner == MAX:
        return 1
    elif winner == MIN:
        return -1
    else:
        return 0


# ---------- CHECK WINNER ----------
def check_winner(board):
    lines = []

    # Rows & Columns
    lines.extend(board)
    lines.extend([[board[r][c] for r in range(3)] for c in range(3)])

    # Diagonals
    lines.append([board[i][i] for i in range(3)])
    lines.append([board[i][2 - i] for i in range(3)])

    for line in lines:
        if line.count(line[0]) == 3 and line[0] != EMPTY:
            return line[0]
    return None


# ---------- ACTIONS ----------
def actions(state):
    moves = []
    for i in range(3):
        for j in range(3):
            if state[i][j] == EMPTY:
                moves.append((i, j))
    return moves


# ---------- RESULT ----------
def result(state, action, player):
    new_state = copy.deepcopy(state)
    i, j = action
    new_state[i][j] = player
    return new_state


# ---------- MAX-VALUE ----------
def max_value(state):
    if terminal_test(state):
        return utility(state)

    v = -math.inf
    for action in actions(state):
        v = max(v, min_value(result(state, action, MAX)))
    return v


# ---------- MIN-VALUE ----------
def min_value(state):
    if terminal_test(state):
        return utility(state)

    v = math.inf
    for action in actions(state):
        v = min(v, max_value(result(state, action, MIN)))
    return v


# ---------- MINIMAX DECISION ----------
def minimax_decision(state):
    best_value = -math.inf
    best_action = None

    for action in actions(state):
        value = min_value(result(state, action, MAX))
        if value > best_value:
            best_value = value
            best_action = action

    return best_action


# ---------- DISPLAY ----------
def print_board(board):
    for row in board:
        print(" ".join(row))
    print()


# ---------- GAME LOOP ----------
def play_game():
    board = [[EMPTY]*3 for _ in range(3)]
    print("You are O (MIN). AI is X (MAX).")
    print_board(board)

    while not terminal_test(board):
        # Human Move
        row, col = map(int, input("Enter row and column (0-2 0-2): ").split())
        if board[row][col] != EMPTY:
            print("Invalid move!")
            continue
        board[row][col] = MIN
        print_board(board)

        if terminal_test(board):
            break

        # AI Move
        ai_move = minimax_decision(board)
        board = result(board, ai_move, MAX)
        print("AI move:")
        print_board(board)

    # Game Result
    winner = check_winner(board)
    if winner == MAX:
        print("AI wins!")
    elif winner == MIN:
        print("You win!")
    else:
        print("It's a draw!")


# ---------- START ----------
play_game()