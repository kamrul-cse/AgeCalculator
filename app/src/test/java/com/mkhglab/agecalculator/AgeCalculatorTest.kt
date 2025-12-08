package com.mkhglab.agecalculator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.LocalDate

class AgeCalculatorTest {

    @Test
    fun `calculates age across months and days`() {
        val birth = LocalDate.of(1991, 6, 9)
        val current = LocalDate.of(2017, 1, 1)

        val result = calculateAge(birth, current)

        assertEquals(25, result.years)
        assertEquals(6, result.months)
        assertEquals(23, result.days)
    }

    @Test
    fun `handles leap day birthday`() {
        val birth = LocalDate.of(2016, 2, 29)
        val current = LocalDate.of(2017, 2, 28)

        val result = calculateAge(birth, current)

        assertEquals(0, result.years)
        assertEquals(11, result.months)
        assertEquals(30, result.days)
    }

    @Test
    fun `returns zero age for same day`() {
        val birth = LocalDate.of(2024, 12, 5)
        val current = LocalDate.of(2024, 12, 5)

        val result = calculateAge(birth, current)

        assertEquals(0, result.years)
        assertEquals(0, result.months)
        assertEquals(0, result.days)
    }

    @Test
    fun `throws when birth is after current`() {
        val birth = LocalDate.of(2024, 12, 6)
        val current = LocalDate.of(2024, 12, 5)

        assertThrows(IllegalArgumentException::class.java) {
            calculateAge(birth, current)
        }
    }
}
