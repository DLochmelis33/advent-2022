import java.io.File
import kotlin.math.max
import kotlin.math.min

fun main() {
    val world = List(200) { MutableList(600) { false } }
    File("in.txt").readLines().forEach { line ->
        val rockPath = line.split(" -> ").map {
            val (x, y) = it.split(",")
            y.toInt() to x.toInt()
        }
        for ((p1, p2) in rockPath.zipWithNext()) {
            val (y1, x1) = p1
            val (y2, x2) = p2
            if (y1 == y2) {
                for (j in (min(x1, x2)..max(x1, x2))) world[y1][j] = true
            } else if (x1 == x2) {
                for (i in (min(y1, y2)..max(y1, y2))) world[i][x1] = true
            } else error("not straight line")
        }
    }
    var cnt = 0
    emit@ while (true) {
        var y = 0
        var x = 500
        fall@ while (true) {
            if (y >= 199) break@emit
            else if (!world[y + 1][x]) {
                y++
            } else if (!world[y + 1][x - 1]) {
                y++; x--
            } else if (!world[y + 1][x + 1]) {
                y++; x++
            } else break@fall
        }
        world[y][x] = true
        cnt++
    }
    println(cnt)
}