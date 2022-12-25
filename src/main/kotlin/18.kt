import java.io.File
import kotlin.math.abs

infix fun Triple<Int, Int, Int>.adjacentTo(triple: Triple<Int, Int, Int>): Boolean =
    abs(first - triple.first) + abs(second - triple.second) + abs(third - triple.third) == 1

fun main() {
    val cubes = File("in.txt").readLines().map {
        val (x, y, z) = it.split(",").map(String::toInt); Triple(x, y, z)
    }
    val adjSides = cubes.sumOf { cube -> cubes.count { it adjacentTo cube } }
    val totalSides = cubes.size * 6
    val result = totalSides - adjSides
    println(result)
}