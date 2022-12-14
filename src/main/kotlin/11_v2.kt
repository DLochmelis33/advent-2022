// to see solution of part 1, see previous commit 7f3f9e00ccded640e9f82267798274eb0a126145

typealias MonkeyItem = Long
typealias Midx = Int

var gcd = 1L

class Monkey(
    items: List<MonkeyItem>,
    private val itemOp: (MonkeyItem) -> MonkeyItem,
    val testModulo: Long,
    private val onTrue: Midx,
    private val onFalse: Midx,
    private val monkeys: List<Monkey>
) {
    private val items = items.toMutableList()
    var cnt = 0L
        private set

    fun act() {
        for (item in items) {
            val newItem = itemOp(item) % gcd
            (if (newItem % testModulo == 0L) monkeys[onTrue] else monkeys[onFalse]).items.add(newItem)
            cnt++
        }
        items.clear()
    }
}

val monkeys = mutableListOf<Monkey>()

fun readMonkey() {
    readln() // "monkey i"
    val startingItems = readln().removePrefix("  Starting items: ").split(", ").map { it.toLong() }
    val itemOp: (MonkeyItem) -> MonkeyItem = readln().removePrefix("  Operation: new = ").let { expr ->
        if (expr == "old * old") {
            { it * it }
        } else "old \\* (\\d+)".toRegex().matchEntire(expr)?.let { mr ->
            { it * mr.groupValues[1].toLong() }
        } ?: "old \\+ (\\d+)".toRegex().matchEntire(expr)?.let { mr ->
            { it + mr.groupValues[1].toLong() }
        } ?: error("unknown operation '$expr'")
    }
    val testModulo = readln().removePrefix("  Test: divisible by ").toLong()
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
    gcd = monkeys.map { it.testModulo }.reduce(Long::times)
    repeat(10000) {
        for (m in monkeys) m.act()
    }
    println(monkeys.map { it.cnt })
    println(monkeys.sortedByDescending { it.cnt }.map { it.cnt }.take(2).reduce(Long::times))
}
