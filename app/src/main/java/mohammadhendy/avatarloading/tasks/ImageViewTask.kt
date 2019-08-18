package mohammadhendy.avatarloading.tasks

import android.graphics.Bitmap
import android.widget.ImageView
import mohammadhendy.avatarloading.utils.ImageProgressDrawable

class ImageViewTask(private val imageView: ImageView, var showProgress: Boolean) : Runnable {
    var bitmap: Bitmap? = null
    var progress: Int = 0

    private val drawable = ImageProgressDrawable(showProgress = showProgress)

    override fun run() {
        bitmap?.let { drawable.bitmap = it }
        drawable.showProgress = showProgress
        drawable.progress = progress
        imageView.setImageDrawable(drawable)
        imageView.invalidate()
    }
}