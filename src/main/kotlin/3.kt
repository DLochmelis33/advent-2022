fun main() {
    var sum = 0
    fun priority(c: Char) = if (c > 'a') c - 'a' + 1 else c - 'A' + 27
    while (true) {
        val strs = List(3) { readln() }
        if (strs.contains("hehe")) break
        val common = strs.map { it.toSet() }.reduce(Set<Char>::intersect)
        for (c in common) sum += priority(c)
    }
    println(sum)
}