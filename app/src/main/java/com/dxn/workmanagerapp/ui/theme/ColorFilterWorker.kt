package com.dxn.workmanagerapp.ui.theme

import android.content.Context
import android.graphics.*
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.dxn.workmanagerapp.WorkerParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ColorFilterWorker(
    private val context: Context, private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val imageFile = workerParams.inputData.getString(WorkerParams.IMAGE_URI)?.toUri()?.toFile()
        delay(5000L)
        val result = imageFile?.let { file ->
            val bmp = BitmapFactory.decodeFile(file.absolutePath)
            val resultBitmap = bmp.copy(bmp.config, true)
            val paint = Paint()
            paint.colorFilter = LightingColorFilter(0x08FF04, 1)
            val canvas = Canvas(resultBitmap)
            canvas.drawBitmap(resultBitmap, 0f, 0f, paint)
            withContext(Dispatchers.IO) {
                val resultImageFile = File(context.cacheDir, "result.jpg")
                val outputStream = FileOutputStream(resultImageFile)
                val successFul = resultBitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    90,
                    outputStream
                )
                if (successFul) {
                    Result.success(
                        workDataOf(WorkerParams.FILTER_URI to resultImageFile.toUri().toString())
                    )
                } else {
                    Result.failure()
                }
            }
        }
        return result ?: Result.failure()
    }
}