                  
def heu(node,goal):
    return abs(node[0]-goal[0])+abs(node[1]-goal[1])


def _neighbors(node, problem):
    x,y = node
    moves = [(1,0),(-1,0),(0,1),(0,-1)]
    neighbors = []

    for dx,dy in moves:
        nx,ny = x + dx, y + dy
        if 0 <= nx < len(problem) and 0 <= ny < len(problem[0]):
            neighbors.append((nx, ny))
    return neighbors


def bfs(start,goal,problem):
    frnt = [start]
    explored = set()
    parent = {}
    
    
    while frnt:
        best_node = frnt[0]
        for node in frnt:
            if heu(node, goal)<heu(best_node, goal):
                best_node = node
        frnt.remove(best_node)
        
        if best_node == goal:
            return path(parent,start,goal)
        explored.add(best_node)

        for n in _neighbors(best_node, problem):
            if n not in explored and n not in frnt:
                parent[n] = best_node
                frnt.append(n)
                

    while frnt:
        node = frnt.pop(0)

        if node == goal:
            return path(parent,start,goal)

        explored.add(node)
        for n in _neighbors(node,problem):
            if n not in explored and n not in frnt:
                parent[n] = node
                frnt.append(n)
    return None

def path():
    


problem = [
        [0,1,0,0,0,0,1,0,1,0],
        [0,0,0,0,1,0,1,0,1,0],
        [0,1,0,0,0,0,1,0,1,0],
        [0,0,0,0,1,0,1,0,1,0],
        [0,1,0,0,0,0,1,0,1,0],
        [0,0,0,0,1,0,1,0,1,0],
    ]

show = bfs()