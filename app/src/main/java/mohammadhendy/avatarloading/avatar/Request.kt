package mohammadhendy.avatarloading.avatar

import android.support.annotation.DrawableRes
import mohammadhendy.avatarloading.tasks.AvatarJob

data class Request(
    val url: String,
    @DrawableRes val placeholder: Int?,
    @DrawableRes val errorImage: Int?,
    val memoryCache: Boolean,
    val diskCache: Boolean,
    val showProgress: Boolean,
    val requiredWidth: Int,
    val requiredHeight: Int
)

class RequestBuilder() {
    private lateinit var url: String
    @DrawableRes private var placeholder: Int? = null
    @DrawableRes private var errorImage: Int? = null
    private var memoryCache: Boolean = false
    private var diskCache: Boolean = false
    private var showProgress: Boolean = false
    private var requiredWidth: Int = 256
    private var requiredHeight: Int =  256

    fun url(url: String): RequestBuilder {
        this.url = url
        return this
    }

    fun placeholder(@DrawableRes placeholder: Int): RequestBuilder {
        this.placeholder = placeholder
        return this
    }

    fun errorImage(@DrawableRes errorRes: Int): RequestBuilder {
        this.errorImage = errorRes
        return this
    }

    fun requiredWidth(width: Int): RequestBuilder {
        this.requiredWidth = width
        return this
    }

    fun requiredHeight(height: Int): RequestBuilder {
        this.requiredHeight = height
        return this
    }

    fun showProgress(showProgress: Boolean): RequestBuilder {
        this.showProgress = showProgress
        return this
    }

    fun memoryCache(memoryCache: Boolean): RequestBuilder {
        this.memoryCache = memoryCache
        return this
    }

    fun diskCache(diskCache: Boolean): RequestBuilder {
        this.diskCache = diskCache
        return this
    }

    fun build() : Request {
        return Request(
            url = url,
            placeholder = placeholder,
            errorImage = errorImage,
            memoryCache = memoryCache,
            diskCache = diskCache,
            showProgress = showProgress,
            requiredWidth = requiredWidth,
            requiredHeight = requiredHeight
        )
    }
}

