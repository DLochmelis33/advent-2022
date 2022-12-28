import java.io.File

private typealias Coord = Pair<Int, Int> // X to Y

fun main() {
    val initialPositions = File("in.txt").readLines().withIndex()
        .flatMap { (i, s) -> s.withIndex().filter { (_, c) -> c == '#' }.map { (j, _) -> j to i } }

    val positions = initialPositions.toMutableSet()
    fun isEmpty(vararg coords: Coord) = coords.all { it !in positions }
    val intentionChecks = mutableListOf<(Coord) -> Coord?>(
        { (x, y) -> if (isEmpty(x - 1 to y - 1, x to y - 1, x + 1 to y - 1)) x to y - 1 else null },
        { (x, y) -> if (isEmpty(x - 1 to y + 1, x to y + 1, x + 1 to y + 1)) x to y + 1 else null },
        { (x, y) -> if (isEmpty(x - 1 to y - 1, x - 1 to y, x - 1 to y + 1)) x - 1 to y else null },
        { (x, y) -> if (isEmpty(x + 1 to y - 1, x + 1 to y, x + 1 to y + 1)) x + 1 to y else null },
    )

    data class Intention(val from: Coord, val to: Coord)

    fun calcRegion(): Pair<IntRange, IntRange> {
        return positions.minOf { it.first }..positions.maxOf { it.first } to
                positions.minOf { it.second }..positions.maxOf { it.second }
    }

    fun printRegion() {
        val (xRange, yRange) = calcRegion()
        for (y in yRange) {
            for (x in xRange) print(if (x to y in positions) '#' else '.')
            println()
        }
    }

    repeat(10) {
        // phase 1: intentions
        val intentions = positions.map { pos ->
            val (x, y) = pos
            val to = if (isEmpty(
                    x - 1 to y - 1,
                    x to y - 1,
                    x + 1 to y - 1,
                    x + 1 to y,
                    x + 1 to y + 1,
                    x to y + 1,
                    x - 1 to y + 1,
                    x - 1 to y
                )
            ) (x to y) else intentionChecks.firstNotNullOfOrNull { it(pos) } ?: pos
            Intention(pos, to)
        }
        // phase 2: move
        intentions.filter { itn ->
            intentions.none { it.to == itn.to && it !== itn }
        }.forEach { (from, to) ->
            positions.remove(from)
            positions.add(to)
        }
        // phase 3: rotate checks
        intentionChecks.add(intentionChecks.removeFirst())
    }

    val result = calcRegion().let { (xr, yr) -> (xr.last - xr.first + 1) * (yr.last - yr.first + 1) - positions.size }
    println(result)
}