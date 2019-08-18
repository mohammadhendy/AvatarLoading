package mohammadhendy.avatarloading.avatar

import android.content.Context
import android.os.Handler
import mohammadhendy.avatarloading.tasks.ImageLoadingTask
import java.util.concurrent.*
import android.os.Looper
import mohammadhendy.avatarloading.utils.BitmapUtils
import mohammadhendy.avatarloading.tasks.TaskBuilder


object Avatar {
    private const val CORE_THREADS_COUNT = 5
    private const val MAX_THREADS_COUNT = 5
    private const val KEEP_ALIVE_TIME = 60L

    val mainThreadHandler = Handler(Looper.getMainLooper())
    private val loadingTasksQueue: BlockingQueue<Runnable> = LinkedBlockingQueue<Runnable>()
    private val threadPoolExecutor: ThreadPoolExecutor by lazy {
        ThreadPoolExecutor(CORE_THREADS_COUNT, MAX_THREADS_COUNT, KEEP_ALIVE_TIME, TimeUnit.SECONDS, loadingTasksQueue)
    }
    private var context: Context? = null
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

    fun load(url: String) : TaskBuilder {
        checkInit()
        return TaskBuilder(this).url(url)
    }

    fun enqueueTask(imageLoadingTask: ImageLoadingTask) {
        val future = threadPoolExecutor.submit(imageLoadingTask)
//        future.cancel(true)
    }

    fun cancelAll() {
        threadPoolExecutor.shutdown()
    }

    private fun checkInit() {
        if (this.context == null) {
            throw IllegalStateException("Uninitialised! Please call Avatar.init()")
        }
    }
}