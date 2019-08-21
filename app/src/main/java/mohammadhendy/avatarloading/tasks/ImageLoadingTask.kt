package mohammadhendy.avatarloading.tasks

import android.graphics.Bitmap
import android.os.Handler
import androidx.annotation.WorkerThread
import androidx.core.graphics.drawable.toBitmap
import mohammadhendy.avatarloading.avatar.Request
import mohammadhendy.avatarloading.cache.CacheEntry
import mohammadhendy.avatarloading.cache.DiskCache
import mohammadhendy.avatarloading.cache.MemoryCache
import mohammadhendy.avatarloading.download.LoadingError
import mohammadhendy.avatarloading.download.DownloadProgressCallback
import mohammadhendy.avatarloading.download.DownloadResultCallback
import mohammadhendy.avatarloading.download.ImageDownloader
import mohammadhendy.avatarloading.utils.*
import kotlin.Exception

class ImageLoadingTask(
    private val bitmapUtils: BitmapUtils,
    private val mainThreadHandler: Handler,
    private val request: Request,
    private val imageViewTask: ImageViewTask,
    private val displayErrorTask: DisplayErrorTask?,
    private val memoryCache: MemoryCache,
    private val diskCache: DiskCache
) : Runnable, DownloadResultCallback, DownloadProgressCallback {
    companion object {
        private const val TAG = "ImageLoadingTask"
    }

    @WorkerThread
    override fun run() {
        try {
            checkInterrupted()
            if (request.placeholder != null) {
                imageViewTask.bitmap = bitmapUtils.decodeResource(request.placeholder)?.toBitmap()?.let { bitmap ->
                    bitmapUtils.getCircle(bitmap)
                }
                mainThreadHandler.post(imageViewTask)
            }
            if (request.memoryCache) {
                val bitmap = memoryCache.get(request.key())
                if (bitmap != null) {
                    onLoadFromMemory(bitmap)
                    return
                }
            }

            if (request.diskCache) {
                val data = diskCache.get(request.key())?.data
                if (data != null) {
                    onLoadFromDisk(data)
                    return
                }
            }

            val imageDownloader = ImageDownloader(request.url, this, this)
            imageDownloader.download()
        } catch (e: InterruptedException) {
            Logger.e(TAG, "Exception when run Task", e)
        }
    }

    override fun onDownloadCompleted(data: ByteArray) {
        decodeAndNotifyUI(data, false)
    }

    override fun onDownloadError(error: LoadingError) {
        if (error != LoadingError.Cancelled) {
            displayErrorTask?.let {
                mainThreadHandler.post(it)
            }
        }
    }

    override fun onProgressUpdate(progress: Int) {
        Logger.d(TAG, "downloading progress %$progress")
        imageViewTask.progress = progress
        mainThreadHandler.post(imageViewTask)
    }

    private fun onLoadFromMemory(bitmap: Bitmap) {
        checkInterrupted()
        imageViewTask.bitmap = bitmapUtils.getCircle(bitmap)
        imageViewTask.showProgress = false
        checkInterrupted()
        mainThreadHandler.post(imageViewTask)
    }

    private fun onLoadFromDisk(data: ByteArray) {
        decodeAndNotifyUI(data, true)
    }

    private fun decodeAndNotifyUI(data: ByteArray, noDiskCache: Boolean) {
        checkInterrupted()
        try {
            bitmapUtils.decodeSampledBitmap(data, request.requiredWidth, request.requiredHeight)?.let {
                imageViewTask.bitmap = bitmapUtils.getCircle(it)
                if (request.memoryCache) {
                    memoryCache.put(request.key(), it)
                } else {
                    it.recycle()
                }
            }
            imageViewTask.showProgress = false
            checkInterrupted()
            mainThreadHandler.post(imageViewTask)
            if (!noDiskCache && request.diskCache) {
                val key = request.key()
                diskCache.put(key, CacheEntry(key, data.sizeKBytes(), data))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Exception when decode bitmap", e)
            displayErrorTask?.let {
                mainThreadHandler.post(it)
            }
        }
    }
}