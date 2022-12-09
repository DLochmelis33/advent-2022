fun main() {
    var sum = 0
    while (true) {
        val s = readln()
        if (s == "hehe") break
        val (opponent, you) = s.split(" ")
        // rock = 1, paper = 2, scissors = 3
        // rock = A, paper = B, scissors = C
        // X = lose = 0, Y = draw = 3, Z = win = 6
        val points = when (opponent to you) {
            "A" to "X" -> 3
            "A" to "Y" -> 4
            "A" to "Z" -> 8
            "B" to "X" -> 1
            "B" to "Y" -> 5
            "B" to "Z" -> 9
            "C" to "X" -> 2
            "C" to "Y" -> 6
            "C" to "Z" -> 7
            else -> error("wat '$opponent' '$you'")
        }
        sum += points
    }
    println(sum)
}