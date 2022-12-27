import java.io.File

private const val width = 150
private const val height = 200
//private const val width = 16
//private const val height = 12

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

    fun findNextPos(): Pair<Int, Int> {
        val (ny, nx) = curPos + curDir.shift
        fun getWrappedPos() = when (curDir) {
            Dir.UP -> world.indexOfLast { it[nx] != ' ' } to nx
            Dir.LEFT -> ny to world[ny].indexOfLast { it != ' ' }
            Dir.DOWN -> world.indexOfFirst { it[nx] != ' ' } to nx
            Dir.RIGHT -> ny to world[ny].indexOfFirst { it != ' ' }
        }

        return if (ny !in 0 until height || nx !in 0 until width || world[ny][nx] == ' ') {
            getWrappedPos()
        } else ny to nx
    }

    for (a in actions) {
        when (a) {
            is Move -> repeat(a.dist) {
                val (ny, nx) = findNextPos()
                if (world[ny][nx] == '.') {
                    curPos = ny to nx
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

//        println("after action $a:")
//        for ((y, row) in world.withIndex()) {
//            if (y == curPos.first)
//                println(row.toMutableList().apply { this[curPos.second] = '@' }.joinToString(separator = ""))
//            else
//                println(row.joinToString(separator = ""))
//        }
//        println("\n")
    }

    println("result pos = $curPos, dir = $curDir")
    println("result = ${(curPos.first + 1) * 1000 + (curPos.second + 1) * 4 + curDir.value}")
}