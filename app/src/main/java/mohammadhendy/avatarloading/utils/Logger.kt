package mohammadhendy.avatarloading.utils

import android.util.Log

object Logger {
    fun d(tag: String, message: String, throwable: Throwable?= null) = throwable?.let {
        Log.d(tag, message, it)
    } ?: Log.d(tag, message)

    fun e(tag: String, message: String, throwable: Throwable? =null) = throwable?.let {
        Log.e(tag, message, it)
    } ?: Log.e(tag, message)
}