package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.FolderEntity
import com.example.data.FolderPreset
import com.example.data.PresetCatalog
import com.example.ui.theme.BrandNavyBorder
import com.example.ui.theme.BrandNavyCard
import com.example.ui.theme.BrandNavyDark
import com.example.ui.theme.BrandNavyElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlowingViolet
import com.example.ui.theme.NeonCyan

@Composable
fun PresetsScreen(
    onUsePreset: (FolderPreset) -> Unit
) {
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
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = stringResource(R.string.nav_presets),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Curated designs ready for your home screen",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(PresetCatalog.presets, key = { it.id }) { preset ->
                    PresetCard(preset = preset, onUse = { onUsePreset(preset) })
                }
                item {
                    Spacer(Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
fun PresetCard(
    preset: FolderPreset,
    onUse: () -> Unit
) {
    val bgColor = try {
        Color(android.graphics.Color.parseColor(preset.backgroundColorHex)).copy(alpha = preset.backgroundOpacity)
    } catch (e: Exception) {
        Color(0xFF11162E)
    }

    val endColor = if (preset.isGradient) {
        try {
            Color(android.graphics.Color.parseColor(preset.gradientEndColorHex)).copy(alpha = preset.backgroundOpacity)
        } catch (e: Exception) {
            bgColor
        }
    } else bgColor

    val borderColor = try {
        Color(android.graphics.Color.parseColor(preset.borderColorHex))
    } catch (e: Exception) {
        ElectricBlue
    }

    val radius = if (preset.shape == FolderEntity.ShapeType.CIRCLE) {
        50.dp
    } else {
        preset.cornerRadiusDp.dp
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BrandNavyCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandNavyBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Visual Preview Box
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(radius))
                    .background(Brush.verticalGradient(listOf(bgColor, endColor)))
                    .border(preset.borderThicknessDp.dp, borderColor, RoundedCornerShape(radius))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Mini dots/tiles simulating apps
                val count = minOf(preset.columns * preset.rows, 4)
                val dotColors = listOf(NeonCyan, GlowingViolet, Color(0xFFFF9800), Color(0xFF00E676))
                val rows = (0 until count).chunked(preset.columns.coerceAtMost(2))

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    rows.forEach { rowIndices ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowIndices.forEach { i ->
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(dotColors[i % dotColors.size])
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = preset.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "${preset.columns}×${preset.rows} • ${preset.subtitle}",
                fontSize = 11.sp,
                color = NeonCyan
            )

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = onUse,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandNavyElevated)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp), tint = ElectricBlue)
                Spacer(Modifier.width(4.dp))
                Text("Use Preset", fontSize = 11.sp, color = Color.White)
            }
        }
    }
}
