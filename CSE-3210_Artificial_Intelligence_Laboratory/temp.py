def dfs(graph, node, goal, visited, explored_count):
    explored_count[0] += 1

    if node == goal:
        return True

    visited.add(node)

    for child in graph.get(node, []):
        if child not in visited:
            if dfs(graph, child, goal, visited, explored_count):
                return True
    return False


def dls(graph, node, goal, limit, visited=None, explored_count=None):
    if visited is None:
        visited = set()

    if explored_count is None:
        explored_count = [0]

    explored_count[0] += 1

    if node == goal:
        return 'SUCCESS'

    if limit == 0:
        return 'CUTOFF'

    visited.add(node)
    cutoff_occurred = False

    for child in graph.get(node, []):
        if child not in visited:
            result = dls(
                graph,
                child,
                goal,
                limit - 1,
                visited.copy(),
                explored_count
            )

            if result == 'SUCCESS':
                return 'SUCCESS'
            elif result == 'CUTOFF':
                cutoff_occurred = True

    if cutoff_occurred:
        return 'CUTOFF'
    return 'FAILURE'


def iddfs(graph, start, goal, max_depth=100):
    total_explored_count = [0]

    for depth in range(max_depth):
        visited_for_dls = set()
        result = dls(
            graph,
            start,
            goal,
            depth,
            visited=visited_for_dls,
            explored_count=total_explored_count
        )

        if result == 'SUCCESS':
            return depth, total_explored_count[0]

    return -1, total_explored_count[0]


if __name__ == "__main__":
    graph = {
        '1': ['2', '3'],
        '2': ['4', '5'],
        '3': [],
        '4': ['8', '13'],
        '5': ['6', '7'],
        '6': [],
        '7': ['10', '12'],
        '8': ['9'],
        '9': [],
        '10': ['11'],
        '11': [],
        '12': [],
        '13': []
    }

    start = '1'
    goal = '6'

    print("DFS")
    visited = set()
    explored_count_dfs = [0]
    result_dfs = dfs(graph, start, goal, visited, explored_count_dfs)
    print(f"Found: {result_dfs}")
    print(f"Total explored: {explored_count_dfs[0]}")
    print("Visited:", visited)

    print("\nIDDFS")
    depth_found, explored_count_iddfs = iddfs(graph, start, goal)

    if depth_found != -1:
        print(f"Found at depth: {depth_found}")
        print(f"Total explored: {explored_count_iddfs}")
    else:
        print("Not found")
        print(f"Total explored: {explored_count_iddfs}")
