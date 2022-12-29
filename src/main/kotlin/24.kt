import java.io.File

private data class Pos(val x: Int, val y: Int)

private sealed class Direction(val xShift: Int, val yShift: Int)
private object Up : Direction(0, -1)
private object Left : Direction(-1, 0)
private object Down : Direction(0, 1)
private object Right : Direction(1, 0)

fun main() {
    val input = File("in.txt").readLines()
    val xMin = 0
    val xMax = input[0].length - 1
    val yMin = 0
    val yMax = input.size - 1
    var blizzards: List<Pair<Pos, Direction>> = input.drop(1).dropLast(1)
        .withIndex().flatMap { (i, s) ->
            s.withIndex().mapNotNull { (j, c) ->
                val x = j
                val y = i + 1
                val dir = when (c) {
                    '^' -> Up
                    '<' -> Left
                    'v' -> Down
                    '>' -> Right
                    else -> null
                } ?: return@mapNotNull null
                Pos(x, y) to dir
            }
        }
    var occupiedPos: Set<Pos> = emptySet()
    var t = 0

    fun stepWorld() {
        blizzards = blizzards.map { (pos, dir) ->
            val nextPos = run {
                var x = pos.x
                var y = pos.y
                x += dir.xShift
                if (x == xMax) x = xMin + 1
                if (x == xMin) x = xMax - 1
                y += dir.yShift
                if (y == yMax) y = yMin + 1
                if (y == yMin) y = yMax - 1
                Pos(x, y)
            }
            nextPos to dir
        }
        occupiedPos = blizzards.map { it.first }.toSet()
        t++
    }

    val start = Pos(1, 0)
    val end = Pos(xMax - 1, yMax)

    fun Pos.getNeighbors() = listOf(
        Pos(x, y - 1), Pos(x - 1, y), Pos(x, y + 1), Pos(x + 1, y), Pos(x, y)
    ).filter { it !in occupiedPos && (it == end || it == start || (it.x in (xMin + 1) until xMax && it.y in (yMin + 1) until yMax)) }

    // no need to build full graph, just maintain bfs queue
    var currentPositions: Set<Pos> = setOf(start)
    val targets = mutableListOf(end, start, end)
    while (true) {
        stepWorld()
        currentPositions = currentPositions.flatMap { it.getNeighbors() }.toSet()
        assert(currentPositions.none { it in occupiedPos })
        assert(currentPositions.isNotEmpty())

        if (targets.first() in currentPositions) {
            currentPositions = setOf(targets.first())
            targets.removeFirst()
            if(targets.isEmpty()) break
        }

//        println("after time $t:")
//        (yMin..yMax).forEach { y ->
//            (xMin..xMax).map { x ->
//                if (y == yMin || y == yMax || x == xMin || x == xMax) return@map '#'
//                val bs = blizzards.filter { (pos, _) -> pos.x == x && pos.y == y }
//                if (bs.size > 1) bs.size.digitToChar() else if (bs.size == 1) when (bs.first().second) {
//                    Up -> '^'
//                    Left -> '<'
//                    Down -> 'v'
//                    Right -> '>'
//                } else if (Pos(x, y) in currentPositions) '@' else '.'
//            }.forEach { print(it) }
//            println()
//        }
//        println()
    }

    println(t)
}