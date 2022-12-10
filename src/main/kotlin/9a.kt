import kotlin.math.abs

private operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>) = (first + other.first) to (second + other.second)

private infix fun Pair<Int, Int>.dist(other: Pair<Int, Int>) = abs(first - other.first) + abs(second - other.second)

fun main() {
    var head = 0 to 0
    var tail = 0 to 0
    val beenCoords = mutableSetOf(0 to 0)
    while (true) {
        val s = readln()
        if (s == "hehe") break
        val (dirStr, countStr) = s.split(" ")
        repeat(countStr.toInt()) {
            val nextHead = head + when (dirStr) {
                "R" -> 0 to 1
                "U" -> 1 to 0
                "L" -> 0 to -1
                "D" -> -1 to 0
                else -> error("wrong dir")
            }
            tail = when (tail dist nextHead) {
                0, 1 -> tail
                2 -> if (abs(tail.first - nextHead.first) == 2 || abs(tail.second - nextHead.second) == 2) head else tail
                3 -> head
                else -> error("bad dist")
            }
            beenCoords.add(tail)
            head = nextHead
        }
    }
    println(beenCoords.size)
}