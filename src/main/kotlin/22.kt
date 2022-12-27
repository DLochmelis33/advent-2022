import java.io.File

//private const val width = 150
//private const val height = 200
private const val width = 16
private const val height = 12

private sealed class Action
private data class Move(val dist: Int) : Action()
private object TurnLeft : Action()
private object TurnRight : Action()

// y, x
private enum class Dir(
    val shift: Pair<Int, Int>,
    val value: Int
) { UP(-1 to 0, 3), LEFT(0 to -1, 2), DOWN(1 to 0, 1), RIGHT(0 to 1, 0) }

fun main() {
    val input = File("in.txt").readLines()
    val world = input.take(height).map { (it.toList() + List(width) { ' ' }).take(width) }

    fun parseActions(input: String): List<Action> {
        var i = 0
        val numRegex = Regex("\\d+")
        val moveExtractor = {
            numRegex.matchAt(input, i)!!.groupValues[0].let {
                i += it.length
                Move(it.toInt())
            }
        }
        val turnExtractor = {
            when (input[i]) {
                'L' -> TurnLeft; 'R' -> TurnRight; else -> error("wtf '${input[i]}, i=$i'")
            }.also { i++ }
        }
        val actions = mutableListOf<Action>()
        while (i < input.length) {
            actions.add(moveExtractor())
            if (i == input.length) break
            actions.add(turnExtractor())
        }
        return actions
    }

    val actions = parseActions(input.drop(height + 1).first())

    var curPos: Pair<Int, Int> = 0 to world[0].indexOfFirst { it == '.' }
    var curDir: Dir = Dir.RIGHT

    val wrapRules = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()
//  MAIN INPUT
//    val a = (0 until 50).map { 199 to it }
//    val b = (150 until 200).map { it to 0 }
//    val c = (100 until 150).map { it to 0 }
//    val d = (0 until 50).map { 100 to it }
//    val e = (50 until 100).map { it to 50 }
//    val f = (0 until 50).map { it to 50 }
//    val g = (50 until 100).map { 0 to it }
//    val h = (100 until 150).map { 0 to it }
//    val i = (0 until 50).map { it to 149 }
//    val j = (100 until 150).map { 49 to it }
//    val k = (50 until 100).map { it to 99 }
//    val l = (100 until 150).map { it to 99 }
//    val m = (50 until 100).map { 149 to it }
//    val n = (150 until 200).map { it to 49 }
//    val edges = listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n)
//    val rules = listOf(
//        a zip h,
//        h zip a,
//        b zip g,
//        g zip b,
//        c zip f.reversed(),
//        f zip c.reversed(),
//        d zip e,
//        e zip d,
//        i zip l.reversed(),
//        l zip i.reversed(),
//        j zip k,
//        k zip j,
//        m zip n,
//        n zip m
//    )
//    for (rule in rules) wrapRules += rule
//    val checkWrapDir = mapOf(
//        a to Dir.DOWN,
//        b to Dir.LEFT,
//        c to Dir.LEFT,
//        d to Dir.UP,
//        e to Dir.LEFT,
//        f to Dir.LEFT,
//        g to Dir.UP,
//        h to Dir.UP,
//        i to Dir.RIGHT,
//        j to Dir.DOWN,
//        k to Dir.RIGHT,
//        l to Dir.RIGHT,
//        m to Dir.DOWN,
//        n to Dir.RIGHT
//    )
//    val changeWrapDir = mapOf(
//        a to Dir.DOWN, // h
//        b to Dir.DOWN, // g
//        c to Dir.RIGHT, // f
//        d to Dir.RIGHT, // e
//        e to Dir.DOWN, // d
//        f to Dir.RIGHT, // c
//        g to Dir.RIGHT, // b
//        h to Dir.UP, // a
//        i to Dir.LEFT, // l
//        j to Dir.LEFT, // k
//        k to Dir.UP, // j
//        l to Dir.LEFT, // i
//        m to Dir.LEFT, // n
//        n to Dir.UP, // m
//    )

    // TEST INPUT
    val a = (4 until 8).map { it to 0 }
    val b = (0 until 4).map { 4 to it }
    val c = (4 until 8).map { 4 to it }
    val d = (0 until 4).map { it to 8 }
    val e = (8 until 12).map { 0 to it }
    val f = (0 until 4).map { it to 11 }
    val g = (4 until 8).map { it to 11 }
    val h = (12 until 16).map { 8 to it }
    val i = (8 until 12).map { it to 15 }
    val j = (12 until 16).map { 11 to it }
    val k = (8 until 12).map { 11 to it }
    val l = (8 until 12).map { it to 8 }
    val m = (4 until 8).map { 7 to it }
    val n = (0 until 4).map { 7 to it }
    val edges = listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n)
    listOf(
        a zip j.reversed(),
        j zip a.reversed(),
        b zip e.reversed(),
        e zip b.reversed(),
        c zip d,
        d zip c,
        f zip i.reversed(),
        i zip f.reversed(),
        g zip h.reversed(),
        h zip g.reversed(),
        k zip n.reversed(),
        n zip k.reversed(),
        l zip m.reversed(),
        m zip l.reversed()
    ).forEach { wrapRules += it }
    val checkWrapDir = mapOf(
        a to Dir.LEFT,
        b to Dir.UP,
        c to Dir.UP,
        d to Dir.LEFT,
        e to Dir.UP,
        f to Dir.RIGHT,
        g to Dir.RIGHT,
        h to Dir.UP,
        i to Dir.RIGHT,
        j to Dir.DOWN,
        k to Dir.DOWN,
        l to Dir.LEFT,
        m to Dir.DOWN,
        n to Dir.DOWN
    )
    val changeWrapDir = mapOf(
        a to Dir.DOWN,
        b to Dir.DOWN,
        c to Dir.RIGHT,
        d to Dir.DOWN,
        e to Dir.DOWN,
        f to Dir.UP,
        g to Dir.DOWN,
        h to Dir.LEFT,
        i to Dir.LEFT,
        k to Dir.UP,
        l to Dir.UP,
        m to Dir.RIGHT,
        n to Dir.UP
    )

    fun calcNext(): Pair<Pair<Int, Int>, Dir> {
        edges.firstOrNull { curPos in it }?.let { letter ->
            if (curDir == checkWrapDir[letter]) {
                return wrapRules[curPos]!! to changeWrapDir[letter]!!
            }
        }
        return (curPos + curDir.shift) to curDir
    }

    for (action in actions) {
        when (action) {
            is Move -> repeat(action.dist) {
                if (curPos == 0 to 69)
                    println("hehe")
                val (nextPos, nextDir) = calcNext()
                val (ny, nx) = nextPos
                if (world[ny][nx] == '.') {
                    curPos = ny to nx
                    curDir = nextDir
                } else assert(world[ny][nx] == '#')
            }
            is TurnRight -> curDir = when (curDir) {
                Dir.UP -> Dir.RIGHT
                Dir.RIGHT -> Dir.DOWN
                Dir.DOWN -> Dir.LEFT
                Dir.LEFT -> Dir.UP
            }
            is TurnLeft -> curDir = when (curDir) {
                Dir.UP -> Dir.LEFT
                Dir.LEFT -> Dir.DOWN
                Dir.DOWN -> Dir.RIGHT
                Dir.RIGHT -> Dir.UP
            }
        }

        println("after action $action:")
//        for ((y, row) in world.withIndex()) {
//            if (y == curPos.first)
//                println(row.toMutableList().apply { this[curPos.second] = '@' }.joinToString(separator = ""))
//            else
//                println(row.joinToString(separator = ""))
//        }
        println("current pos = $curPos, dir = $curDir")
        println("\n")
    }

    println("result pos = $curPos, dir = $curDir")
    println("result = ${(curPos.first + 1) * 1000 + (curPos.second + 1) * 4 + curDir.value}")
}