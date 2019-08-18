package mohammadhendy.avatarloading.tasks

import android.os.Handler
import mohammadhendy.avatarloading.utils.BitmapUtils
import mohammadhendy.avatarloading.utils.Logger
import mohammadhendy.avatarloading.avatar.Request
import mohammadhendy.avatarloading.download.LoadingError
import mohammadhendy.avatarloading.download.DownloadProgressCallback
import mohammadhendy.avatarloading.download.DownloadResultCallback
import mohammadhendy.avatarloading.download.ImageDownloader

class ImageLoadingTask(
    private val bitmapUtils: BitmapUtils,
    private val mainThreadHandler: Handler,
    private val request: Request,
    private val imageViewTask: ImageViewTask,
    private val displayErrorTask: DisplayErrorTask?
) : Runnable, DownloadResultCallback, DownloadProgressCallback {
    companion object {
        private const val TAG = "ImageLoadingTask"
    }
    override fun run() {
        try {
            if (request.placeholder != null) {
                imageViewTask.bitmap = bitmapUtils.decodeResource(request.placeholder)?.let { bitmapUtils.getCircle(it) }
                mainThreadHandler.post(imageViewTask)
            }
            val imageDownloader = ImageDownloader(request.url, this, this)
            imageDownloader.download()
        } catch (e: Exception) {
            Logger.e(TAG, "Exception when run Task", e)
        }
    }

    override fun onDownloadCompleted(data: ByteArray) {
        bitmapUtils.decodeSampledBitmap(data, request.requiredWidth, request.requiredHeight)?.let {
            imageViewTask.bitmap = bitmapUtils.getCircle(it)
        }
        imageViewTask.showProgress = false
        mainThreadHandler.post(imageViewTask)
    }

    override fun onDownloadError(error: LoadingError) {
        displayErrorTask?.let {
            mainThreadHandler.post(it)
        }
    }

    override fun onProgressUpdate(progress: Int) {
        Logger.d(TAG, "downloading progress %$progress")
        imageViewTask.progress = progress
        mainThreadHandler.post(imageViewTask)
    }
}