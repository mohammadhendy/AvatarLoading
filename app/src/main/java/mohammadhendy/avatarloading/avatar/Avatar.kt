package mohammadhendy.avatarloading.avatar

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.os.Handler
import mohammadhendy.avatarloading.tasks.ImageLoadingTask
import java.util.concurrent.*
import android.os.Looper
import android.widget.ImageView
import mohammadhendy.avatarloading.utils.BitmapUtils
import mohammadhendy.avatarloading.tasks.AvatarJob
import mohammadhendy.avatarloading.utils.Logger


object Avatar : LifecycleObserver {
    private const val CORE_THREADS_COUNT = 5
    private const val MAX_THREADS_COUNT = 5
    private const val KEEP_ALIVE_TIME = 60L
    private const val TAG = "Avatar"

    val mainThreadHandler = Handler(Looper.getMainLooper())
    private val loadingTasksQueue: BlockingQueue<Runnable> = LinkedBlockingQueue<Runnable>()
    private val threadPoolExecutor: ThreadPoolExecutor by lazy {
        ThreadPoolExecutor(CORE_THREADS_COUNT, MAX_THREADS_COUNT, KEEP_ALIVE_TIME, TimeUnit.SECONDS, loadingTasksQueue)
    }
    private var context: Context? = null
    private val jobsMap = mutableMapOf<ImageView, AvatarJob>()
    lateinit var bitmapUtils: BitmapUtils

    /**
     * Must be called before using any other function
     * @param context application context
     */
    fun init(context: Context) {
        if (this.context != null) {
            throw IllegalStateException("Already initialised!")
        }
        this.context = context
        bitmapUtils = BitmapUtils(context.resources)
    }

    fun load(url: String) : AvatarJob {
        checkInit()
        return AvatarJob(this).url(url)
    }

    fun submitJob(avatarJob: AvatarJob, imageView: ImageView, imageLoadingTask: ImageLoadingTask): Future<*>  {
        jobsMap.put(imageView, avatarJob)?.cancel()
        threadPoolExecutor.purge()
        return threadPoolExecutor.submit(imageLoadingTask)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cancelAll() {
        Logger.d(TAG, "cancel all")
        jobsMap.values.forEach { it.cancel() }
        jobsMap.clear()
    }

    private fun checkInit() {
        if (this.context == null) {
            throw IllegalStateException("Uninitialised! Please call Avatar.init()")
        }
    }
}