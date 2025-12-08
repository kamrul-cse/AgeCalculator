package com.mkhglab.agecalculator

import java.time.LocalDate

data class AgeResult(
    val years: Int,
    val months: Int,
    val days: Int
)

/**
 * Calculates the age difference between two dates in years, months, and days.
 * Birth date must be on or before the current/reference date.
 */
fun calculateAge(birthDate: LocalDate, currentDate: LocalDate): AgeResult {
    require(!birthDate.isAfter(currentDate)) {
        "Birth date must be on or before the current date."
    }

    var years = currentDate.year - birthDate.year
    var months = currentDate.monthValue - birthDate.monthValue
    var days = currentDate.dayOfMonth - birthDate.dayOfMonth

    if (days < 0) {
        months -= 1
        val previousMonth = currentDate.minusMonths(1)
        days += previousMonth.lengthOfMonth()
    }

    if (months < 0) {
        years -= 1
        months += 12
    }

    return AgeResult(years, months, days)
}
