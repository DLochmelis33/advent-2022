import java.io.File
import kotlin.math.abs

private typealias Cube = Triple<Int, Int, Int>

private val Cube.x get() = first
private val Cube.y get() = second
private val Cube.z get() = third

infix fun Cube.adjacentTo(c: Cube): Boolean = abs(x - c.x) + abs(y - c.y) + abs(z - c.z) == 1

private const val MIN_COORD = -1
private const val MAX_COORD = 20

private fun genAdj(c: Cube) = listOf(
    Cube(c.x - 1, c.y, c.z),
    Cube(c.x + 1, c.y, c.z),
    Cube(c.x, c.y - 1, c.z),
    Cube(c.x, c.y + 1, c.z),
    Cube(c.x, c.y, c.z - 1),
    Cube(c.x, c.y, c.z + 1)
).filter { (x, y, z) -> listOf(x, y, z).all { it in MIN_COORD..MAX_COORD } }

fun main() {
    val givenCubes = File("in.txt").readLines().map {
        val (x, y, z) = it.split(",").map(String::toInt); Cube(x, y, z).also {
        assert(listOf(x, y, z).all { it in (MIN_COORD + 1) until MAX_COORD })
    }
    }.toSet()

    fun bfsFill(start: Cube): Set<Cube> {
        val filledCubes = mutableSetOf<Cube>()
        val queue = mutableListOf(start)
        while (queue.isNotEmpty()) {
            val c = queue.removeFirst()
            if (c in filledCubes) continue
            filledCubes.add(c)
            queue.addAll(genAdj(c).filter { it !in filledCubes && it !in givenCubes })
        }
        return filledCubes
    }

    val filledCubes = bfsFill(Cube(MIN_COORD, MIN_COORD, MIN_COORD))
    val result = filledCubes.sumOf { c -> givenCubes.count { it adjacentTo c } }
    println(result)
}