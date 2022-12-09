import kotlin.math.max

fun main() {
    val trees = buildList {
        while (true) {
            val s = readln()
            if (s == "hehe") break
            add(s.toList().map { it - '0' })
        }
    }
    val width = trees[0].size
    val height = trees.size

    val scores = buildList {
        for (i in 0 until height) {
            for (j in 0 until width) {
                val curHeight = trees[i][j]
                val score = listOf(1 to 0, -1 to 0, 0 to 1, 0 to -1).map { (isDirI, isDirJ) ->
                    check@ for (k in 1..max(width, height)) {
                        val curI = i + isDirI * k
                        val curJ = j + isDirJ * k
                        if (curI !in 0 until height || curJ !in 0 until width) return@map k - 1
                        if (trees[curI][curJ] >= curHeight) return@map k
                    }
                    error("unreachable")
                }.reduce(Int::times)
                add(score)
            }
        }
    }
    println(scores.max())
}