package mohammadhendy.avatarloading

import android.app.Application
import mohammadhendy.avatarloading.avatar.Avatar

class AvatarApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Avatar.init(this)
    }
}