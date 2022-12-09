fun main() {
    val elfs: List<Int> = buildList {
        var sum = 0
        while (true) {
            val s = readln()
            when (s) {
                "" -> {
                    add(sum)
                    sum = 0
                }
                "hehe" -> {
                    add(sum)
                    break
                }
                else -> {
                    sum += s.toInt()
                    println(sum)
                }
            }
        }
    }

    println(elfs.asSequence().sortedDescending().take(3).sum())
}