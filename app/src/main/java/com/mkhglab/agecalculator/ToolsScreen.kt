package com.mkhglab.agecalculator

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val versionText = remember { getVersionText(context) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tools",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            /*
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    text = "Ad-free and built to be simple. Share it with friends and tell us how to improve.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }

            SectionLabel(text = "General")
            */

            ToolsItem(
                icon = Icons.Default.Share,
                title = "Share the app",
                subtitle = "Let friends know about Age Calculator"
            ) { shareApp(context) }

            Divider(modifier = Modifier.padding(vertical = 0.dp))

            ToolsItem(
                icon = Icons.Default.Star,
                title = "Rate & review",
                subtitle = "Open Play Store to leave feedback"
            ) { openPlayStore(context) }

            Divider(modifier = Modifier.padding(vertical = 0.dp))

            ToolsItem(
                icon = Icons.Default.ArrowForward,
                title = "Check for updates",
                subtitle = "See if a newer version is available"
            ) { checkForUpdates(context) }

            Divider(modifier = Modifier.padding(vertical = 0.dp))

            ToolsItem(
                icon = Icons.Default.ArrowForward,
                title = "More apps",
                subtitle = "Explore other MKHG Lab apps"
            ) { showMoreApps(context) }

            Divider(modifier = Modifier.padding(vertical = 0.dp))

            ToolsItem(
                icon = Icons.Default.Email,
                title = "Contact us",
                subtitle = "Send feedback or suggestions"
            ) { contactUs(context) }

            Spacer(modifier = Modifier.padding(8.dp))

            Text(
                text = versionText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToolsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.size(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

private fun shareApp(context: android.content.Context) {
    val playStoreUrl = "https://play.google.com/store/apps/details?id=${context.packageName}"
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(
            Intent.EXTRA_TEXT,
            "Try Age Calculator for quick age calculations.\n\nGet it on Play Store: $playStoreUrl"
        )
    }
    context.launchIntent(Intent.createChooser(shareIntent, "Share via"))
}

private fun showMoreApps(context: android.content.Context) {
    try {
        context.launchIntent(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://search?q=pub:MKHG+Lab")
            )
        )
    } catch (anfe: ActivityNotFoundException) {
        context.launchIntent(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/developer?id=MKHG+Lab")
            )
        )
    }
}

private fun contactUs(context: android.content.Context) {
    val email = "mkhg.lab@gmail.com"
    val subject = "Feedback: Age Calculator"
    val mailUri = Uri.parse("mailto:${Uri.encode(email)}?subject=${Uri.encode(subject)}")
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = mailUri
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        putExtra(Intent.EXTRA_SUBJECT, subject)
    }

    try {
        context.launchIntent(Intent.createChooser(intent, "Choose email client"))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No email client found", Toast.LENGTH_LONG).show()
    }
}

private fun openPlayStore(context: android.content.Context) {
    val packageName = context.packageName

    val marketIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("market://details?id=$packageName")
    ).apply { setPackage("com.android.vending") }

    try {
        context.launchIntent(marketIntent)
    } catch (e: ActivityNotFoundException) {
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        )
        context.launchIntent(webIntent)
    }
}

private fun checkForUpdates(context: android.content.Context) {
    val packageName = context.packageName

    val marketIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("market://details?id=$packageName")
    ).apply {
        setPackage("com.android.vending")
    }

    try {
        context.launchIntent(marketIntent)
    } catch (e: ActivityNotFoundException) {
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        )
        context.launchIntent(webIntent)
    }
}

private fun android.content.Context.launchIntent(intent: Intent) {
    val safeIntent = if (this is android.app.Activity) intent else Intent(intent).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(safeIntent)
}

private fun getVersionText(context: android.content.Context): String {
    val versionName = runCatching {
        val packageManager = context.packageManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(
                context.packageName,
                android.content.pm.PackageManager.PackageInfoFlags.of(0)
            ).versionName
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(context.packageName, 0).versionName
        }
    }.getOrNull() ?: "Unknown"
    return "Version: $versionName"
}
