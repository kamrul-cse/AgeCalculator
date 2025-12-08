package com.mkhglab.agecalculator.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AgeCalculatorColorScheme = lightColorScheme(
    primary = BrandTeal,
    onPrimary = Color.White,
    secondary = BrandTeal,
    onSecondary = Color.White,
    background = RevexOffWhite,      // matches Revex background
    onBackground = RevexBlack,
    surface = RevexOffWhite,
    onSurface = RevexBlack,
    error = BrandTeal,
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
