import java.io.File

fun Char.asSnafu(): Int = when (this) {
    '2' -> 2
    '1' -> 1
    '0' -> 0
    '-' -> -1
    '=' -> -2
    else -> error("invalid snafu symbol '$this'")
}

fun Int.toSnafu(): Char = when (this) {
    2 -> '2'
    1 -> '1'
    0 -> '0'
    -1 -> '-'
    -2 -> '='
    else -> error("invalid int '$this' to snafu")
}

fun main() {
    val input = File("in.txt").readLines().map { s ->
        s.reversed().map { it.asSnafu() }
    }

    val maxRadix = input.maxOf { it.size }
    val sum = MutableList(1000) { 0 }
    for (i in 0 until maxRadix) {
        sum[i] = input.sumOf { it.getOrElse(i) { 0 } }
    }

    for (i in 0 until 999) {
        while (sum[i] > 2) {
            sum[i + 1] += 1
            sum[i] -= 5
        }
        while (sum[i] < -2) {
            sum[i + 1] -= 1
            sum[i] += 5
        }
    }

    val result = sum.reversed().joinToString("") { it.toSnafu().toString() }.trimStart('0')
    println(result)
}