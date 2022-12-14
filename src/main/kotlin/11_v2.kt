typealias MonkeyItem = Int
typealias Midx = Int

class Monkey(
    items: List<Int>,
    private val itemOp: (MonkeyItem) -> MonkeyItem,
    private val testModulo: Int,
    private val onTrue: Midx,
    private val onFalse: Midx,
    val monkeys: List<Monkey>
) {
    private val items = items.toMutableList()
    var cnt = 0
        private set

    fun act() {
        for (item in items) {
            val newItem = itemOp(item) / 3
            (if (newItem % testModulo == 0) monkeys[onTrue] else monkeys[onFalse]).items.add(newItem)
            cnt++
        }
        items.clear()
    }
}

val monkeys = mutableListOf<Monkey>()

fun readMonkey() {
    readln() // "monkey i"
    val startingItems = readln().removePrefix("  Starting items: ").split(", ").map { it.toInt() }
    val itemOp: (Int) -> Int = readln().removePrefix("  Operation: new = ").let { expr ->
        if (expr == "old * old") {
            { it * it }
        } else "old \\* (\\d+)".toRegex().matchEntire(expr)?.let { mr ->
            { it * mr.groupValues[1].toInt() }
        } ?: "old \\+ (\\d+)".toRegex().matchEntire(expr)?.let { mr ->
            { it + mr.groupValues[1].toInt() }
        } ?: error("unknown operation '$expr'")
    }
    val testModulo = readln().removePrefix("  Test: divisible by ").toInt()
    val onTrue = readln().removePrefix("    If true: throw to monkey ").toInt()
    val onFalse = readln().removePrefix("    If false: throw to monkey ").toInt()

    val monkey = Monkey(startingItems, itemOp, testModulo, onTrue, onFalse, monkeys)
    monkeys.add(monkey)
}

fun main() {
    repeat(8) {
        readMonkey()
        readln()
    }
    repeat(20) {
        for (m in monkeys) m.act()
    }
    println(monkeys.map { it.cnt })
    println(monkeys.sortedByDescending { it.cnt }.map { it.cnt }.take(2).reduce(Int::times))
}
