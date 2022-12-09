import java.util.*

fun main() {
    val stackCount = 9
    val inputHeight = 8
    val stacks = List(stackCount) { Stack<Char>() }
    val boxes = List(inputHeight) { readln().let { line -> (1 until stackCount * 4 step 4).map { i -> line[i] } } }
    for ((j, stack) in stacks.withIndex()) {
        for (i in (0 until inputHeight).reversed()) boxes[i][j].let { if (it != ' ') stack.add(it) }
    }
    readln(); readln()
    val queryRegex = "move (\\d+) from (\\d+) to (\\d+)".toRegex()
    while (true) {
        val s = readln()
        if (s == "hehe") break
        val (count, from, to) = queryRegex.matchEntire(s)!!.groupValues.drop(1).map { it.toInt() - 1 }
        val batch = List(count + 1) { stacks[from].pop() }
        for (item in batch.reversed()) stacks[to].add(item)
    }
    println(stacks.map { it.peek() }.joinToString(separator = ""))
}