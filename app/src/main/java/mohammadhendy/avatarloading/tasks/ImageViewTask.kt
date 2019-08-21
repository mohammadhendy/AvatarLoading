package mohammadhendy.avatarloading.tasks

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.MainThread
import mohammadhendy.avatarloading.utils.ImageProgressDrawable

class ImageViewTask(private val imageView: ImageView, var showProgress: Boolean) : Runnable {
    var bitmap: Bitmap? = null
    var progress: Int = 0

    private val imageProgressDrawable = ImageProgressDrawable(showProgress = showProgress)

    @MainThread
    override fun run() {
        bitmap?.let { imageProgressDrawable.bitmap = it }
        imageProgressDrawable.showProgress = showProgress
        imageProgressDrawable.progress = progress
        imageView.setImageDrawable(imageProgressDrawable)
        imageProgressDrawable.invalidateSelf()
    }
}