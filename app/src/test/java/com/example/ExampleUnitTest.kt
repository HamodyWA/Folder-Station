package com.example

import com.example.data.FolderAppItem
import com.example.data.FolderEntity
import com.example.data.PresetCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleUnitTest {

    @Test
    fun testFolderAppItemSerialization() {
        val apps = listOf(
            FolderAppItem(
                packageName = "com.android.chrome",
                activityName = "com.google.android.apps.chrome.Main",
                label = "Chrome",
                order = 0
            ),
            FolderAppItem(
                packageName = "com.spotify.music",
                activityName = "com.spotify.music.MainActivity",
                label = "Spotify",
                customLabel = "Music",
                order = 1
            )
        )

        val jsonStr = FolderAppItem.listToJson(apps)
        val deserialized = FolderAppItem.listFromJson(jsonStr)

        assertEquals(2, deserialized.size)
        assertEquals("com.android.chrome", deserialized[0].packageName)
        assertEquals("Chrome", deserialized[0].getDisplayName())
        assertEquals("Music", deserialized[1].getDisplayName())
    }

    @Test
    fun testWidgetIdBinding() {
        val folder = FolderEntity(name = "Test Folder")
        assertTrue(folder.getWidgetIdList().isEmpty())

        val folderWithWidget = folder.addWidgetId(101).addWidgetId(202)
        assertEquals(listOf(101, 202).sorted(), folderWithWidget.getWidgetIdList().sorted())

        val removed = folderWithWidget.removeWidgetId(101)
        assertEquals(listOf(202), removed.getWidgetIdList())
    }

    @Test
    fun testPresetCatalog() {
        val presets = PresetCatalog.presets
        assertTrue(presets.isNotEmpty())

        val classic = presets.find { it.id == "preset_3x3_classic" }
        assertEquals(3, classic?.columns)
        assertEquals(3, classic?.rows)

        val entity = classic?.toFolderEntity("My Station")
        assertEquals("My Station", entity?.name)
        assertEquals(3, entity?.columns)
        assertEquals(3, entity?.rows)
    }
}
