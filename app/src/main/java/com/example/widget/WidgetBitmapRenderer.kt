package com.example.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import com.example.data.FolderEntity

object WidgetBitmapRenderer {

    fun renderBackground(
        widthPx: Int = 400,
        heightPx: Int = 400,
        folder: FolderEntity
    ): Bitmap {
        val safeWidth = maxOf(widthPx, 100)
        val safeHeight = maxOf(heightPx, 100)
        val bitmap = Bitmap.createBitmap(safeWidth, safeHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val baseColor = try {
            Color.parseColor(folder.backgroundColorHex)
        } catch (e: Exception) {
            Color.parseColor("#11162E")
        }

        val alphaInt = (folder.backgroundOpacity.coerceIn(0f, 1f) * 255).toInt()
        val startColorWithAlpha = Color.argb(
            alphaInt,
            Color.red(baseColor),
            Color.green(baseColor),
            Color.blue(baseColor)
        )

        val endColor = if (folder.isGradient) {
            try {
                val endBase = Color.parseColor(folder.gradientEndColorHex)
                Color.argb(
                    alphaInt,
                    Color.red(endBase),
                    Color.green(endBase),
                    Color.blue(endBase)
                )
            } catch (e: Exception) {
                startColorWithAlpha
            }
        } else {
            startColorWithAlpha
        }

        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            shader = LinearGradient(
                0f, 0f, 0f, safeHeight.toFloat(),
                startColorWithAlpha,
                endColor,
                Shader.TileMode.CLAMP
            )
        }

        val borderThicknessPx = (folder.borderThicknessDp * 2.5f)
        val strokePaint = if (folder.borderThicknessDp > 0) {
            val strokeColor = try {
                Color.parseColor(folder.borderColorHex)
            } catch (e: Exception) {
                Color.parseColor("#388BFD")
            }
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = borderThicknessPx
                color = strokeColor
            }
        } else null

        val inset = borderThicknessPx / 2f
        val rect = RectF(inset, inset, safeWidth - inset, safeHeight - inset)

        val radiusPx = when (folder.folderShape) {
            FolderEntity.ShapeType.CIRCLE.name -> {
                minOf(safeWidth, safeHeight) / 2f
            }
            FolderEntity.ShapeType.ROUNDED_SQUARE.name -> {
                folder.cornerRadiusDp * 3.0f
            }
            FolderEntity.ShapeType.MINIMAL_TRANSPARENT.name -> {
                folder.cornerRadiusDp * 2.5f
            }
            else -> {
                folder.cornerRadiusDp * 2.5f
            }
        }

        canvas.drawRoundRect(rect, radiusPx, radiusPx, fillPaint)
        if (strokePaint != null) {
            canvas.drawRoundRect(rect, radiusPx, radiusPx, strokePaint)
        }

        return bitmap
    }
}
