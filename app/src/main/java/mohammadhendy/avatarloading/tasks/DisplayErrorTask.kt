package mohammadhendy.avatarloading.tasks

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.MainThread
import mohammadhendy.avatarloading.utils.Logger

class DisplayErrorTask(private val imageView: ImageView, @DrawableRes private val errorDrawableRes: Int) : Runnable {
    companion object {
        private const val TAG = "DisplayErrorTask"
    }

    @MainThread
    override fun run() {
        Logger.d(TAG, "error drawable will be set")
        imageView.setImageResource(errorDrawableRes)
    }
}