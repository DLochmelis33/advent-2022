fun main() {
    val s = readln()
    val l = 14
    for (i in l..s.length) if (s.substring(i - l, i).toSet().size == l) {
        println(i)
        break
    }
    println("done")
}