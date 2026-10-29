package com.example.lta_macho.ui.orders

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {

    fun exportOrderToPdf(context: Context, order: Order) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(380, 580, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint()
        val titlePaint = Paint()

        // Background
        canvas.drawColor(Color.WHITE)

        // Header Title
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textSize = 20f
        titlePaint.color = Color.parseColor("#0F2851")
        titlePaint.textAlign = Paint.Align.CENTER
        canvas.drawText("WASHTRACK", 190f, 45f, titlePaint)

        // Subtitle
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 12f
        paint.color = Color.parseColor("#5A6E85")
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("Struk Transaksi Digital", 190f, 65f, paint)

        // Divider Line
        paint.color = Color.parseColor("#DCE5EE")
        paint.strokeWidth = 2f
        canvas.drawLine(30f, 80f, 350f, 80f, paint)

        // Field drawing helper
        paint.textAlign = Paint.Align.LEFT
        var y = 110f
        val lineSpacing = 35f

        fun drawField(label: String, value: String, isBold: Boolean = false) {
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = 11f
            paint.color = Color.parseColor("#5A6E85")
            canvas.drawText(label, 35f, y, paint)

            paint.typeface = Typeface.create(Typeface.DEFAULT, if (isBold) Typeface.BOLD else Typeface.NORMAL)
            paint.textSize = 13f
            paint.color = Color.parseColor("#0F2851")
            canvas.drawText(value, 35f, y + 16f, paint)

            y += lineSpacing + 6f
        }

        val currentDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        drawField("No. Transaksi:", order.id)
        drawField("Tanggal Cetak:", currentDate)
        drawField("Nama Pelanggan:", order.namaPelanggan, isBold = true)
        drawField("Nomor Plat / Identitas:", order.platNomor, isBold = true)
        drawField("Nomor WhatsApp:", order.noWhatsapp)
        drawField("Jenis Layanan:", order.jenisLayanan)
        drawField("Total Biaya:", order.totalBiaya, isBold = true)

        // Divider Line
        paint.color = Color.parseColor("#DCE5EE")
        canvas.drawLine(30f, y, 350f, y, paint)
        y += 25f

        // Status Pengerjaan & Pembayaran
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 12f
        paint.color = Color.parseColor("#0F2851")
        canvas.drawText("Status Pengerjaan: ${order.statusPengerjaan}", 35f, y, paint)
        y += 22f
        canvas.drawText("Status Pembayaran: ${order.statusPembayaran}", 35f, y, paint)

        // Footer
        y += 40f
        paint.color = Color.parseColor("#DCE5EE")
        canvas.drawLine(30f, y, 350f, y, paint)
        y += 25f

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        paint.textSize = 10f
        paint.color = Color.parseColor("#8C9DAE")
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("Terima kasih telah menggunakan layanan WASHTRACK!", 190f, y, paint)

        pdfDocument.finishPage(page)

        // File Naming: Struk_WASHTRACK_{PlatNomor}_{Tanggal}.pdf
        val sanitizedPlat = order.platNomor.replace(Regex("[^a-zA-Z0-9]"), "_")
        val dateStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "Struk_WASHTRACK_${sanitizedPlat}_$dateStamp.pdf"

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                if (uri != null) {
                    val outputStream: OutputStream? = resolver.openOutputStream(uri)
                    outputStream?.use { stream ->
                        pdfDocument.writeTo(stream)
                    }
                    Toast.makeText(context, "Struk PDF berhasil disimpan di folder Downloads!\n($fileName)", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Gagal membuat file PDF di Downloads.", Toast.LENGTH_SHORT).show()
                }
            } else {
                @Suppress("DEPRECATION")
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs()
                }
                val file = File(downloadsDir, fileName)
                FileOutputStream(file).use { stream ->
                    pdfDocument.writeTo(stream)
                }
                Toast.makeText(context, "Struk PDF berhasil disimpan di folder Downloads!\n($fileName)", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal mengunduh PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }
    }
}