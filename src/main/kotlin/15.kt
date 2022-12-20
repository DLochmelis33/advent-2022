import java.io.File
import kotlin.math.abs

class UnitedSegments {

    val segs = mutableListOf<IntRange>()

    fun add(start: Int, end: Int) {
        if (segs.isEmpty() || start > segs.last().last) {
            segs.add(start..end)
            return
        }
        if (end < segs.first().first) {
            segs.add(0, start..end)
            return
        }
        val startOrNextIndex = segs.indexOfFirst { start in it || it.first > start }
            .takeUnless { it == -1 } ?: (segs.size - 1)
        val endOrPrevIndex = segs.indexOfLast { end in it || it.last < end }
            .takeUnless { it == -1 } ?: 0
        val actualStart = if (start in segs[startOrNextIndex]) segs[startOrNextIndex].first else start
        val actualEnd = if (end in segs[endOrPrevIndex]) segs[endOrPrevIndex].last else end
        segs.removeAll(segs.subList(startOrNextIndex, endOrPrevIndex + 1))
        segs.add(startOrNextIndex, actualStart..actualEnd)
    }
}

fun main() {
    val inputRegex = Regex("Sensor at x=(.+), y=(.+): closest beacon is at x=(.+), y=(.+)")
    val yTarget = 2000000
    val us = UnitedSegments()
    val occupied = mutableSetOf<Pair<Int, Int>>()
    File("in.txt").readLines().forEach { line ->
        val (xs, ys, xb, yb) = inputRegex.matchEntire(line)!!.groupValues.drop(1).map { it.toInt() }
        val dist = abs(xs - xb) + abs(ys - yb)
        // xs-xMin + abs(ys-yTarget) = dist
        // xMax-xs + abs(ys-yTarget) = dist
        if (abs(ys - yTarget) <= dist) {
            val xMin = xs + abs(ys - yTarget) - dist
            val xMax = dist - abs(ys - yTarget) + xs
            us.add(xMin, xMax)
        }
        if (yb == yTarget) occupied.add(xb to yb)
        if (ys == yTarget) occupied.add(xs to ys)
    }
    println(us.segs.sumOf { it.last - it.first + 1 } - occupied.size)
}