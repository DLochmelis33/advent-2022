import kotlin.math.abs

fun main() {
    val commands = buildList {
        while (true) {
            val s = readln().split(' ')
            when (s[0]) {
                "hehe" -> break
                "noop" -> add(NOOP())
                "addx" -> add(ADDX(s[1].toInt()))
            }
        }
    }
    var x = 1
    var cycle = 0
    for (c in commands) {
        repeat(c.execTime) {
            print(if (abs((cycle % 40) - x) <= 1) 'X' else '.')
            cycle++
            if (cycle % 40 == 0) {
                println()
            }
        }
        when (c) {
            is NOOP -> {}
            is ADDX -> x += c.value
        }
    }
}