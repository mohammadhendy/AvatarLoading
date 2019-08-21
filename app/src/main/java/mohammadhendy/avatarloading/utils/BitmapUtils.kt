package mohammadhendy.avatarloading.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat


class BitmapUtils(private val context: Context) {
    companion object {
        private const val TAG = "BitmapUtils"
    }

    fun decodeResource(resId: Int): Drawable? = ContextCompat.getDrawable(context, resId)

    fun decodeSampledBitmap(byteArray: ByteArray, requiredWidth: Int, requiredHeight: Int): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        // just decode bitmap bounds to get the width and height so we ignore the returned value here
        decodeByteArray(byteArray, options)
        val width = if (requiredWidth == 0) options.outWidth else requiredWidth
        val height = if (requiredHeight == 0) options.outHeight else requiredHeight
        options.inSampleSize = calcSampleSize(options, width, height)
        options.inJustDecodeBounds = false
        return decodeByteArray(byteArray, options)
    }

    fun getCircle(bitmap: Bitmap): Bitmap? {
        var output: Bitmap
        try {
            output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        } catch (e: OutOfMemoryError) {
            Log.e(TAG, "Out of memory in getCircle()")
            return null
        }
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawCircle(
            (bitmap.width / 2).toFloat(),
            (bitmap.height / 2).toFloat(),
            (bitmap.width / 2).toFloat(),
            paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

    /**
     * Calculates ratios of bitmap height and width to requested height and width
     * Choose the smallest ratio as sampleSize value, this will guarantee
     * a final image with both dimensions larger than or equal to the
     * requested height and width.
     */
    private fun calcSampleSize(options: BitmapFactory.Options, requiredWidth: Int, requiredHeight: Int): Int {
        var sampleSize = 1
        if (options.outHeight > requiredHeight || options.outWidth > requiredWidth) {
            val heightRatio = Math.round(options.outHeight.toFloat() / requiredHeight.toFloat())
            val widthRatio = Math.round(options.outWidth.toFloat() / requiredWidth.toFloat())
            sampleSize = Math.min(widthRatio, heightRatio)
        }
        return sampleSize
    }

    private fun decodeByteArray(byteArray: ByteArray, options: BitmapFactory.Options): Bitmap? = try {
        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
    } catch (e: OutOfMemoryError) {
        Logger.e(TAG, "Out of memory in decodeByteArray()", e)
        null
    }
}