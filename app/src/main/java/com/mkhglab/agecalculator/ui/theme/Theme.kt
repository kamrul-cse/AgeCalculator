package com.mkhglab.agecalculator.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AgeCalculatorColorScheme = darkColorScheme(
    primary = HeaderBlue,
    onPrimary = Color.White,
    secondary = AccentYellow,
    onSecondary = Color.Black,
    background = ForestGreen,
    onBackground = TextLight,
    surface = ForestGreenDark,
    onSurface = SubtleWhite,
    error = ActionRed,
    onError = Color.White
)

@Composable
fun AgeCalculatorTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AgeCalculatorColorScheme,
        typography = Typography,
        content = content
    )
}
