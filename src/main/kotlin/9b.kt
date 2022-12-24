import kotlin.math.abs

operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>) = (first + other.first) to (second + other.second)

operator fun Pair<Int, Int>.minus(other: Pair<Int, Int>) = (first - other.first) to (second - other.second)

private infix fun Pair<Int, Int>.dist(other: Pair<Int, Int>) = abs(first - other.first) + abs(second - other.second)

class Rope {
    inner class Node(var x: Int, var y: Int, val child: Node?) {
        fun move(nextParent: Pair<Int, Int>) {
            val delta = nextParent - (x to y)
            val (dx, dy) = when (delta) {
                0 to 0, 1 to 0, -1 to 0, 0 to 1, 0 to -1, 1 to 1, 1 to -1, -1 to -1, -1 to 1 -> 0 to 0
                2 to 0 -> 1 to 0
                -2 to 0 -> -1 to 0
                0 to 2 -> 0 to 1
                0 to -2 -> 0 to -1
                1 to 2, 2 to 2, 2 to 1 -> 1 to 1
                2 to -1, 2 to -2, 1 to -2 -> 1 to -1
                -1 to -2, -2 to -2, -2 to -1 -> -1 to -1
                -2 to 1, -2 to 2, -1 to 2 -> -1 to 1
                else -> error("wrong delta")
            }
            x += dx; y += dy
            child?.move(x to y)
        }
    }

    val nodes = buildList {
        var cur: Node? = null
        repeat(10) { add(Node(0, 0, cur).also { cur = it }) }
    }

    val tailBeenCoords = mutableSetOf(0 to 0)

    fun moveHead(dirStr: String) {
        val (dx, dy) = when (dirStr) {
            "R" -> 0 to 1
            "U" -> 1 to 0
            "L" -> 0 to -1
            "D" -> -1 to 0
            else -> error("wrong dir")
        }
        nodes.last().run {
            x += dx; y += dy
            child?.move(x to y)
        }
        tailBeenCoords.add(nodes.first().run { x to y })
    }
}

fun main() {
    val rope = Rope()
    while (true) {
        val s = readln()
        if (s == "hehe") break
        val (dirStr, countStr) = s.split(" ")
        repeat(countStr.toInt()) {
            rope.moveHead(dirStr)
        }
    }
    println(rope.tailBeenCoords.size)
}