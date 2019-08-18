package mohammadhendy.avatarloading.utils

fun Runnable.checkInterrupted() {
    if (Thread.interrupted()) {
        throw InterruptedException()
    }
}