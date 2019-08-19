package mohammadhendy.avatarloading.utils

import android.graphics.Bitmap
import mohammadhendy.avatarloading.avatar.Request
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * If runnable's thread is interrupted then throw [InterruptedException]
 */
fun Runnable.checkInterrupted() {
    if (Thread.interrupted()) {
        throw InterruptedException()
    }
}

/**
 * Return Bitmap size in KBytes
 * @return [Int] size of bitmap in KBytes
 */
fun Bitmap.sizeKBytes() = convertToKBytes(byteCount)

/**
 * Return ByteArray size in KBytes
 * @return [Int] size of ByteArray in KBytes
 */
fun ByteArray.sizeKBytes() = convertToKBytes(size)

/**
 * Generate key from url of the request by hashing it to MD5 digest hash
 */
fun Request.key(): String {
    val sizedUrl = "$url/$requiredWidth/$requiredHeight"
    return md5(sizedUrl) ?: sizedUrl.hashCode().toString()
}

private fun convertToKBytes(byteCount: Int): Int = byteCount / 1024 + if (byteCount % 1024 > 0) 1 else 0

private fun md5(input: String): String? = try {
    val md = MessageDigest.getInstance("MD5")
    val hexString = StringBuilder()
    for (digestByte in md.digest(input.toByteArray()))
        hexString.append(String.format("%02X", digestByte))

    hexString.toString()
} catch (e: NoSuchAlgorithmException) {
    null
}