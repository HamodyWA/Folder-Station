package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pentagon
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderEditorScreen(
    folder: FolderEntity,
    viewModel: MainViewModel,
    onSave: () -> Unit,
    onPin: (FolderEntity) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showAppPicker by remember { mutableStateOf(false) }
    var activeTab by remember { mutableIntStateOf(1) } // 0: Apps, 1: Shape, 2: Grid, 3: Size, 4: Colors, 5: Frame
    var hexInput by remember(folder.backgroundColorHex) { mutableStateOf(folder.backgroundColorHex) }

    val currentApps = folder.getApps()
    val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()

    // Photo picker for custom widget decorative frame
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val targetFile = File(context.filesDir, "frame_${folder.id}.png")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    targetFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                viewModel.updateEditingFolder(folder.copy(frameImagePath = targetFile.absolutePath))
                Toast.makeText(context, "Frame image loaded!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        containerColor = BrandNavyDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.edit_folder),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    Button(
                        onClick = onSave,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 18.dp, vertical = 6.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFF388BFD), Color(0xFF8B5CF6))),
                                RoundedCornerShape(20.dp)
                            )
                            .testTag("save_widget_btn")
                    ) {
                        Text(stringResource(R.string.save_folder), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandNavyDark)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Folder Name Input Card
                item {
                    Text(
                        text = stringResource(R.string.folder_name),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(Modifier.height(6.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandNavyCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandNavyBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = folder.name,
                                onValueChange = { newName ->
                                    if (newName.length <= 25) {
                                        viewModel.updateEditingFolder(folder.copy(name = newName))
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("folder_name_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    cursorColor = NeonCyan
                                ),
                                placeholder = {
                                    Text(stringResource(R.string.folder_name_hint), color = Color.Gray)
                                },
                                singleLine = true
                            )
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Name",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "${folder.name.length}/25",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // 2. Category Tabs Row
                item {
                    val tabs = listOf(
                        Triple(0, "Apps", Icons.Default.GridView),
                        Triple(1, "Shape", Icons.Default.ViewInAr),
                        Triple(2, "Grid", Icons.Default.Pentagon),
                        Triple(3, "Size", Icons.Default.FormatSize),
                        Triple(4, "Colors", Icons.Default.Palette),
                        Triple(5, "Frame", Icons.Default.Image)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tabs.forEach { (index, title, icon) ->
                            val isSelected = activeTab == index
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSelected) ElectricBlue else BrandNavyCard)
                                    .border(1.dp, if (isSelected) NeonCyan else BrandNavyBorder, RoundedCornerShape(14.dp))
                                    .clickable { activeTab = index }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        icon,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else Color.LightGray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = title,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else Color.LightGray
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Tab Contents
                when (activeTab) {
                    0 -> { // Apps Tab
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${currentApps.size} Applications",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Button(
                                    onClick = { showAppPicker = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Add Apps", fontSize = 12.sp)
                                }
                            }
                        }

                        if (currentApps.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No apps in this folder yet. Tap 'Add Apps' to select.", color = Color.Gray, fontSize = 13.sp)
                                }
                            }
                        } else {
                            items(currentApps.size) { index ->
                                val appItem = currentApps[index]
                                val iconBitmap = viewModel.appScanner.getAppIcon(appItem.packageName, 72)

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = BrandNavyCard),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandNavyBorder)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (iconBitmap != null) {
                                            Image(
                                                bitmap = iconBitmap.asImageBitmap(),
                                                contentDescription = appItem.getDisplayName(),
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(ElectricBlue.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Default.SportsEsports, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                                            }
                                        }

                                        Spacer(Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(appItem.getDisplayName(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text(appItem.packageName, color = Color.Gray, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }

                                        IconButton(
                                            onClick = { if (index > 0) viewModel.moveAppInEditingFolder(index, index - 1) },
                                            enabled = index > 0,
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(Icons.Default.ArrowUpward, contentDescription = "Move Up", tint = if (index > 0) Color.White else Color.DarkGray)
                                        }

                                        IconButton(
                                            onClick = { if (index < currentApps.size - 1) viewModel.moveAppInEditingFolder(index, index + 1) },
                                            enabled = index < currentApps.size - 1,
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(Icons.Default.ArrowDownward, contentDescription = "Move Down", tint = if (index < currentApps.size - 1) Color.White else Color.DarkGray)
                                        }

                                        IconButton(
                                            onClick = { viewModel.removeAppFromEditingFolder(appItem.packageName) },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color(0xFFFF5252))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> { // Shape Tab (9 Shapes matching reference UI)
                        item {
                            Text("Choose Shape", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(Modifier.height(8.dp))

                            val shapesList = listOf(
                                Triple(FolderEntity.ShapeType.ROUNDED_RECT, "Rounded Rect", listOf(Color(0xFF388BFD), Color(0xFF00E5FF))),
                                Triple(FolderEntity.ShapeType.SQUARE, "Square", listOf(Color(0xFF8B5CF6), Color(0xFFEC4899))),
                                Triple(FolderEntity.ShapeType.CIRCLE, "Circle", listOf(Color(0xFF10B981), Color(0xFF00E5FF))),
                                Triple(FolderEntity.ShapeType.LARGE_ROUNDED, "Large Rounded", listOf(Color(0xFFF59E0B), Color(0xFFEF4444))),
                                Triple(FolderEntity.ShapeType.MATERIAL, "Material", listOf(Color(0xFF0EA5E9), Color(0xFF388BFD))),
                                Triple(FolderEntity.ShapeType.MINIMAL, "Minimal", listOf(Color(0xFFFB7185), Color(0xFFF43F5E))),
                                Triple(FolderEntity.ShapeType.LIST, "List", listOf(Color(0xFF6366F1), Color(0xFF3B82F6))),
                                Triple(FolderEntity.ShapeType.LARGE_ICON, "Large Icon", listOf(Color(0xFFF97316), Color(0xFFFBBF24))),
                                Triple(FolderEntity.ShapeType.BORDER, "Border", listOf(Color(0xFFEC4899), Color(0xFF8B5CF6)))
                            )

                            val rows = shapesList.chunked(3)
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                rows.forEach { rowShapes ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        rowShapes.forEach { (shapeType, label, colors) ->
                                            val isSelected = folder.folderShape == shapeType.name
                                            ShapeOptionCard(
                                                label = label,
                                                gradient = colors,
                                                shapeType = shapeType,
                                                isSelected = isSelected,
                                                modifier = Modifier.weight(1f),
                                                onClick = {
                                                    viewModel.updateEditingFolder(
                                                        folder.copy(
                                                            folderShape = shapeType.name,
                                                            cornerRadiusDp = when (shapeType) {
                                                                FolderEntity.ShapeType.CIRCLE -> 99
                                                                FolderEntity.ShapeType.SQUARE -> 8
                                                                FolderEntity.ShapeType.LARGE_ROUNDED -> 32
                                                                else -> 22
                                                            }
                                                        )
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> { // Grid Tab
                        item {
                            Text("Grid Layout Presets", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(Modifier.height(8.dp))

                            val presets = listOf(
                                Pair(1, 1), Pair(2, 2), Pair(2, 3), Pair(3, 2),
                                Pair(3, 3), Pair(4, 2), Pair(4, 3), Pair(4, 4),
                                Pair(5, 2), Pair(5, 3), Pair(1, 5)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                presets.forEach { (cols, rows) ->
                                    val isSelected = folder.columns == cols && folder.rows == rows
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.updateEditingFolder(folder.copy(columns = cols, rows = rows)) },
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
                            Text("Columns: ${folder.columns}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Slider(
                                value = folder.columns.toFloat(),
                                onValueChange = { viewModel.updateEditingFolder(folder.copy(columns = it.toInt())) },
                                valueRange = 1f..5f,
                                steps = 3,
                                colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = ElectricBlue)
                            )
                        }

                        item {
                            Text("Visible Rows: ${folder.rows}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Slider(
                                value = folder.rows.toFloat(),
                                onValueChange = { viewModel.updateEditingFolder(folder.copy(rows = it.toInt())) },
                                valueRange = 1f..5f,
                                steps = 3,
                                colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = ElectricBlue)
                            )
                        }

                        item {
                            Text("Grid Spacing: ${folder.spacingDp} dp", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Slider(
                                value = folder.spacingDp.toFloat(),
                                onValueChange = { viewModel.updateEditingFolder(folder.copy(spacingDp = it.toInt())) },
                                valueRange = 2f..16f,
                                colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = ElectricBlue)
                            )
                        }
                    }

                    3 -> { // Size Tab
                        item {
                            Text("Widget Size Presets", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(Modifier.height(8.dp))

                            val sizes = listOf(
                                Triple("Small", 2, 2),
                                Triple("Medium", 3, 2),
                                Triple("Large", 3, 3),
                                Triple("Wide", 4, 3)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                sizes.forEach { (label, c, r) ->
                                    val isMatch = folder.columns == c && folder.rows == r
                                    FilterChip(
                                        selected = isMatch,
                                        onClick = { viewModel.updateEditingFolder(folder.copy(columns = c, rows = r)) },
                                        label = { Text(label) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = GlowingViolet,
                                            selectedLabelColor = Color.White,
                                            containerColor = BrandNavyCard,
                                            labelColor = Color.LightGray
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        item {
                            Text("Icon Sizing: ${folder.iconSizeDp} dp", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Slider(
                                value = folder.iconSizeDp.toFloat(),
                                onValueChange = { viewModel.updateEditingFolder(folder.copy(iconSizeDp = it.toInt())) },
                                valueRange = 32f..64f,
                                colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = ElectricBlue)
                            )
                        }

                        item {
                            Text("Corner Radius: ${folder.cornerRadiusDp} dp", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Slider(
                                value = folder.cornerRadiusDp.toFloat(),
                                onValueChange = { viewModel.updateEditingFolder(folder.copy(cornerRadiusDp = it.toInt())) },
                                valueRange = 0f..40f,
                                colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = ElectricBlue)
                            )
                        }
                    }

                    4 -> { // Colors Tab (Comprehensive Palette & Themes)
                        item {
                            Text("Color Themes", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(Modifier.height(8.dp))

                            val themes = listOf(
                                Triple("Midnight", "#090D1E", "#192040"),
                                Triple("Ocean", "#04152D", "#0D3B66"),
                                Triple("Neon Blue", "#0B1B3D", "#00E5FF"),
                                Triple("Cyber Purple", "#16082B", "#8B5CF6"),
                                Triple("Emerald", "#062319", "#10B981"),
                                Triple("Sunset", "#2B0B1E", "#F97316"),
                                Triple("Rose Gold", "#2B141E", "#FB7185"),
                                Triple("AMOLED Black", "#000000", "#0B0B0E"),
                                Triple("Minimal Light", "#E2E8F0", "#F8FAFC")
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                themes.forEach { (name, startHex, endHex) ->
                                    val isSelected = folder.backgroundColorHex.equals(startHex, ignoreCase = true)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(
                                                        Color(android.graphics.Color.parseColor(startHex)),
                                                        Color(android.graphics.Color.parseColor(endHex))
                                                    )
                                                )
                                            )
                                            .border(if (isSelected) 2.dp else 1.dp, if (isSelected) NeonCyan else Color.Transparent, RoundedCornerShape(12.dp))
                                            .clickable {
                                                viewModel.updateEditingFolder(
                                                    folder.copy(
                                                        backgroundColorHex = startHex,
                                                        isGradient = true,
                                                        gradientEndColorHex = endHex
                                                    )
                                                )
                                                hexInput = startHex
                                            }
                                            .padding(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        Text(name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(Modifier.height(4.dp))
                            Text("Preset Color Palette", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(Modifier.height(8.dp))

                            val fullPalette = listOf(
                                "#000000", "#050505", "#1E1E24", "#FFFFFF", "#D1D5DB",
                                "#0A1128", "#1D4ED8", "#38BDF8", "#388BFD", "#00E5FF",
                                "#14B8A6", "#22C55E", "#10B981", "#6EE7B7", "#84CC16",
                                "#EAB308", "#F59E0B", "#F97316", "#FB7185", "#EF4444",
                                "#F43F5E", "#EC4899", "#D946EF", "#A855F7", "#8B5CF6",
                                "#C084FC", "#78350F"
                            )

                            val paletteRows = fullPalette.chunked(9)
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                paletteRows.forEach { rowHexes ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        rowHexes.forEach { hex ->
                                            val isSelected = folder.backgroundColorHex.equals(hex, ignoreCase = true)
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(android.graphics.Color.parseColor(hex)))
                                                    .border(2.dp, if (isSelected) NeonCyan else Color.Transparent, CircleShape)
                                                    .clickable {
                                                        viewModel.updateEditingFolder(folder.copy(backgroundColorHex = hex))
                                                        hexInput = hex
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (isSelected) {
                                                    Icon(
                                                        Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = if (hex == "#FFFFFF" || hex == "#D1D5DB") Color.Black else Color.White,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Custom HEX Input & Opacity
                        item {
                            Spacer(Modifier.height(4.dp))
                            Text("Custom HEX Color", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF94A3B8))
                            Spacer(Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = hexInput,
                                    onValueChange = { input ->
                                        hexInput = input
                                        if (input.startsWith("#") && (input.length == 7 || input.length == 9)) {
                                            try {
                                                android.graphics.Color.parseColor(input)
                                                viewModel.updateEditingFolder(folder.copy(backgroundColorHex = input))
                                            } catch (e: Exception) {
                                                // Invalid hex
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = NeonCyan,
                                        unfocusedBorderColor = BrandNavyBorder
                                    ),
                                    placeholder = { Text("#388BFD", color = Color.Gray) },
                                    singleLine = true
                                )
                                Spacer(Modifier.width(10.dp))
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            try {
                                                Color(android.graphics.Color.parseColor(folder.backgroundColorHex))
                                            } catch (e: Exception) {
                                                ElectricBlue
                                            }
                                        )
                                        .border(1.dp, Color.White, RoundedCornerShape(10.dp))
                                )
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(stringResource(R.string.opacity), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                Text("${(folder.backgroundOpacity * 100).toInt()}%", fontSize = 13.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                            }

                            Slider(
                                value = folder.backgroundOpacity,
                                onValueChange = { viewModel.updateEditingFolder(folder.copy(backgroundOpacity = it)) },
                                valueRange = 0.1f..1.0f,
                                colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = ElectricBlue)
                            )
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Border Thickness: ${folder.borderThicknessDp} dp", color = Color.LightGray, fontSize = 12.sp)
                                Slider(
                                    value = folder.borderThicknessDp.toFloat(),
                                    onValueChange = { viewModel.updateEditingFolder(folder.copy(borderThicknessDp = it.toInt())) },
                                    valueRange = 0f..6f,
                                    steps = 5,
                                    modifier = Modifier.width(180.dp),
                                    colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = ElectricBlue)
                                )
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(stringResource(R.string.show_title), color = Color.White, fontSize = 13.sp)
                                Switch(
                                    checked = folder.showTitle,
                                    onCheckedChange = { viewModel.updateEditingFolder(folder.copy(showTitle = it)) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = ElectricBlue)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(stringResource(R.string.show_labels), color = Color.White, fontSize = 13.sp)
                                Switch(
                                    checked = folder.showAppLabels,
                                    onCheckedChange = { viewModel.updateEditingFolder(folder.copy(showAppLabels = it)) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = ElectricBlue)
                                )
                            }
                        }
                    }

                    5 -> { // Frame Tab (Custom Widget Frame Images)
                        item {
                            Text("Custom Widget Frame Image", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Select a photo from your gallery to use as decorative border or frame background.", fontSize = 12.sp, color = Color.LightGray)
                            Spacer(Modifier.height(10.dp))

                            if (!folder.frameImagePath.isNullOrBlank()) {
                                val file = File(folder.frameImagePath)
                                val bmp = if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = BrandNavyCard),
                                    shape = RoundedCornerShape(14.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandNavyBorder)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (bmp != null) {
                                                Image(
                                                    bitmap = bmp.asImageBitmap(),
                                                    contentDescription = "Frame Preview",
                                                    modifier = Modifier
                                                        .size(64.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                )
                                            }
                                            Spacer(Modifier.width(14.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Frame Image Active", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Text(file.name, color = Color.Gray, fontSize = 11.sp, maxLines = 1)
                                            }
                                        }

                                        Spacer(Modifier.height(14.dp))

                                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Button(
                                                onClick = {
                                                    photoPickerLauncher.launch(
                                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                    )
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Change Image", fontSize = 12.sp)
                                            }

                                            Button(
                                                onClick = {
                                                    viewModel.updateEditingFolder(folder.copy(frameImagePath = null))
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252).copy(alpha = 0.2f)),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Text("Remove", color = Color(0xFFFF5252), fontSize = 12.sp)
                                            }
                                        }

                                        Spacer(Modifier.height(14.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Frame Opacity", color = Color.LightGray, fontSize = 12.sp)
                                            Text("${(folder.frameOpacity * 100).toInt()}%", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        Slider(
                                            value = folder.frameOpacity,
                                            onValueChange = { viewModel.updateEditingFolder(folder.copy(frameOpacity = it)) },
                                            valueRange = 0.1f..1.0f,
                                            colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = ElectricBlue)
                                        )
                                    }
                                }
                            } else {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                    colors = CardDefaults.cardColors(containerColor = BrandNavyCard),
                                    shape = RoundedCornerShape(14.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandNavyBorder)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(28.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(36.dp))
                                        Spacer(Modifier.height(10.dp))
                                        Text("Select Image from Gallery", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Will be clipped to the widget shape", color = Color.Gray, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(10.dp))
                }
            }

            // Pinned Floating Live Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrandNavyDark)
                    .padding(horizontal = 24.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                LiveWidgetPreviewCard(
                    folder = folder,
                    apps = currentApps,
                    getIcon = { pkg -> viewModel.appScanner.getAppIcon(pkg, 96) }
                )
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
fun ShapeOptionCard(
    label: String,
    gradient: List<Color>,
    shapeType: FolderEntity.ShapeType,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BrandNavyCard),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) NeonCyan else BrandNavyBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(
                        when (shapeType) {
                            FolderEntity.ShapeType.CIRCLE -> CircleShape
                            FolderEntity.ShapeType.SQUARE -> RoundedCornerShape(4.dp)
                            FolderEntity.ShapeType.LARGE_ROUNDED -> RoundedCornerShape(16.dp)
                            FolderEntity.ShapeType.LIST -> RoundedCornerShape(8.dp)
                            else -> RoundedCornerShape(10.dp)
                        }
                    )
                    .background(Brush.linearGradient(gradient))
                    .then(
                        if (shapeType == FolderEntity.ShapeType.BORDER) {
                            Modifier.border(2.5.dp, Color.White, RoundedCornerShape(10.dp))
                        } else Modifier
                    )
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else Color.LightGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun LiveWidgetPreviewCard(
    folder: FolderEntity,
    apps: List<FolderAppItem>,
    getIcon: (String) -> Bitmap?
) {
    val bgColor = try {
        Color(android.graphics.Color.parseColor(folder.backgroundColorHex)).copy(alpha = folder.backgroundOpacity)
    } catch (e: Exception) {
        Color(0xFF0C112A)
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
        ElectricBlue
    }

    val radius = when (folder.folderShape) {
        FolderEntity.ShapeType.CIRCLE.name -> 99.dp
        FolderEntity.ShapeType.SQUARE.name -> 8.dp
        FolderEntity.ShapeType.LARGE_ROUNDED.name -> 32.dp
        else -> folder.cornerRadiusDp.dp
    }

    val frameBmp = remember(folder.frameImagePath) {
        if (!folder.frameImagePath.isNullOrBlank()) {
            val file = File(folder.frameImagePath)
            if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
        } else null
    }

    Card(
        modifier = Modifier
            .width(220.dp)
            .height(170.dp),
        shape = RoundedCornerShape(radius),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(folder.borderThicknessDp.dp, borderColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(bgColor, endColor)))
        ) {
            // Draw decorative frame image if active
            if (frameBmp != null) {
                Image(
                    bitmap = frameBmp.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(radius)),
                    alpha = folder.frameOpacity
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                if (folder.showTitle) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = folder.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                val maxDisplay = (folder.columns * folder.rows).coerceIn(1, 10)
                val displayApps = apps.take(maxDisplay)

                if (displayApps.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No apps selected", fontSize = 11.sp, color = Color.Gray)
                    }
                } else {
                    val rows = displayApps.chunked(folder.columns.coerceAtLeast(1))
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
                                rowApps.forEach { item ->
                                    val bmp = getIcon(item.packageName)
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        if (bmp != null) {
                                            Image(
                                                bitmap = bmp.asImageBitmap(),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .background(ElectricBlue.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                            )
                                        }

                                        if (folder.showAppLabels) {
                                            Text(
                                                text = item.getDisplayName(),
                                                fontSize = 8.sp,
                                                color = Color.White,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.width(36.dp),
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
