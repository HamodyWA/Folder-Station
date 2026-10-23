package com.example.ui.screens

import android.graphics.Bitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.R
import com.example.data.FolderAppItem
import com.example.data.FolderEntity
import com.example.ui.theme.BrandNavyBorder
import com.example.ui.theme.BrandNavyCard
import com.example.ui.theme.BrandNavyDark
import com.example.ui.theme.BrandNavyElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlowingViolet
import com.example.ui.theme.NeonCyan

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FolderEditorScreen(
    folder: FolderEntity,
    viewModel: MainViewModel,
    onSave: () -> Unit,
    onPin: (FolderEntity) -> Unit,
    onBack: () -> Unit
) {
    var showAppPicker by remember { mutableStateOf(false) }
    var selectedSection by remember { mutableIntStateOf(0) }
    val sections = listOf(
        stringResource(R.string.select_apps),
        stringResource(R.string.grid_layout),
        stringResource(R.string.appearance),
        "Text & Labels"
    )

    val currentApps = folder.getApps()
    val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = BrandNavyDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (folder.name.isNotBlank()) folder.name else stringResource(R.string.edit_folder),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { onPin(folder) }) {
                        Icon(Icons.Default.Home, contentDescription = stringResource(R.string.add_to_home_screen), tint = NeonCyan)
                    }
                    IconButton(onClick = onSave) {
                        Icon(Icons.Default.Save, contentDescription = stringResource(R.string.save_folder), tint = ElectricBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandNavyDark)
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrandNavyCard)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { onPin(folder) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
                ) {
                    Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.add_to_home_screen), maxLines = 1, fontSize = 13.sp)
                }

                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.save_folder), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Live Preview Card at top
            FolderLivePreview(
                folder = folder,
                apps = currentApps,
                getIcon = { pkg -> viewModel.appScanner.getAppIcon(pkg, 96) }
            )

            // Section Tabs
            TabRow(
                selectedTabIndex = selectedSection,
                containerColor = BrandNavyCard,
                contentColor = ElectricBlue,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedSection]),
                        color = ElectricBlue
                    )
                }
            ) {
                sections.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedSection == index,
                        onClick = { selectedSection = index },
                        text = {
                            Text(
                                title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedSection == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedSection == index) ElectricBlue else Color.LightGray
                            )
                        }
                    )
                }
            }

            // Controls Body
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedSection) {
                    0 -> { // Apps Section
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${currentApps.size} apps included",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Button(
                                    onClick = { showAppPicker = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(stringResource(R.string.select_apps))
                                }
                            }
                        }

                        if (currentApps.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(BrandNavyCard)
                                        .clickable { showAppPicker = true }
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(36.dp))
                                        Spacer(Modifier.height(8.dp))
                                        Text("Tap to add applications to this folder", color = Color.LightGray, fontSize = 14.sp)
                                    }
                                }
                            }
                        } else {
                            items(currentApps.size) { index ->
                                val appItem = currentApps[index]
                                val iconBitmap = remember(appItem.packageName) {
                                    viewModel.appScanner.getAppIcon(appItem.packageName, 96)
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(BrandNavyCard)
                                        .border(1.dp, BrandNavyBorder, RoundedCornerShape(12.dp))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (iconBitmap != null) {
                                        Image(
                                            bitmap = iconBitmap.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                    } else {
                                        Image(
                                            painter = painterResource(R.drawable.ic_folder_station_logo),
                                            contentDescription = null,
                                            modifier = Modifier.size(38.dp)
                                        )
                                    }

                                    Spacer(Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = appItem.getDisplayName(),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = appItem.packageName,
                                            fontSize = 11.sp,
                                            color = Color.Gray,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    // Move Up
                                    IconButton(
                                        onClick = { viewModel.moveAppInEditingFolder(index, index - 1) },
                                        enabled = index > 0
                                    ) {
                                        Icon(Icons.Default.ArrowUpward, contentDescription = "Move Up", tint = if (index > 0) Color.White else Color.DarkGray)
                                    }

                                    // Move Down
                                    IconButton(
                                        onClick = { viewModel.moveAppInEditingFolder(index, index + 1) },
                                        enabled = index < currentApps.size - 1
                                    ) {
                                        Icon(Icons.Default.ArrowDownward, contentDescription = "Move Down", tint = if (index < currentApps.size - 1) Color.White else Color.DarkGray)
                                    }

                                    // Delete
                                    IconButton(
                                        onClick = { viewModel.removeAppFromEditingFolder(appItem.packageName) }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color(0xFFFF5252))
                                    }
                                }
                            }
                        }
                    }

                    1 -> { // Grid & Layout Section
                        item {
                            Text("Grid Presets", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(Modifier.height(8.dp))
                            val gridPresets = listOf(
                                Pair(1, 1), Pair(2, 2), Pair(2, 3), Pair(3, 2),
                                Pair(3, 3), Pair(4, 2), Pair(4, 3), Pair(4, 4), Pair(1, 5)
                            )
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                gridPresets.forEach { (cols, rows) ->
                                    val isSelected = folder.columns == cols && folder.rows == rows
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            viewModel.updateEditingFolder(folder.copy(columns = cols, rows = rows))
                                        },
                                        label = { Text("${cols}×${rows}") },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = ElectricBlue,
                                            selectedLabelColor = Color.White,
                                            containerColor = BrandNavyCard,
                                            labelColor = Color.LightGray
                                        )
                                    )
                                }
                            }
                        }

                        item {
                            Text(
                                text = "${stringResource(R.string.columns)}: ${folder.columns}",
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Slider(
                                value = folder.columns.toFloat(),
                                onValueChange = { viewModel.updateEditingFolder(folder.copy(columns = it.toInt())) },
                                valueRange = 1f..6f,
                                steps = 4,
                                colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = ElectricBlue)
                            )
                        }

                        item {
                            Text(
                                text = "${stringResource(R.string.rows)}: ${folder.rows}",
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Slider(
                                value = folder.rows.toFloat(),
                                onValueChange = { viewModel.updateEditingFolder(folder.copy(rows = it.toInt())) },
                                valueRange = 1f..6f,
                                steps = 4,
                                colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = ElectricBlue)
                            )
                        }

                        item {
                            Text(
                                text = "${stringResource(R.string.icon_size)}: ${folder.iconSizeDp} dp",
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Slider(
                                value = folder.iconSizeDp.toFloat(),
                                onValueChange = { viewModel.updateEditingFolder(folder.copy(iconSizeDp = it.toInt())) },
                                valueRange = 32f..64f,
                                colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = ElectricBlue)
                            )
                        }

                        item {
                            Text(
                                text = "${stringResource(R.string.spacing)}: ${folder.spacingDp} dp",
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Slider(
                                value = folder.spacingDp.toFloat(),
                                onValueChange = { viewModel.updateEditingFolder(folder.copy(spacingDp = it.toInt())) },
                                valueRange = 2f..20f,
                                colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = ElectricBlue)
                            )
                        }
                    }

                    2 -> { // Appearance Section
                        item {
                            Text("Folder Shape", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(Modifier.height(8.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FolderEntity.ShapeType.values().forEach { shape ->
                                    val isSelected = folder.folderShape == shape.name
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            viewModel.updateEditingFolder(
                                                folder.copy(
                                                    folderShape = shape.name,
                                                    cornerRadiusDp = if (shape == FolderEntity.ShapeType.CIRCLE) 99 else folder.cornerRadiusDp
                                                )
                                            )
                                        },
                                        label = { Text(shape.name.replace("_", " ")) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = GlowingViolet,
                                            selectedLabelColor = Color.White,
                                            containerColor = BrandNavyCard,
                                            labelColor = Color.LightGray
                                        )
                                    )
                                }
                            }
                        }

                        item {
                            Text(stringResource(R.string.background_color), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White)
                            Spacer(Modifier.height(8.dp))
                            val bgColors = listOf(
                                "#080B1C", "#11162E", "#151B33", "#0E1535",
                                "#1A103C", "#0D2538", "#1E1E2E", "#FFFFFF"
                            )
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(bgColors) { hex ->
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color(android.graphics.Color.parseColor(hex)))
                                            .border(
                                                2.dp,
                                                if (folder.backgroundColorHex.equals(hex, ignoreCase = true)) NeonCyan else Color.Transparent,
                                                CircleShape
                                            )
                                            .clickable { viewModel.updateEditingFolder(folder.copy(backgroundColorHex = hex)) }
                                    )
                                }
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Gradient Background", color = Color.White, fontSize = 14.sp)
                                Switch(
                                    checked = folder.isGradient,
                                    onCheckedChange = { viewModel.updateEditingFolder(folder.copy(isGradient = it)) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = ElectricBlue)
                                )
                            }
                        }

                        item {
                            Text(
                                text = "${stringResource(R.string.opacity)}: ${(folder.backgroundOpacity * 100).toInt()}%",
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Slider(
                                value = folder.backgroundOpacity,
                                onValueChange = { viewModel.updateEditingFolder(folder.copy(backgroundOpacity = it)) },
                                valueRange = 0.1f..1.0f,
                                colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = ElectricBlue)
                            )
                        }

                        item {
                            Text(
                                text = "${stringResource(R.string.corner_radius)}: ${folder.cornerRadiusDp} dp",
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Slider(
                                value = folder.cornerRadiusDp.toFloat(),
                                onValueChange = { viewModel.updateEditingFolder(folder.copy(cornerRadiusDp = it.toInt())) },
                                valueRange = 0f..40f,
                                colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = ElectricBlue)
                            )
                        }

                        item {
                            Text(stringResource(R.string.border_color), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White)
                            Spacer(Modifier.height(8.dp))
                            val borderColors = listOf(
                                "#388BFD", "#00E5FF", "#8B5CF6", "#F97316",
                                "#10B981", "#FFFFFF", "#26325C"
                            )
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(borderColors) { hex ->
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(android.graphics.Color.parseColor(hex)))
                                            .border(
                                                2.dp,
                                                if (folder.borderColorHex.equals(hex, ignoreCase = true)) Color.White else Color.Transparent,
                                                CircleShape
                                            )
                                            .clickable { viewModel.updateEditingFolder(folder.copy(borderColorHex = hex)) }
                                    )
                                }
                            }
                        }

                        item {
                            Text(
                                text = "${stringResource(R.string.border_thickness)}: ${folder.borderThicknessDp} dp",
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Slider(
                                value = folder.borderThicknessDp.toFloat(),
                                onValueChange = { viewModel.updateEditingFolder(folder.copy(borderThicknessDp = it.toInt())) },
                                valueRange = 0f..6f,
                                steps = 5,
                                colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = ElectricBlue)
                            )
                        }
                    }

                    3 -> { // Text & Labels Section
                        item {
                            OutlinedTextField(
                                value = folder.name,
                                onValueChange = { viewModel.updateEditingFolder(folder.copy(name = it)) },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text(stringResource(R.string.folder_name)) },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricBlue,
                                    unfocusedBorderColor = BrandNavyBorder,
                                    focusedContainerColor = BrandNavyCard,
                                    unfocusedContainerColor = BrandNavyCard,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(stringResource(R.string.show_title), color = Color.White, fontSize = 14.sp)
                                Switch(
                                    checked = folder.showTitle,
                                    onCheckedChange = { viewModel.updateEditingFolder(folder.copy(showTitle = it)) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = ElectricBlue)
                                )
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(stringResource(R.string.show_labels), color = Color.White, fontSize = 14.sp)
                                Switch(
                                    checked = folder.showAppLabels,
                                    onCheckedChange = { viewModel.updateEditingFolder(folder.copy(showAppLabels = it)) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = ElectricBlue)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAppPicker) {
        val selectedPackages = currentApps.map { it.packageName }.toSet()
        AppPickerSheet(
            installedApps = installedApps,
            selectedPackageNames = selectedPackages,
            onAppToggled = { app ->
                if (selectedPackages.contains(app.packageName)) {
                    viewModel.removeAppFromEditingFolder(app.packageName)
                } else {
                    viewModel.addAppToEditingFolder(app)
                }
            },
            onDismiss = { showAppPicker = false }
        )
    }
}

@Composable
fun FolderLivePreview(
    folder: FolderEntity,
    apps: List<FolderAppItem>,
    getIcon: (String) -> Bitmap?
) {
    val bgColor = try {
        Color(android.graphics.Color.parseColor(folder.backgroundColorHex)).copy(alpha = folder.backgroundOpacity)
    } catch (e: Exception) {
        Color(0xFF11162E)
    }

    val endColor = if (folder.isGradient) {
        try {
            Color(android.graphics.Color.parseColor(folder.gradientEndColorHex)).copy(alpha = folder.backgroundOpacity)
        } catch (e: Exception) {
            bgColor
        }
    } else bgColor

    val borderColor = try {
        Color(android.graphics.Color.parseColor(folder.borderColorHex))
    } catch (e: Exception) {
        Color(0xFF388BFD)
    }

    val radius = if (folder.folderShape == FolderEntity.ShapeType.CIRCLE.name) {
        99.dp
    } else {
        folder.cornerRadiusDp.dp
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrandNavyElevated)
            .padding(vertical = 12.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "LIVE WIDGET PREVIEW",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NeonCyan,
            letterSpacing = 1.sp
        )

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(RoundedCornerShape(radius))
                .background(Brush.verticalGradient(listOf(bgColor, endColor)))
                .border(folder.borderThicknessDp.dp, borderColor, RoundedCornerShape(radius))
                .padding(10.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (folder.showTitle) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_folder_station_logo),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = folder.name,
                            fontSize = folder.titleSizeSp.sp,
                            fontWeight = FontWeight.Bold,
                            color = try { Color(android.graphics.Color.parseColor(folder.titleColorHex)) } catch (e: Exception) { Color.White },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Grid Display
                val maxVisible = folder.columns * folder.rows
                val displayApps = apps.take(maxVisible)

                if (displayApps.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Empty Folder\n(Add apps below)",
                            fontSize = 12.sp,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    val rows = displayApps.chunked(folder.columns)
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rows.forEach { rowApps ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                rowApps.forEach { appItem ->
                                    val bmp = getIcon(appItem.packageName)
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.width(50.dp)
                                    ) {
                                        if (bmp != null) {
                                            Image(
                                                bitmap = bmp.asImageBitmap(),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                            )
                                        } else {
                                            Image(
                                                painter = painterResource(R.drawable.ic_folder_station_logo),
                                                contentDescription = null,
                                                modifier = Modifier.size(34.dp)
                                            )
                                        }

                                        if (folder.showAppLabels) {
                                            Text(
                                                text = appItem.getDisplayName(),
                                                fontSize = 9.sp,
                                                color = try { Color(android.graphics.Color.parseColor(folder.appLabelColorHex)) } catch (e: Exception) { Color.White },
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
