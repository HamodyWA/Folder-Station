package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.FolderAppItem
import com.example.data.FolderEntity
import com.example.data.FolderPreset
import com.example.data.FolderRepository
import com.example.model.InstalledAppInfo
import com.example.service.AppScanner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class NavigationTab {
    HOME,
    PRESETS,
    SHORTCUTS,
    SETTINGS
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FolderRepository(
        context = application,
        folderDao = AppDatabase.getInstance(application).folderDao()
    )
    val appScanner = AppScanner(application)

    val folders: StateFlow<List<FolderEntity>> = repository.allFolders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentTab = MutableStateFlow(NavigationTab.HOME)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _installedApps = MutableStateFlow<List<InstalledAppInfo>>(emptyList())
    val installedApps: StateFlow<List<InstalledAppInfo>> = _installedApps.asStateFlow()

    private val _isLoadingApps = MutableStateFlow(false)
    val isLoadingApps: StateFlow<Boolean> = _isLoadingApps.asStateFlow()

    // Editor state
    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _editingFolder = MutableStateFlow<FolderEntity?>(null)
    val editingFolder: StateFlow<FolderEntity?> = _editingFolder.asStateFlow()

    // Settings
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _messageToast = MutableStateFlow<String?>(null)
    val messageToast: StateFlow<String?> = _messageToast.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDefaultFoldersIfEmpty()
            loadInstalledApps()
        }
    }

    fun setTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun clearToast() {
        _messageToast.value = null
    }

    fun showToast(msg: String) {
        _messageToast.value = msg
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            _isLoadingApps.value = true
            _installedApps.value = appScanner.getInstalledApps()
            _isLoadingApps.value = false
        }
    }

    fun openCreateFolder(preset: FolderPreset? = null, initialApps: List<FolderAppItem> = emptyList()) {
        val newFolder = if (preset != null) {
            preset.toFolderEntity("New Folder").withApps(initialApps)
        } else {
            FolderEntity(
                id = UUID.randomUUID().toString(),
                name = "New Folder"
            ).withApps(initialApps)
        }
        _editingFolder.value = newFolder
        _isEditing.value = true
    }

    fun openEditFolder(folder: FolderEntity) {
        _editingFolder.value = folder
        _isEditing.value = true
    }

    fun closeEditor() {
        _isEditing.value = false
        _editingFolder.value = null
    }

    fun updateEditingFolder(updated: FolderEntity) {
        _editingFolder.value = updated
    }

    fun addAppToEditingFolder(app: InstalledAppInfo) {
        val current = _editingFolder.value ?: return
        val currentApps = current.getApps().toMutableList()
        if (currentApps.none { it.packageName == app.packageName }) {
            currentApps.add(
                FolderAppItem(
                    packageName = app.packageName,
                    activityName = app.activityName,
                    label = app.appName,
                    order = currentApps.size
                )
            )
            _editingFolder.value = current.withApps(currentApps)
        }
    }

    fun removeAppFromEditingFolder(packageName: String) {
        val current = _editingFolder.value ?: return
        val updatedApps = current.getApps()
            .filter { it.packageName != packageName }
            .mapIndexed { idx, item -> item.copy(order = idx) }
        _editingFolder.value = current.withApps(updatedApps)
    }

    fun moveAppInEditingFolder(fromIndex: Int, toIndex: Int) {
        val current = _editingFolder.value ?: return
        val list = current.getApps().toMutableList()
        if (fromIndex in list.indices && toIndex in list.indices) {
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            val reordered = list.mapIndexed { idx, itm -> itm.copy(order = idx) }
            _editingFolder.value = current.withApps(reordered)
        }
    }

    fun saveCurrentFolder() {
        val folder = _editingFolder.value ?: return
        viewModelScope.launch {
            repository.saveFolder(folder)
            _isEditing.value = false
            _editingFolder.value = null
            _messageToast.value = "Folder saved successfully!"
        }
    }

    fun pinFolderToHome(folder: FolderEntity) {
        val success = repository.requestPinWidget(folder)
        if (!success) {
            _messageToast.value = "To add to home screen: Long press desktop -> Widgets -> Folder Station"
        } else {
            _messageToast.value = "Widget pin requested!"
        }
    }

    fun duplicateFolder(folder: FolderEntity) {
        viewModelScope.launch {
            repository.duplicateFolder(folder)
            _messageToast.value = "Folder duplicated!"
        }
    }

    fun deleteFolder(folderId: String) {
        viewModelScope.launch {
            repository.deleteFolder(folderId)
            _messageToast.value = "Folder deleted."
        }
    }
}
