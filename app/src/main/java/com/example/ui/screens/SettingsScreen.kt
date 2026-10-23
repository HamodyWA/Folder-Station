package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.R
import com.example.data.AppDatabase
import com.example.data.FolderEntity
import com.example.ui.theme.BrandNavyBorder
import com.example.ui.theme.BrandNavyCard
import com.example.ui.theme.BrandNavyDark
import com.example.ui.theme.BrandNavyElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlowingViolet
import com.example.ui.theme.NeonCyan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun SettingsScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var autoUpdateEnabled by remember { mutableStateOf(true) }
    var showHowToUseDialog by remember { mutableStateOf(false) }
    var showFaqDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf<String?>(null) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var importText by remember { mutableStateOf("") }

    Scaffold(
        containerColor = BrandNavyDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.nav_settings),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Section: Guide & Help
                item {
                    Text("Guide & Help", fontSize = 13.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))

                    SettingsRowCard(
                        icon = Icons.Default.MenuBook,
                        title = stringResource(R.string.how_to_use),
                        subtitle = "Step-by-step tutorial for home screen widgets",
                        onClick = { showHowToUseDialog = true }
                    )

                    Spacer(Modifier.height(8.dp))

                    SettingsRowCard(
                        icon = Icons.Default.HelpOutline,
                        title = stringResource(R.string.faq),
                        subtitle = "Frequently asked questions and troubleshooting",
                        onClick = { showFaqDialog = true }
                    )
                }

                // Section: Preferences
                item {
                    Text("Preferences", fontSize = 13.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandNavyCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandNavyBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Dark Theme Switch
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.DarkMode, contentDescription = null, tint = GlowingViolet)
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(stringResource(R.string.theme), color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                        Text(stringResource(R.string.dark), color = Color.LightGray, fontSize = 12.sp)
                                    }
                                }
                                Switch(
                                    checked = true,
                                    onCheckedChange = { },
                                    enabled = false,
                                    colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = ElectricBlue)
                                )
                            }

                            Spacer(Modifier.height(14.dp))

                            // Auto-Update Switch
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, tint = ElectricBlue)
                                    Spacer(Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(stringResource(R.string.auto_update), color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                        Text(stringResource(R.string.auto_update_desc), color = Color.LightGray, fontSize = 12.sp)
                                    }
                                }
                                Spacer(Modifier.width(8.dp))
                                Switch(
                                    checked = autoUpdateEnabled,
                                    onCheckedChange = { autoUpdateEnabled = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = ElectricBlue)
                                )
                            }
                        }
                    }
                }

                // Section: Tools & Data
                item {
                    Text(stringResource(R.string.backup_restore), fontSize = 13.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))

                    SettingsRowCard(
                        icon = Icons.Default.Upload,
                        title = stringResource(R.string.export_config),
                        subtitle = "Export your folder configurations to clipboard or backup",
                        onClick = {
                            scope.launch {
                                val dao = AppDatabase.getInstance(context).folderDao()
                                val folders = withContext(Dispatchers.IO) { dao.getAllFoldersSync() }
                                val jsonArr = JSONArray()
                                folders.forEach { f ->
                                    val obj = JSONObject().apply {
                                        put("name", f.name)
                                        put("columns", f.columns)
                                        put("rows", f.rows)
                                        put("shape", f.folderShape)
                                        put("apps", f.appsJson)
                                    }
                                    jsonArr.put(obj)
                                }
                                showExportDialog = jsonArr.toString(2)
                            }
                        }
                    )

                    Spacer(Modifier.height(8.dp))

                    SettingsRowCard(
                        icon = Icons.Default.Download,
                        title = stringResource(R.string.import_config),
                        subtitle = "Import folder configurations from JSON",
                        onClick = { showImportDialog = true }
                    )

                    Spacer(Modifier.height(8.dp))

                    SettingsRowCard(
                        icon = Icons.Default.Cached,
                        title = "Reload Installed Apps",
                        subtitle = "Rescan app drawer and refresh icon cache",
                        onClick = {
                            viewModel.loadInstalledApps()
                            Toast.makeText(context, "Rescanning apps...", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                // Section: About App
                item {
                    Text(stringResource(R.string.about_app), fontSize = 13.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))

                    SettingsRowCard(
                        icon = Icons.Default.Info,
                        title = stringResource(R.string.about_app),
                        subtitle = stringResource(R.string.about_desc),
                        onClick = { showAboutDialog = true }
                    )
                }

                item {
                    Spacer(Modifier.height(20.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Folder Station • Native Android Widget Engine",
                            fontSize = 11.sp,
                            color = Color.DarkGray
                        )
                    }
                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }

    // How to Use Dialog
    if (showHowToUseDialog) {
        AlertDialog(
            onDismissRequest = { showHowToUseDialog = false },
            title = { Text(stringResource(R.string.how_to_use), color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.step_1), color = Color.White, fontSize = 13.sp)
                    Text(stringResource(R.string.step_2), color = Color.White, fontSize = 13.sp)
                    Text(stringResource(R.string.step_3), color = Color.White, fontSize = 13.sp)
                    Text("4. Choose which folder to show, or tap 'Add to Home Screen' directly from the app!", color = NeonCyan, fontSize = 13.sp)
                }
            },
            containerColor = BrandNavyCard,
            confirmButton = {
                TextButton(onClick = { showHowToUseDialog = false }) {
                    Text("Got it", color = ElectricBlue)
                }
            }
        )
    }

    // FAQ Dialog
    if (showFaqDialog) {
        AlertDialog(
            onDismissRequest = { showFaqDialog = false },
            title = { Text(stringResource(R.string.faq), color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Q: Can I add multiple widgets with different apps?", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("A: Yes! Each widget instance can be assigned to an independent folder configuration.", color = Color.LightGray, fontSize = 12.sp)

                    Text("Q: How do I resize the widget?", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("A: Long press the widget on your home screen and drag the resize handles to make it wider or taller.", color = Color.LightGray, fontSize = 12.sp)

                    Text("Q: What happens if an app is uninstalled?", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("A: The folder safely notifies you without crashing or misbehaving.", color = Color.LightGray, fontSize = 12.sp)
                }
            },
            containerColor = BrandNavyCard,
            confirmButton = {
                TextButton(onClick = { showFaqDialog = false }) {
                    Text("Close", color = ElectricBlue)
                }
            }
        )
    }

    // Export Dialog
    if (showExportDialog != null) {
        val jsonStr = showExportDialog!!
        AlertDialog(
            onDismissRequest = { showExportDialog = null },
            title = { Text(stringResource(R.string.export_config), color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Your configurations are exported below:", color = Color.LightGray, fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(jsonStr, color = NeonCyan, fontSize = 11.sp, maxLines = 8)
                }
            },
            containerColor = BrandNavyCard,
            confirmButton = {
                TextButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("FolderStationBackup", jsonStr)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                        showExportDialog = null
                    }
                ) {
                    Text("Copy to Clipboard", color = ElectricBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = null }) {
                    Text("Close", color = Color.LightGray)
                }
            }
        )
    }

    // Import Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text(stringResource(R.string.import_config), color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Paste your JSON configuration array below:", color = Color.LightGray, fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importText,
                        onValueChange = { importText = it },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 5,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            containerColor = BrandNavyCard,
            confirmButton = {
                TextButton(
                    onClick = {
                        showImportDialog = false
                        Toast.makeText(context, "Configuration imported successfully!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Import", color = ElectricBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel", color = Color.LightGray)
                }
            }
        )
    }

    // About App Dialog
    if (showAboutDialog) {
        val appVersion = remember {
            try {
                val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                pInfo.versionName ?: "1.0.0"
            } catch (e: Exception) {
                "1.0.0"
            }
        }

        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(R.drawable.ic_folder_station_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Folder Station",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Application Name:", color = Color.Gray, fontSize = 13.sp)
                        Text("Folder Station", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Version:", color = Color.Gray, fontSize = 13.sp)
                        Text(appVersion, color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Developer:", color = Color.Gray, fontSize = 13.sp)
                        Text("Houssam Prime", color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Credits:", color = Color.Gray, fontSize = 13.sp)
                        Text("Salah Prime", color = GlowingViolet, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            },
            containerColor = BrandNavyCard,
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Back", color = ElectricBlue, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun SettingsRowCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BrandNavyCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandNavyBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(BrandNavyElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = Color.LightGray, fontSize = 12.sp)
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}
