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

    fun makeWorld() = List(7) { MutableList(100_000_000) { false } }
    val world = makeWorld()
    var height = 0
    println("init world")

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
    }

    val gcd = rockList.size * jetPattern.size
    println("gcd = $gcd")

    val magicNumber = 400 // must be larger than period's cnt AND IS ALSO MAGIC
    fun gcdDrop() = repeat(gcd) { dropRock() }
    fun prepareDrop() =
        repeat(magicNumber) { i -> gcdDrop().also { println("preparing: done ${i + 1} / $magicNumber") } }

    var periodHeight: Int? = null
    fun findPeriod(): Int {
        fun takeSnapshot() = world.map { it.subList(height - 100, height) }

        prepareDrop()
        val target = takeSnapshot()
        val prepHeight = height
        var cnt = 0
        while (true) {
            cnt++
            gcdDrop()
            if (target == takeSnapshot()) break
            println("finding period: cnt=$cnt")
        }
        periodHeight = height - prepHeight
        return gcd * cnt
    }

    val period = findPeriod()
    println("period = $period")
    val curDrops = magicNumber * gcd + period

    val leftRocks = 1000000000000L - curDrops
    val curHeight = height

    println("doing extra ${leftRocks % period} drops")
    repeat((leftRocks % period).toInt()) { dropRock() }
    val extraHeight = height - curHeight

    println("RESULT:")
    println(curHeight + leftRocks / period * periodHeight!! + extraHeight)
}
