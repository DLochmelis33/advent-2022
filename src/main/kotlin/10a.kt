sealed class CpuState(val execTime: Int)
class NOOP : CpuState(1)
data class ADDX(val value: Int) : CpuState(2)

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
    var signalSum = 0
    for (c in commands) {
        repeat(c.execTime) {
            cycle++
            if ((cycle + 20) % 40 == 0) {
                signalSum += cycle * x
            }
        }
        when (c) {
            is NOOP -> {}
            is ADDX -> x += c.value
        }
    }
    println(signalSum)
}