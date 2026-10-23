package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.NavigationTab
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
fun HomeScreen(
    folders: List<FolderEntity>,
    viewModel: MainViewModel,
    onCreateFolder: () -> Unit,
    onEditFolder: (FolderEntity) -> Unit
) {
    var folderToDelete by remember { mutableStateOf<FolderEntity?>(null) }

    Scaffold(
        containerColor = BrandNavyDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateFolder,
                containerColor = ElectricBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("create_folder_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.create_folder))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.create_folder), fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.ic_folder_station_logo),
                        contentDescription = "Folder Station Logo",
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.app_name),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${folders.size} folder widgets created",
                            fontSize = 12.sp,
                            color = NeonCyan
                        )
                    }
                }

                IconButton(onClick = { viewModel.setTab(NavigationTab.SETTINGS) }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.LightGray)
                }
            }

            // Quick Tutorial Hint Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BrandNavyElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandNavyBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GlowingViolet.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Home, contentDescription = null, tint = GlowingViolet, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Add Widgets to Home Screen", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Tap 'Pin to Home' on any folder card or long press empty desktop", fontSize = 11.sp, color = Color.LightGray)
                    }
                }
            }

            // Folders List
            if (folders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Folder,
                            contentDescription = null,
                            tint = ElectricBlue.copy(alpha = 0.4f),
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.no_folders_title),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.no_folders_desc),
                            fontSize = 13.sp,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(20.dp))
                        Button(
                            onClick = onCreateFolder,
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text(stringResource(R.string.create_folder))
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(folders, key = { it.id }) { folder ->
                        FolderCard(
                            folder = folder,
                            viewModel = viewModel,
                            onEdit = { onEditFolder(folder) },
                            onPin = { viewModel.pinFolderToHome(folder) },
                            onDuplicate = { viewModel.duplicateFolder(folder) },
                            onDelete = { folderToDelete = folder }
                        )
                    }
                    item {
                        Spacer(Modifier.height(80.dp)) // Padding for FAB
                    }
                }
            }
        }
    }

    // Confirm Delete Dialog
    if (folderToDelete != null) {
        val folder = folderToDelete!!
        AlertDialog(
            onDismissRequest = { folderToDelete = null },
            title = { Text(stringResource(R.string.confirm_delete), color = Color.White) },
            text = { Text("Are you sure you want to delete '${folder.name}'? Existing home screen widgets using this folder will become unassigned.", color = Color.LightGray) },
            containerColor = BrandNavyCard,
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteFolder(folder.id)
                        folderToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.delete), color = Color(0xFFFF5252))
                }
            },
            dismissButton = {
                TextButton(onClick = { folderToDelete = null }) {
                    Text("Cancel", color = Color.LightGray)
                }
            }
        )
    }
}

@Composable
fun FolderCard(
    folder: FolderEntity,
    viewModel: MainViewModel,
    onEdit: () -> Unit,
    onPin: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    val apps = folder.getApps()
    val widgetCount = folder.getWidgetIdList().size

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
        ElectricBlue
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onEdit() },
        colors = CardDefaults.cardColors(containerColor = BrandNavyCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandNavyBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mini Folder Preview Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.verticalGradient(listOf(bgColor, endColor)))
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    val previewApps = apps.take(4)
                    if (previewApps.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Folder, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                        }
                    } else {
                        val chunks = previewApps.chunked(2)
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            chunks.forEach { rowApps ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    rowApps.forEach { appItem ->
                                        val bmp = viewModel.appScanner.getAppIcon(appItem.packageName, 48)
                                        if (bmp != null) {
                                            Image(
                                                bitmap = bmp.asImageBitmap(),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .background(ElectricBlue.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = folder.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "${apps.size} apps • ${folder.columns}×${folder.rows} grid • ${folder.folderShape.replace("_", " ").lowercase()}",
                        fontSize = 12.sp,
                        color = NeonCyan
                    )
                    if (widgetCount > 0) {
                        Text(
                            text = "Active on $widgetCount home widget(s)",
                            fontSize = 11.sp,
                            color = GlowingViolet,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = ElectricBlue)
                }
            }

            Spacer(Modifier.height(10.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onPin,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f))
                ) {
                    Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.add_to_home_screen), fontSize = 11.sp, maxLines = 1)
                }

                IconButton(
                    onClick = onDuplicate,
                    modifier = Modifier
                        .size(36.dp)
                        .background(BrandNavyElevated, RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(36.dp)
                        .background(BrandNavyElevated, RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
