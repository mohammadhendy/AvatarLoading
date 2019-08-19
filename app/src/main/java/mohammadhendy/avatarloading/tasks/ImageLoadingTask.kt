package mohammadhendy.avatarloading.tasks

import android.graphics.Bitmap
import android.os.Handler
import mohammadhendy.avatarloading.utils.BitmapUtils
import mohammadhendy.avatarloading.utils.Logger
import mohammadhendy.avatarloading.avatar.Request
import mohammadhendy.avatarloading.cache.DiskCache
import mohammadhendy.avatarloading.cache.MemoryCache
import mohammadhendy.avatarloading.download.LoadingError
import mohammadhendy.avatarloading.download.DownloadProgressCallback
import mohammadhendy.avatarloading.download.DownloadResultCallback
import mohammadhendy.avatarloading.download.ImageDownloader
import mohammadhendy.avatarloading.utils.checkInterrupted
import mohammadhendy.avatarloading.utils.key
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
    override fun run() {
        try {
            checkInterrupted()
            if (request.placeholder != null) {
                imageViewTask.bitmap = bitmapUtils.decodeResource(request.placeholder)?.let { bitmapUtils.getCircle(it) }
                mainThreadHandler.post(imageViewTask)
            }
            if (request.memoryCache) {
                val bitmap = memoryCache.get(request.key())
                if (bitmap != null) {
                    onLoadFromMemory(bitmap)
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
        checkInterrupted()
        try {
            bitmapUtils.decodeSampledBitmap(data, request.requiredWidth, request.requiredHeight)?.let {
                if (request.memoryCache) {
                    memoryCache.put(request.key(), it)
                }
                imageViewTask.bitmap = bitmapUtils.getCircle(it)
            }
            imageViewTask.showProgress = false
            checkInterrupted()
            mainThreadHandler.post(imageViewTask)
        } catch (e: Exception) {
            Logger.e(TAG, "Exception when decode btimap", e)
            displayErrorTask?.let {
                mainThreadHandler.post(it)
            }
        }
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
}