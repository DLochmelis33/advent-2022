import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UnitedSegmentsTest {
    private val us = UnitedSegments()
    private fun check(vararg segments: IntRange) = assertEquals(segments.toList(), us.segs)

    @BeforeTest
    fun clear() = us.segs.clear()

    @Test
    fun test() {
        us.add(1, 3)
        check(1..3)
        us.add(5, 8)
        check(1..3, 5..8)
        us.add(2, 6)
        check(1..8)

        us.add(15, 20)
        check(1..8, 15..20)
        us.add(12, 18)
        check(1..8, 12..20)
        us.add(-10, 5)
        check(-10..8, 12..20)
        us.add(9, 11)
        check(-10..8, 9..11, 12..20)
        us.add(-100, 100)
        check(-100..100)
    }

    @Test
    fun alsoTest() {
        us.add(1, 2)
        us.add(3, 4)
        us.add(5, 6)
        us.add(7, 8)
        check(1..2, 3..4, 5..6, 7..8)
        us.add(2, 6)
        check(1..6, 7..8)
        us.add(1, 6)
        check(1..6, 7..8)
        us.add(0, 0)
        check(0..0, 1..6, 7..8)
        us.add(0, 1)
        check(0..6, 7..8)
    }

    @Test
    fun anotherTest() {
        us.add(1, 3)
        us.add(5, 7)
        us.add(9, 11)
        us.add(4, 8)
        check(1..3, 4..8, 9..11)
    }
}