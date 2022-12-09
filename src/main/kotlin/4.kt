fun main() {
    var cnt = 0
    while (true) {
        val s = readln()
        if (s == "hehe") break
        val (a, b, c, d) = s.split(',').flatMap { rangeStr ->
            rangeStr.split('-').map { it.toInt() }
        }
        if (c in a..b || d in a..b || a in c..d || b in c..d) cnt++
    }
    println(cnt)
}