package mohammadhendy.avatarloading.tasks

import android.support.annotation.DrawableRes
import android.widget.ImageView
import mohammadhendy.avatarloading.utils.Logger

class DisplayErrorTask(private val imageView: ImageView, @DrawableRes private val errorDrawableRes: Int) : Runnable {
    companion object {
        private const val TAG = "DisplayErrorTask"
    }

    override fun run() {
        Logger.d(TAG, "error drawable will be set")
        imageView.setImageResource(errorDrawableRes)
    }
}