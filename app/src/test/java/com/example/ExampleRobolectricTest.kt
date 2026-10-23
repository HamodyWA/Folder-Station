package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.FolderEntity
import com.example.widget.WidgetBitmapRenderer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Folder Station", appName)
    }

    @Test
    fun `render widget background bitmap`() {
        val folder = FolderEntity(
            name = "Test Widget",
            cornerRadiusDp = 20,
            backgroundColorHex = "#11162E",
            borderColorHex = "#388BFD",
            borderThicknessDp = 2
        )
        val bitmap = WidgetBitmapRenderer.renderBackground(300, 300, folder)
        assertNotNull(bitmap)
        assertEquals(300, bitmap.width)
        assertEquals(300, bitmap.height)
    }
}
