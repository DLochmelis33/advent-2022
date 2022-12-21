import kotlin.test.Test
import kotlin.test.assertEquals

class PermutationsTest {

    @Test
    fun test() {
        assertEquals(
            setOf(listOf(1)), listOf(1).permutations().toSet()
        )
        assertEquals(
            setOf(
                listOf(1, 2),
                listOf(2, 1),
            ),
            listOf(1, 2).permutations().toSet()
        )
        assertEquals(
            setOf(
                listOf(1, 2, 3),
                listOf(1, 3, 2),
                listOf(2, 1, 3),
                listOf(2, 3, 1),
                listOf(3, 1, 2),
                listOf(3, 2, 1),
            ),
            listOf(1, 2, 3).permutations().toSet()
        )
    }

}