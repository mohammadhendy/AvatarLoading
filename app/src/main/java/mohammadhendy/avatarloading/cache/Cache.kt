package mohammadhendy.avatarloading.cache

abstract class Cache<K, V>(private val maxSize: Int, private val maxCount: Int) {
    protected var size: Int = 0
    protected var count: Int = 0

    fun get(key: K): V? {
        synchronized(this) {
            return loadValue(key)
        }
    }

    fun put(key: K, value: V) {
        val addedSize = sizeOf(key, value)
        if (addedSize > maxSize) {
            remove(key)
            return
        }
        synchronized(this) {
            saveValue(key, value)
            size += addedSize
            count++
            assureMaxSizeAndCount(maxSize, maxCount)
        }
    }

    fun remove(key: K): V? {
        synchronized(this) {
            return safeDelete(key)
        }
    }

    fun clear() {
        assureMaxSizeAndCount(-1, -1)
    }

    protected abstract fun loadValue(key: K): V?

    protected abstract fun saveValue(key: K, value: V)

    protected abstract fun delete(key: K): V?

    protected abstract fun isCacheEmpty(): Boolean

    protected abstract fun sizeOf(key: K, value: V): Int

    protected abstract fun nextRemovableCandidate(): K

    private fun assureMaxSizeAndCount(maxSize: Int, maxCount: Int) {
            synchronized(this) {
                while (true) {
                if (size < 0 || isCacheEmpty() && size != 0) {
                    throw IllegalStateException("inconsistent size!")
                }

                if (count < 0 || isCacheEmpty() && count != 0) {
                    throw IllegalStateException("inconsistent count!")
                }

                if (isCacheEmpty() || count <= maxCount && size <= maxSize) {
                    return
                }

                safeDelete(nextRemovableCandidate())
            }
        }
    }

    protected fun safeDelete(key: K): V?  = delete(key)?.also {
        size -= sizeOf(key, it)
        count--
    }
}