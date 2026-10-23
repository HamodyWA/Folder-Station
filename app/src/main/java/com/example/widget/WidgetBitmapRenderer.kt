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
        widthPx: Int = 450,
        heightPx: Int = 450,
        folder: FolderEntity
    ): Bitmap {
        val safeWidth = maxOf(widthPx, 120)
        val safeHeight = maxOf(heightPx, 120)
        val bitmap = Bitmap.createBitmap(safeWidth, safeHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val baseColor = try {
            Color.parseColor(folder.backgroundColorHex)
        } catch (e: Exception) {
            Color.parseColor("#11162E")
        }

        val effectiveOpacity = if (folder.folderShape == FolderEntity.ShapeType.MINIMAL.name) {
            folder.backgroundOpacity.coerceIn(0.1f, 0.45f)
        } else {
            folder.backgroundOpacity.coerceIn(0.1f, 1f)
        }

        val alphaInt = (effectiveOpacity * 255).toInt()
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

        val thicknessMultiplier = if (folder.folderShape == FolderEntity.ShapeType.BORDER.name) 4.0f else 2.5f
        val borderThicknessPx = if (folder.borderThicknessDp > 0 || folder.folderShape == FolderEntity.ShapeType.BORDER.name) {
            maxOf(folder.borderThicknessDp * thicknessMultiplier, 4f)
        } else 0f

        val strokePaint = if (borderThicknessPx > 0f) {
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

        when (folder.folderShape) {
            FolderEntity.ShapeType.CIRCLE.name -> {
                val cx = safeWidth / 2f
                val cy = safeHeight / 2f
                val radius = minOf(safeWidth, safeHeight) / 2f - inset
                canvas.drawCircle(cx, cy, radius, fillPaint)
                if (strokePaint != null) {
                    canvas.drawCircle(cx, cy, radius, strokePaint)
                }
            }
            FolderEntity.ShapeType.SQUARE.name -> {
                val radiusPx = 12f
                canvas.drawRoundRect(rect, radiusPx, radiusPx, fillPaint)
                if (strokePaint != null) {
                    canvas.drawRoundRect(rect, radiusPx, radiusPx, strokePaint)
                }
            }
            FolderEntity.ShapeType.LARGE_ROUNDED.name -> {
                val radiusPx = minOf(safeWidth, safeHeight) * 0.35f
                canvas.drawRoundRect(rect, radiusPx, radiusPx, fillPaint)
                if (strokePaint != null) {
                    canvas.drawRoundRect(rect, radiusPx, radiusPx, strokePaint)
                }
            }
            FolderEntity.ShapeType.MATERIAL.name -> {
                val radiusPx = 28f * 2.5f
                canvas.drawRoundRect(rect, radiusPx, radiusPx, fillPaint)
                if (strokePaint != null) {
                    canvas.drawRoundRect(rect, radiusPx, radiusPx, strokePaint)
                }
            }
            FolderEntity.ShapeType.MINIMAL.name -> {
                val radiusPx = (folder.cornerRadiusDp * 2.5f).coerceAtLeast(16f)
                canvas.drawRoundRect(rect, radiusPx, radiusPx, fillPaint)
                if (strokePaint != null) {
                    canvas.drawRoundRect(rect, radiusPx, radiusPx, strokePaint)
                }
            }
            FolderEntity.ShapeType.LIST.name -> {
                val radiusPx = 22f * 2.5f
                canvas.drawRoundRect(rect, radiusPx, radiusPx, fillPaint)
                if (strokePaint != null) {
                    canvas.drawRoundRect(rect, radiusPx, radiusPx, strokePaint)
                }
            }
            FolderEntity.ShapeType.LARGE_ICON.name -> {
                val radiusPx = 20f * 2.5f
                canvas.drawRoundRect(rect, radiusPx, radiusPx, fillPaint)
                if (strokePaint != null) {
                    canvas.drawRoundRect(rect, radiusPx, radiusPx, strokePaint)
                }
            }
            FolderEntity.ShapeType.BORDER.name -> {
                val radiusPx = (folder.cornerRadiusDp * 2.5f).coerceAtLeast(20f)
                canvas.drawRoundRect(rect, radiusPx, radiusPx, fillPaint)
                if (strokePaint != null) {
                    canvas.drawRoundRect(rect, radiusPx, radiusPx, strokePaint)
                }
            }
            else -> { // ROUNDED_RECT
                val radiusPx = (folder.cornerRadiusDp * 2.5f).coerceAtLeast(16f)
                canvas.drawRoundRect(rect, radiusPx, radiusPx, fillPaint)
                if (strokePaint != null) {
                    canvas.drawRoundRect(rect, radiusPx, radiusPx, strokePaint)
                }
            }
        }

        return bitmap
    }
}
