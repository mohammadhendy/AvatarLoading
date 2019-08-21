package mohammadhendy.avatarloading

import android.app.Application
import mohammadhendy.avatarloading.avatar.Avatar

class AvatarApplication : Application() {

    companion object {
        private val maxMemoryCacheSizeKB = (Runtime.getRuntime().maxMemory() / 1024 / 8).toInt()
        private const val maxDiskCacheSizeKB = 10 * 1024
        private const val maxMemoryCacheCount = 100
        private const val maxDiskCacheCount = 110
    }
    override fun onCreate() {
        super.onCreate()
        Avatar.init(
            context = this,
            maxDiskCacheItemCount = maxDiskCacheCount,
            maxDiskCacheSizeKBytes = maxDiskCacheSizeKB,
            maxMemoryCacheItemCount = maxMemoryCacheCount,
            maxMemoryCacheSizeKBytes = maxMemoryCacheSizeKB
        )
    }
}