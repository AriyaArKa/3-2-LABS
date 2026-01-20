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
    level_explored = {}   

    for depth in range(max_depth):
        before = total_explored_count[0]

        result = dls(
            graph,
            start,
            goal,
            depth,
            visited=set(),
            explored_count=total_explored_count
        )

        after = total_explored_count[0]
        level_explored[depth] = after - before

        if result == 'SUCCESS':
            return depth, total_explored_count[0], level_explored

    return -1, total_explored_count[0], level_explored


if __name__ == "__main__":
    graph = {
        'a': ['b','c'],
        'b': ['a','d','e'],
        'c': ['a'],
        'd': ['b','f','g'],
        'e': ['b'],
        'f': ['d','h','i'],
        'g': ['d', 'j','k'],
        'h': ['f'],
        'i': ['f'],
        'j': ['g'],
        'k': ['g']
    }

    start = 'a'
    goal = 'e'

    print("DFS")
    visited = set()
    explored_count_dfs = [0]
    result_dfs = dfs(graph, start, goal, visited, explored_count_dfs)
    print(f"Found: {result_dfs}")
    print(f"Total explored: {explored_count_dfs[0]}")
    print("Visited:", visited)

    print("\nIDDFS")
    depth_found, explored_count_iddfs, level_explored = iddfs(graph, start, goal)

    if depth_found != -1:
        print(f"Found at depth: {depth_found}")
        print(f"Total explored: {explored_count_iddfs}")
        print("Nodes explored at each depth:")
        for d, e in level_explored.items():
            print(f"  Depth {d}: {e}")
    else:
        print("Not found")
        print(f"Total explored: {explored_count_iddfs}")
