import java.io.File
import java.util.*

fun main() {
    System.setIn(File("in.txt").inputStream())
    val table = generateSequence { readlnOrNull()?.toList() }.toList()
    val elevations = table.map {
        it.map { c ->
            when (c) {
                'S' -> 0; 'E' -> 25; else -> c - 'a'
            }
        }
    }
    val indPairs = table.indices.flatMap { i -> table[0].indices.map { j -> i to j } }
    val graph = indPairs.associateWith { (i, j) ->
        listOf(i + 1 to j, i - 1 to j, i to j + 1, i to j - 1).filter { (y, x) ->
            y in elevations.indices && x in elevations[0].indices && elevations[y][x] - elevations[i][j] <= 1
        }
    }

    fun bfs(start: Pair<Int, Int>, target: Pair<Int, Int>): Int {
        val been = mutableSetOf(start)
        val queue: Queue<Pair<Int, Int>> = LinkedList<Pair<Int, Int>>().apply { add(start) }
        val buffer = mutableSetOf<Pair<Int, Int>>()
        var dist = 0
        dist@ while (true) {
            dist++
            while (queue.isNotEmpty()) {
                val v = queue.poll()
                for (u in graph[v]!!) {
                    been.add(v)
                    if (u == target)
                        return dist
                    if (u !in been)
                        buffer.add(u)
                }
            }
            queue.addAll(buffer)
            buffer.clear()
            if (queue.isEmpty()) error("graph exhausted")
        }
    }

    val start = indPairs.first { (i, j) -> table[i][j] == 'S' }
    val target = indPairs.first { (i, j) -> table[i][j] == 'E' }
    println(bfs(start, target))
}