package mohammadhendy.avatarloading.tasks

import android.widget.ImageView
import mohammadhendy.avatarloading.avatar.Avatar
import mohammadhendy.avatarloading.avatar.Request
import mohammadhendy.avatarloading.avatar.RequestBuilder
import java.util.concurrent.Future

class AvatarJob(private val avatar: Avatar) {

    private lateinit var future: Future<*>
    private lateinit var request: Request
    private val requestBuilder: RequestBuilder =
        RequestBuilder()

    fun into(imageView: ImageView) {
        requestBuilder.requiredWidth(imageView.width)
        requestBuilder.requiredHeight(imageView.height)
        request = requestBuilder.build()
        future = avatar.submitJob(
            this,
            imageView,
            ImageLoadingTask(
                avatar.bitmapUtils,
                avatar.mainThreadHandler,
                request,
                ImageViewTask(imageView, request.showProgress),
                request.errorImage?.let { DisplayErrorTask(imageView, it) }
            )
        )
    }

    fun url(url: String): AvatarJob {
        requestBuilder.url(url)
        return this
    }

    fun placeholder(placeholder: Int): AvatarJob {
        requestBuilder.placeholder(placeholder)
        return this
    }

    fun errorImage(errorRes: Int): AvatarJob {
        requestBuilder.errorImage(errorRes)
        return this
    }

    fun showProgress(showProgress: Boolean): AvatarJob {
        requestBuilder.showProgress(showProgress)
        return this
    }

    fun memoryCache(memoryCache: Boolean): AvatarJob {
        requestBuilder.memoryCache(memoryCache)
        return this
    }

    fun diskCache(diskCache: Boolean): AvatarJob {
        requestBuilder.diskCache(diskCache)
        return this
    }

    fun cancel() {
        future.cancel(true)
    }
}