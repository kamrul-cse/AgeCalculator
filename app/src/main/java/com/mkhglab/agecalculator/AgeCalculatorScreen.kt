package com.mkhglab.agecalculator

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mkhglab.agecalculator.ui.theme.AgeCalculatorTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.mkhglab.agecalculator.ToolsScreen

enum class AppScreen { Calculator, Tools }

@Composable
fun AgeCalculatorApp() {
    var screen by remember { mutableStateOf(AppScreen.Calculator) }

    AgeCalculatorTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (screen) {
                AppScreen.Calculator -> AgeCalculatorScreen(onOpenTools = { screen = AppScreen.Tools })
                AppScreen.Tools -> ToolsScreen(onBack = { screen = AppScreen.Calculator })
            }
        }
    }
}

@Composable
fun AgeCalculatorScreen(
    modifier: Modifier = Modifier,
    onOpenTools: () -> Unit
) {
    val now = remember { LocalDate.now() }
    var birthDate by remember { mutableStateOf(now.minusYears(20)) }
    var currentDate by remember { mutableStateOf(now) }
    var ageResult by remember { mutableStateOf<AgeResult?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    val clockSize = 240.dp
    val fullDateFormatter = remember { DateTimeFormatter.ofPattern("EEEE,\nd MMM yyyy") }
    val todayFullDate = remember { LocalDate.now().format(fullDateFormatter) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("d MMM yyyy") }
    val invalidRange = remember(birthDate, currentDate) { birthDate.isAfter(currentDate) }

    Scaffold(
        topBar = { AgeCalculatorAppBar(onSettingsClick = onOpenTools) },
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(0.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                ) {
                    Box(
                        modifier = Modifier
                            .size(clockSize)
                            .align(Alignment.Center)
                    ) {
                        AnalogClock(clockSize = clockSize)
                    }
                }

                //Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier.weight(0.35f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = todayFullDate,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }

            DateInputCard(
                label = "Birth Date",
                date = birthDate,
                formatted = birthDate.format(dateFormatter),
                onDateSelected = {
                    birthDate = it
                    ageResult = null
                    error = null
                }
            )

            DateInputCard(
                label = "Current Date",
                date = currentDate,
                formatted = currentDate.format(dateFormatter),
                onDateSelected = {
                    currentDate = it
                    ageResult = null
                    error = null
                }
            )

            Button(
                onClick = {
                    runCatching { calculateAge(birthDate, currentDate) }
                        .onSuccess {
                            ageResult = it
                            error = null
                        }
                        .onFailure { throwable ->
                            error = throwable.message ?: "Please enter valid dates."
                            ageResult = null
                        }
                },
                enabled = !invalidRange,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                    disabledContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(26.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "CALCULATE",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Text(
                text = "Click CALCULATE to show your age",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            if (invalidRange) {
                Text(
                    text = "Birth date must be on or before the current date.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            } else if (error != null) {
                Text(
                    text = error.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

            ageResult?.let { result ->
                ResultCard(result = result)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "© MKHG Lab",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Dhaka, Bangladesh",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AgeCalculatorAppBar(onSettingsClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Age Calculator",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
private fun DateInputCard(
    label: String,
    date: LocalDate,
    formatted: String,
    onDateSelected: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    var showPicker by remember { mutableStateOf(false) }

    if (showPicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                showPicker = false
                onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
            },
            date.year,
            date.monthValue - 1,
            date.dayOfMonth
        ).apply {
            setOnDismissListener { showPicker = false }
        }.show()
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Day / Month / Year",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Button(
                onClick = { showPicker = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = formatted,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ResultCard(result: AgeResult) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Your Age",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${result.years} years, ${result.months} months, ${result.days} days",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun AgeCalculatorScreenPreview() {
    AgeCalculatorApp()
}
