# ===============================
# Australia Map Coloring Problem
# Simple vs Smart Backtracking
# ===============================

regions = ["WA", "NT", "SA", "Q", "NSW", "V", "T"]
colors = ["red", "green", "blue"]

neighbours = {
    "WA": ["NT", "SA"],
    "NT": ["SA", "WA", "Q"],
    "Q": ["NT", "SA", "NSW"],
    "NSW": ["Q", "V", "SA"],
    "V": ["SA", "NSW"],
    "SA": ["WA", "Q", "NSW", "V", "NT"],
    "T": [],
}

# ===============================
# Common Constraint Checker
# ===============================


def is_valid(region, color, assignment):
    for n in neighbours[region]:
        if n in assignment and assignment[n] == color:
            return False
    return True


# ===============================
# 1️⃣ SIMPLE BACKTRACKING
# ===============================

simple_calls = 0


def simple_backtracking(assignment):
    global simple_calls
    simple_calls += 1

    if len(assignment) == len(regions):
        return assignment

    # Pick first unassigned region
    for region in regions:
        if region not in assignment:
            break

    for color in colors:
        if is_valid(region, color, assignment):
            assignment[region] = color
            result = simple_backtracking(assignment)

            if result:
                return result

            del assignment[region]

    return None


# ===============================
# 2️⃣ SMART BACKTRACKING
#    (MRV + LCV)
# ===============================

smart_calls = 0


def mrv(assignment):
    unassigned = [r for r in regions if r not in assignment]
    best = None
    min_count = float("inf")

    for region in unassigned:
        count = sum(1 for color in colors if is_valid(region, color, assignment))

        if count < min_count:
            min_count = count
            best = region

    return best


def lcv(region, assignment):
    value_list = []

    for color in colors:
        conflict = 0

        for n in neighbours[region]:
            if n not in assignment:
                if not is_valid(n, color, assignment):
                    conflict += 1

        value_list.append((conflict, color))

    value_list.sort()
    return [color for _, color in value_list]


def smart_backtracking(assignment):
    global smart_calls
    smart_calls += 1

    if len(assignment) == len(regions):
        return assignment

    region = mrv(assignment)

    for color in lcv(region, assignment):
        if is_valid(region, color, assignment):
            assignment[region] = color
            result = smart_backtracking(assignment)

            if result:
                return result

            del assignment[region]

    return None


# ===============================
# RUN BOTH METHODS
# ===============================

print("=== SIMPLE BACKTRACKING ===")
solution1 = simple_backtracking({})
print("Solution:", solution1)
print("Backtracking Calls:", simple_calls)

print("\n=== SMART BACKTRACKING (MRV + LCV) ===")
solution2 = smart_backtracking({})
print("Solution:", solution2)
print("Backtracking Calls:", smart_calls)


# ===============================
# COMPARISON
# ===============================

print("\n=== COMPARISON ===")
print("Simple Calls :", simple_calls)
print("Smart Calls  :", smart_calls)

if simple_calls > smart_calls:
    print("Smart method is more efficient.")
elif simple_calls < smart_calls:
    print("Simple method is more efficient.")
else:
    print("Both methods performed the same.")
