import java.io.File

private data class Item(val value: Int, val next: Item?) {
    override fun toString() = value.toString()
}

fun main() {
    val input = File("in.txt").readLines().map { it.toInt() }
    val items = input.foldRight(mutableListOf<Item>()) { v, acc ->
        acc.apply { add(0, Item(v, acc.firstOrNull())) }
    }
    val initItems = items.toList()

    for (item in initItems) {
        val i = items.indexOf(item)
        val j = item.value.let {
            val wrap = items.size - 1
            val j = i + it
            (if (j > 0) j else j - (j.floorDiv(wrap)) * wrap) % wrap
        }
        items.add(j, items.removeAt(i))
    }

    val zi = items.indexOfFirst { it.value == 0 }
    fun getIth(i: Int) = items[(zi + i) % items.size]

    val result = listOf(1000, 2000, 3000).sumOf { getIth(it).value }
    println(result)
}
