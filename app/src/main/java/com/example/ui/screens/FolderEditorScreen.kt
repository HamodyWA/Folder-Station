package com.example.ui.screens

import android.graphics.Bitmap
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.GridView
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderEditorScreen(
    folder: FolderEntity,
    viewModel: MainViewModel,
    onSave: () -> Unit,
    onPin: (FolderEntity) -> Unit,
    onBack: () -> Unit
) {
    var showAppPicker by remember { mutableStateOf(false) }
    var activeTab by remember { mutableIntStateOf(1) } // 0: Apps, 1: Shape, 2: Grid, 3: Size, 4: Colors
    var hexInput by remember(folder.backgroundColorHex) { mutableStateOf(folder.backgroundColorHex) }

    val currentApps = folder.getApps()
    val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = BrandNavyDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Widget",
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
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 18.dp, vertical = 6.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFF388BFD), Color(0xFF8B5CF6))),
                                RoundedCornerShape(20.dp)
                            )
                            .testTag("save_widget_btn")
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
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
                        text = "Folder Name",
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
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = folder.name,
                                onValueChange = {
                                    if (it.length <= 20) {
                                        viewModel.updateEditingFolder(folder.copy(name = it))
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                placeholder = { Text("Enter folder name...", color = Color.Gray) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Icon(Icons.Default.Edit, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "${folder.name.length}/20",
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
                        Triple(4, "Colors", Icons.Default.Palette)
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
                                    .background(
                                        if (isSelected) ElectricBlue else BrandNavyCard
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) NeonCyan else BrandNavyBorder,
                                        RoundedCornerShape(14.dp)
                                    )
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
                                    text = "${currentApps.size} Applications Included",
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
                                        .height(130.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(BrandNavyCard)
                                        .clickable { showAppPicker = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(32.dp))
                                        Spacer(Modifier.height(6.dp))
                                        Text("Tap to select applications", color = Color.LightGray, fontSize = 13.sp)
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
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                    } else {
                                        Image(
                                            painter = painterResource(R.drawable.ic_folder_station_logo),
                                            contentDescription = null,
                                            modifier = Modifier.size(36.dp)
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

                                    IconButton(
                                        onClick = { viewModel.moveAppInEditingFolder(index, index - 1) },
                                        enabled = index > 0
                                    ) {
                                        Icon(Icons.Default.ArrowUpward, contentDescription = "Up", tint = if (index > 0) Color.White else Color.DarkGray)
                                    }

                                    IconButton(
                                        onClick = { viewModel.moveAppInEditingFolder(index, index + 1) },
                                        enabled = index < currentApps.size - 1
                                    ) {
                                        Icon(Icons.Default.ArrowDownward, contentDescription = "Down", tint = if (index < currentApps.size - 1) Color.White else Color.DarkGray)
                                    }

                                    IconButton(
                                        onClick = { viewModel.removeAppFromEditingFolder(appItem.packageName) }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color(0xFFFF5252))
                                    }
                                }
                            }
                        }
                    }

                    1 -> { // Shape Tab (Exactly 3x3 Cards as in reference mockup!)
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

                            // 3x3 Grid of Shape Cards
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

                        // Background Color Section
                        item {
                            Spacer(Modifier.height(8.dp))
                            Text("Background Color", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(Modifier.height(8.dp))

                            val palette = listOf(
                                "#0C112A", "#11162E", "#EF4444", "#F97316",
                                "#F59E0B", "#10B981", "#06B6D4", "#0EA5E9",
                                "#388BFD", "#8B5CF6", "#EC4899", "#FFFFFF"
                            )

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(palette) { hex ->
                                    val isSelected = folder.backgroundColorHex.equals(hex, ignoreCase = true)
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
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
                                                tint = if (hex == "#FFFFFF") Color.Black else Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Custom Color Bar + Hex Pill
                        item {
                            Text("Custom Color", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF94A3B8))
                            Spacer(Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(BrandNavyCard)
                                        .border(1.dp, BrandNavyBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Colorize, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                                }

                                Spacer(Modifier.width(10.dp))

                                // Gradient Color Bar
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(
                                                    Color(0xFF388BFD),
                                                    Color(0xFF8B5CF6),
                                                    Color(0xFFEC4899),
                                                    Color(0xFFF97316),
                                                    Color(0xFF10B981)
                                                )
                                            )
                                        )
                                        .clickable {
                                            val rainbow = listOf("#388BFD", "#7B3FE4", "#8B5CF6", "#EC4899", "#10B981")
                                            val pick = rainbow.random()
                                            viewModel.updateEditingFolder(folder.copy(backgroundColorHex = pick, gradientEndColorHex = "#1F153E"))
                                            hexInput = pick
                                        }
                                )

                                Spacer(Modifier.width(10.dp))

                                // Hex display pill
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(BrandNavyCard)
                                        .border(1.dp, BrandNavyBorder, RoundedCornerShape(10.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = folder.backgroundColorHex.uppercase(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // Opacity Slider
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Opacity", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                Text("${(folder.backgroundOpacity * 100).toInt()}%", fontSize = 13.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                            }

                            Slider(
                                value = folder.backgroundOpacity,
                                onValueChange = { viewModel.updateEditingFolder(folder.copy(backgroundOpacity = it)) },
                                valueRange = 0.1f..1.0f,
                                colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = ElectricBlue)
                            )
                        }

                        // Border & Shadow
                        item {
                            Text("Border & Shadow", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(Modifier.height(6.dp))

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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Show Folder Title", color = Color.White, fontSize = 14.sp)
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
                                Text("Show App Labels", color = Color.White, fontSize = 14.sp)
                                Switch(
                                    checked = folder.showAppLabels,
                                    onCheckedChange = { viewModel.updateEditingFolder(folder.copy(showAppLabels = it)) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = ElectricBlue)
                                )
                            }
                        }
                    }

                    4 -> { // Colors Tab
                        item {
                            Text("Color Themes", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(Modifier.height(8.dp))

                            val themes = listOf(
                                Triple("Midnight", "#0C112A", "#1F153E"),
                                Triple("Ocean", "#081B26", "#0E384D"),
                                Triple("Neon Blue", "#0A173B", "#103E78"),
                                Triple("Cyber Purple", "#1A0E38", "#42125A"),
                                Triple("Sunset", "#2B1123", "#4A1B29"),
                                Triple("AMOLED Black", "#05070E", "#0B0E1B")
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                themes.forEach { (name, startHex, endHex) ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                viewModel.updateEditingFolder(
                                                    folder.copy(
                                                        backgroundColorHex = startHex,
                                                        isGradient = true,
                                                        gradientEndColorHex = endHex
                                                    )
                                                )
                                            },
                                        colors = CardDefaults.cardColors(containerColor = BrandNavyCard),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandNavyBorder)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(28.dp)
                                                        .clip(CircleShape)
                                                        .background(
                                                            Brush.linearGradient(
                                                                listOf(
                                                                    Color(android.graphics.Color.parseColor(startHex)),
                                                                    Color(android.graphics.Color.parseColor(endHex))
                                                                )
                                                            )
                                                        )
                                                )
                                                Spacer(Modifier.width(10.dp))
                                                Text(name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                            }

                                            if (folder.backgroundColorHex.equals(startHex, ignoreCase = true)) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = NeonCyan)
                                            }
                                        }
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

            // Pinned Floating Live Preview matching bottom of center screen!
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

    Card(
        modifier = Modifier
            .width(220.dp)
            .height(170.dp),
        shape = RoundedCornerShape(radius),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(folder.borderThicknessDp.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(bgColor, endColor)))
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

            val maxDisplay = (folder.columns * folder.rows).coerceIn(1, 9)
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
