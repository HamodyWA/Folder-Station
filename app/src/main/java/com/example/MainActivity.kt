package com.example

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.FolderEditorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PresetsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WidgetsScreen
import com.example.ui.theme.BrandNavyCard
import com.example.ui.theme.BrandNavyDark
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIntentExtras(intent)

        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = isDarkTheme) {
                MainAppContent(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntentExtras(intent)
    }

    private fun handleIntentExtras(intent: Intent?) {
        if (intent == null) return

        val folderId = intent.getStringExtra("folder_id")
        val appWidgetId = intent.getIntExtra("app_widget_id", -1)

        if (!folderId.isNullOrBlank()) {
            viewModel.openEditFolderById(folderId, if (appWidgetId != -1) appWidgetId else null)
        }

        val createWidgetId = intent.getIntExtra("create_for_widget_id", -1)
        if (createWidgetId != -1) {
            viewModel.openCreateFolder()
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val isEditing by viewModel.isEditing.collectAsStateWithLifecycle()
    val editingFolder by viewModel.editingFolder.collectAsStateWithLifecycle()
    val folders by viewModel.folders.collectAsStateWithLifecycle()
    val toastMessage by viewModel.messageToast.collectAsStateWithLifecycle()

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    BackHandler(enabled = isEditing || currentTab != NavigationTab.HOME) {
        if (isEditing) {
            viewModel.closeEditor()
        } else {
            viewModel.setTab(NavigationTab.HOME)
        }
    }

    if (isEditing && editingFolder != null) {
        FolderEditorScreen(
            folder = editingFolder!!,
            viewModel = viewModel,
            onSave = { viewModel.saveCurrentFolder() },
            onPin = { viewModel.pinFolderToHome(it) },
            onBack = { viewModel.closeEditor() }
        )
    } else {
        Scaffold(
            containerColor = BrandNavyDark,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                NavigationBar(
                    containerColor = BrandNavyCard,
                    contentColor = Color.White,
                    tonalElevation = 8.dp,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    val navItems = listOf(
                        Triple(NavigationTab.HOME, "Home", Icons.Default.Home),
                        Triple(NavigationTab.TEMPLATES, "Templates", Icons.Default.GridView),
                        Triple(NavigationTab.WIDGETS, "Widgets", Icons.Default.Widgets),
                        Triple(NavigationTab.SETTINGS, "Settings", Icons.Default.Settings)
                    )

                    navItems.forEach { (tab, title, icon) ->
                        val selected = currentTab == tab
                        NavigationBarItem(
                            selected = selected,
                            onClick = { viewModel.setTab(tab) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = title,
                                    tint = if (selected) NeonCyan else Color.LightGray
                                )
                            },
                            label = {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selected) NeonCyan else Color.LightGray
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = ElectricBlue.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier.testTag("nav_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (currentTab) {
                    NavigationTab.HOME -> {
                        HomeScreen(
                            folders = folders,
                            viewModel = viewModel,
                            onCreateFolder = { viewModel.openCreateFolder() },
                            onEditFolder = { viewModel.openEditFolder(it) }
                        )
                    }
                    NavigationTab.TEMPLATES -> {
                        PresetsScreen(
                            onUsePreset = { preset ->
                                viewModel.openCreateFolder(preset)
                            }
                        )
                    }
                    NavigationTab.WIDGETS -> {
                        WidgetsScreen(
                            folders = folders,
                            viewModel = viewModel,
                            onPinWidget = { folder ->
                                viewModel.pinFolderToHome(folder)
                            }
                        )
                    }
                    NavigationTab.SETTINGS -> {
                        SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
