package com.dxn.workmanagerapp

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.random.Random

class DownloadWorker(
    private val context: Context,
    private val workParams: WorkerParameters
) : CoroutineWorker(context, workParams) {

    override suspend fun doWork(): Result {
        showNotification()
        delay(5000L)
        val response = FileDownloadApi.instance.downloadImage()
        response.body()?.let { responseBody ->
            return withContext(Dispatchers.IO) {
                val file = File(context.cacheDir, "image.jpg")
                val outputStream = FileOutputStream(file)
                outputStream.use { stream ->
                    try {
                        stream.write(responseBody.bytes())
                    } catch (e:IOException) {
                        return@withContext Result.failure(
                            workDataOf(
                                WorkerParams.ERROR_MESSAGE to e.localizedMessage
                            )
                        )
                    }
                    Result.success(
                        workDataOf(
                            WorkerParams.IMAGE_URI to file.toUri().toString()
                        )
                    )
                }
            }
        }

        if(!response.isSuccessful) {
            if(response.code().toString().startsWith("5")) {
                return Result.retry()
            }
            return Result.failure(
                workDataOf(
                    WorkerParams.ERROR_MESSAGE to "Network Error"
                )
            )
        }
        return Result.failure(
            workDataOf(
                WorkerParams.ERROR_MESSAGE to "Unknown Error"
            )
        )
    }

    private suspend fun showNotification() {
        setForeground(
            ForegroundInfo(
                Random.nextInt(),
                NotificationCompat.Builder(context, "download_channel")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentText("Downloading...")
                    .setContentTitle("Download in progress")
                    .build()
            )
        )
    }

}