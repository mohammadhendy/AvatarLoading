package mohammadhendy.avatarloading.tasks

import android.widget.ImageView
import mohammadhendy.avatarloading.avatar.Avatar
import mohammadhendy.avatarloading.avatar.RequestBuilder

class TaskBuilder(private val avatar: Avatar) {

    private val requestBuilder: RequestBuilder =
        RequestBuilder()

    fun into(imageView: ImageView) {
        requestBuilder.requiredWidth(imageView.width)
        requestBuilder.requiredHeight(imageView.height)
        val request = requestBuilder.build()
        avatar.enqueueTask(
            ImageLoadingTask(
                avatar.bitmapUtils,
                avatar.mainThreadHandler,
                request,
                ImageViewTask(imageView, request.showProgress),
                request.errorImage?.let { DisplayErrorTask(imageView, it) }
            )
        )
    }

    fun url(url: String): TaskBuilder {
        requestBuilder.url(url)
        return this
    }

    fun placeholder(placeholder: Int): TaskBuilder {
        requestBuilder.placeholder(placeholder)
        return this
    }

    fun errorImage(errorRes: Int): TaskBuilder {
        requestBuilder.errorImage(errorRes)
        return this
    }
}