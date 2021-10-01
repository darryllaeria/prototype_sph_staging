package com.proto.type.contact.util

import android.content.res.Resources
import android.graphics.*
import androidx.core.graphics.drawable.toBitmap
import com.proto.type.base.R
import com.proto.type.base.utils.AppLog
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class QrCodeUtils private constructor(){

    companion object {
        private const val HEIGHT = 512
        private const val ICON_HEIGHT = 64
        private const val ICON_WIDTH = 64
        private const val WIDTH = 512
        private val TAG = QrCodeUtils::class.java.simpleName

        fun generateQrCode(resources: Resources, code: String): Bitmap? {
            val writer = QRCodeWriter()
            try {
                val bitMatrix = writer.encode(code, BarcodeFormat.QR_CODE, WIDTH, HEIGHT)
                val bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.RGB_565)
                (0 until WIDTH).forEach {x ->
                    (0 until HEIGHT).forEach { y ->
                        if (bitMatrix.get(x, y)) {
                            bitmap.setPixel(x, y, Color.BLACK)
                        } else {
                            bitmap.setPixel(x, y, Color.WHITE)
                        }
                    }
                }
                val canvas = Canvas(bitmap)
                val paint = Paint(Paint.FILTER_BITMAP_FLAG)
                val overlay = Bitmap.createScaledBitmap(resources.getDrawable(R.mipmap.ic_launcher).toBitmap(), ICON_WIDTH, ICON_HEIGHT, false)
                canvas.drawBitmap(overlay, (bitmap.width / 2 - overlay.width / 2).toFloat(), (bitmap.height / 2 - overlay.height / 2).toFloat(), paint)
                return bitmap
            } catch (e: Exception) {
                AppLog.d(TAG, "Generate QR code failed with exception: $e")
                return null
            }
        }
    }
}