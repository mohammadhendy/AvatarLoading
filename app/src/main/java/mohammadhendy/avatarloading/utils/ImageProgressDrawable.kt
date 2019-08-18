package mohammadhendy.avatarloading.utils

import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.RectF
import android.graphics.Paint.Join
import android.graphics.Paint.Cap



class ImageProgressDrawable(
    private val progressColor: Int = Color.parseColor("#FF4081"),
    private val progressBackgroundColor: Int = Color.parseColor("#757575"),
    private val maxProgress: Int = 100,
    private val minProgress: Int = 0,
    var showProgress: Boolean
) : Drawable() {

    // the outside real progress value
    private var realProgress = 0
    var bitmap: Bitmap? = null
    var progress: Int = 0
        get() = realProgress
        set(value) {
            if (!showProgress || progress > maxProgress || progress < minProgress)
                return
            realProgress = value
            field = if (maxProgress == 100) value else (progress.toFloat() / maxProgress.toFloat() * 100F).toInt()
        }


    private var progressStrokeWidth = 10
        set(value) {
            arcPaintBackground.strokeWidth = value.toFloat()
            arcPaintPrimary.strokeWidth = value.toFloat()
            field = value
        }
    private val arcPaintBackground: Paint = Paint().apply {
        isDither = true
        style = Paint.Style.STROKE
        strokeCap = Cap.BUTT
        strokeJoin = Join.BEVEL
        color = progressBackgroundColor
        strokeWidth = progressStrokeWidth.toFloat()
        isAntiAlias = true
    }
    private val arcPaintPrimary: Paint = Paint().apply {
        isDither = true
        style = Paint.Style.STROKE
        strokeCap = Cap.BUTT
        strokeJoin = Join.BEVEL
        color = progressColor
        strokeWidth = progressStrokeWidth.toFloat()
        isAntiAlias = true
    }
    private val imagePaint: Paint = Paint()
    private val arcRect = RectF()
    private val imageRect = RectF()

    /**
     * This method does not scale Bitmap and so there is inconsistency between Loaded Image and placeholder
     */
    @Deprecated("Please use calcRectScale")
    private fun calcRect() {
        val rect = copyBounds()
        val bitmapWidth = bitmap?.width ?: rect.width()
        val bitmapHeight = bitmap?.height ?: rect.height()
        val widthDiff = if (bitmapWidth < rect.width()) rect.width() - bitmapWidth else 0
        val heightDiff = if (bitmapHeight < rect.height()) rect.height() - bitmapHeight else 0
        val width = Math.min(rect.width(), bitmapWidth)
        val height = Math.min(rect.height(), bitmapHeight)
        val arcDiameter = Math.min(width, height) - progressStrokeWidth
        val top = heightDiff / 2 + (if (height < width) height / 2 - arcDiameter / 2 else width / 2 - arcDiameter / 2).toFloat()
        val left = widthDiff / 2 + (if (height > width) width / 2 - arcDiameter / 2 else height / 2 - arcDiameter / 2).toFloat()

        arcRect.set(left, top, left + arcDiameter, top + arcDiameter)
        imageRect.set(left, top, left + arcDiameter, top + arcDiameter)
    }

    /**
     * This method scales Bitmap to fit the view bounds however if the bitmap is much smaller than the view
     * it will be distorted
     */
    private fun calcRectScale() {
        val rect = copyBounds()
        val width = rect.width()
        val height = rect.height()
        val arcDiameter = Math.min(width, height) - progressStrokeWidth
        val top = (if (height < width) height / 2 - arcDiameter / 2 else width / 2 - arcDiameter / 2).toFloat()
        val left = (if (height > width) width / 2 - arcDiameter / 2 else height / 2 - arcDiameter / 2).toFloat()

        arcRect.set(left, top, left + arcDiameter, top + arcDiameter)
        imageRect.set(left, top, left + arcDiameter, top + arcDiameter)
    }

    override fun draw(canvas: Canvas) {
        calcRectScale()
        bitmap?.let {
            canvas.drawBitmap(it, null, imageRect, imagePaint)
        }

        if (showProgress && progress > 0) {
            canvas.drawArc(arcRect, 270f, 360f, false, arcPaintBackground)
            canvas.drawArc(arcRect, 270f, (360 * (progress / 100f)), false, arcPaintPrimary)
        }
    }

    override fun setAlpha(alpha: Int) {
        arcPaintPrimary?.alpha = alpha
        arcPaintBackground?.alpha = alpha
        imagePaint?.alpha = alpha
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter?) {
        arcPaintPrimary?.colorFilter = colorFilter
        arcPaintBackground?.colorFilter = colorFilter
        imagePaint?.colorFilter = colorFilter
    }
}