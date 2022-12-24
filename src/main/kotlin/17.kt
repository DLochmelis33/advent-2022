import java.io.File

typealias RockSpawner = (Pair<Int, Int>) -> Set<Pair<Int, Int>>

// /|\ . # .
//  |  # # #
//  y  @ # .
//  . x ->

private val rockList: List<RockSpawner> = listOf(
    { (x, y) -> setOf(x to y, x + 1 to y, x + 2 to y, x + 3 to y) },
    { (x, y) -> setOf(x + 1 to y, x to y + 1, x + 1 to y + 1, x + 1 to y + 2, x + 2 to y + 1) },
    { (x, y) -> setOf(x to y, x + 1 to y, x + 2 to y, x + 2 to y + 1, x + 2 to y + 2) },
    { (x, y) -> setOf(x to y, x to y + 1, x to y + 2, x to y + 3) },
    { (x, y) -> setOf(x to y, x + 1 to y, x to y + 1, x + 1 to y + 1) }
)

private val rocks = sequence { while (true) yieldAll(rockList) }.iterator()

fun main() {
    val jetPattern = File("in.txt").readLines().first().toList()
    val jets = sequence { while (true) yieldAll(jetPattern) }.iterator()

    fun makeWorld() = List(7) { MutableList(1_000_000) { false } }
    val world = makeWorld()
    var height = 0

    fun updateHeight() {
        while (world.any { it[height] }) height++
    }

    fun Set<Pair<Int, Int>>.tryMove(shift: Pair<Int, Int>) =
        this.map { it + shift }.takeUnless { it.any { (x, y) -> x !in 0 until 7 || y < 0 || world[x][y] } }?.toSet()

    fun dropRock() {
        var rock = rocks.next().invoke(2 to height + 3)
        while (true) {
            val jetShift = when (jets.next()) {
                '<' -> -1 to 0
                '>' -> 1 to 0
                else -> error("wrong jet")
            }
            rock = rock.tryMove(jetShift) ?: rock
            rock = rock.tryMove(0 to -1) ?: break
        }
        rock.forEach { (x, y) -> world[x][y] = true }
        updateHeight()

//        println("dropped a rock:")
//        for (y in height downTo 0) {
//            for (x in 0 until 7) print(if (world[x][y]) '#' else '.')
//            println()
//        }
//        println("\n")
    }

    repeat(2022) { dropRock() }
    println(height)
}
