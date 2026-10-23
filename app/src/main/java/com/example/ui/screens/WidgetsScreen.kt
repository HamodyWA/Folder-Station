package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.R
import com.example.data.FolderEntity
import com.example.ui.theme.BrandNavyBorder
import com.example.ui.theme.BrandNavyCard
import com.example.ui.theme.BrandNavyDark
import com.example.ui.theme.BrandNavyElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlowingViolet
import com.example.ui.theme.NeonCyan

@Composable
fun WidgetsScreen(
    folders: List<FolderEntity>,
    viewModel: MainViewModel,
    onPinWidget: (FolderEntity) -> Unit
) {
    var selectedGridPreset by remember { mutableStateOf("3×3") }
    val featuredFolder = folders.find { it.name.contains("Games", ignoreCase = true) }
        ?: folders.firstOrNull()
        ?: FolderEntity(name = "Games")

    Scaffold(
        containerColor = BrandNavyDark
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Widget Sizes & Grid Layouts",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(8.dp))

                // Selector Pills
                val pills = listOf("1×1", "2×2", "3×3", "4×3")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pills.forEach { pill ->
                        val isSelected = selectedGridPreset == pill
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) ElectricBlue else BrandNavyCard)
                                .border(1.dp, if (isSelected) NeonCyan else BrandNavyBorder, RoundedCornerShape(12.dp))
                            .clickable { selectedGridPreset = pill }
                            .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = pill,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color.LightGray
                            )
                        }
                    }
                }
            }

            // 2x2 Grid of visual cards: Small (1x1), Medium (2x2), Large (3x3), Wide (4x3)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        WidgetPresetCard(
                            title = "Small (1×1)",
                            columns = 2,
                            rows = 2,
                            folder = featuredFolder,
                            viewModel = viewModel,
                            modifier = Modifier.weight(1f)
                        )
                        WidgetPresetCard(
                            title = "Medium (2×2)",
                            columns = 3,
                            rows = 2,
                            folder = featuredFolder,
                            viewModel = viewModel,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        WidgetPresetCard(
                            title = "Large (3×3)",
                            columns = 3,
                            rows = 3,
                            folder = featuredFolder,
                            viewModel = viewModel,
                            modifier = Modifier.weight(1f)
                        )
                        WidgetPresetCard(
                            title = "Wide (4×3)",
                            columns = 4,
                            rows = 3,
                            folder = featuredFolder,
                            viewModel = viewModel,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // "Add to Home Screen" Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandNavyCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandNavyBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Add to Home Screen",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Long press on the home screen, tap Widgets, find Folder Station and choose your widget.",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8),
                            lineHeight = 16.sp
                        )
                        Spacer(Modifier.height(14.dp))

                        Button(
                            onClick = { onPinWidget(featuredFolder) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                        ) {
                            Icon(Icons.Default.Widgets, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Add Widget to Home Screen", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Phone Home Screen Showcase Mockup
            item {
                Text(
                    text = "Desktop Simulation",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF0F172A),
                                    Color(0xFF1E1B4B),
                                    Color(0xFF0B0F19)
                                )
                            )
                        )
                        .border(2.dp, Color(0xFF334155), RoundedCornerShape(24.dp))
                        .padding(14.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Status Bar Simulation
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("12:45", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("78%", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Simulated Home Screen Widget Sitting on Desktop
                        Box(
                            modifier = Modifier
                                .width(200.dp)
                                .height(160.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color(0xFF1A174E),
                                            Color(0xFF2E1A47)
                                        )
                                    )
                                )
                                .border(1.5.dp, Color(0xFF818CF8), RoundedCornerShape(22.dp))
                                .padding(10.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.SportsEsports,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = featuredFolder.name,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                val apps = featuredFolder.getApps().take(6)
                                val rows = apps.chunked(3)
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    rows.forEach { rowApps ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            rowApps.forEach { item ->
                                                val bmp = viewModel.appScanner.getAppIcon(item.packageName, 48)
                                                if (bmp != null) {
                                                    Image(
                                                        bitmap = bmp.asImageBitmap(),
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .size(26.dp)
                                                            .clip(RoundedCornerShape(6.dp))
                                                    )
                                                } else {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(26.dp)
                                                            .background(NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Simulated Dock Apps
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            val dockIcons = listOf(
                                Pair(Color(0xFF10B981), Icons.Default.Call),
                                Pair(Color(0xFF0EA5E9), Icons.Default.ChatBubble),
                                Pair(Color(0xFFEF4444), Icons.Default.Language),
                                Pair(Color(0xFFF59E0B), Icons.Default.CameraAlt)
                            )
                            dockIcons.forEach { (c, icon) ->
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(c),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun WidgetPresetCard(
    title: String,
    columns: Int,
    rows: Int,
    folder: FolderEntity,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = BrandNavyCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandNavyBorder)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF161B3D),
                                Color(0xFF261947)
                            )
                        )
                    )
                    .border(1.dp, GlowingViolet.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(6.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    if (columns > 1) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SportsEsports, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(10.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(folder.name, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }
                    val displayCount = (columns * rows).coerceIn(1, 8)
                    val apps = folder.getApps().take(displayCount)
                    val rws = apps.chunked(columns)
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rws.forEach { rw ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                rw.forEach { item ->
                                    val bmp = viewModel.appScanner.getAppIcon(item.packageName, 48)
                                    if (bmp != null) {
                                        Image(
                                            bitmap = bmp.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .background(NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(3.dp))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
