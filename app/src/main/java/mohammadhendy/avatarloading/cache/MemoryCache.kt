package mohammadhendy.avatarloading.cache

import android.graphics.Bitmap
import mohammadhendy.avatarloading.utils.sizeKBytes

class MemoryCache(maxSizeKBytes: Int, maxEntryCount: Int) : Cache<String, Bitmap>(maxSizeKBytes, maxEntryCount) {
    private val linkedHashMap: LinkedHashMap<String, Bitmap> = LinkedHashMap(
        0,
        0.5f,
        true
    )

    override fun loadValue(key: String): Bitmap? = linkedHashMap[key]

    override fun saveValue(key: String, value: Bitmap) {
        linkedHashMap[key] = value
    }

    override fun delete(key: String): Bitmap? = linkedHashMap.remove(key)

    override fun isCacheEmpty(): Boolean = linkedHashMap.isEmpty()

    override fun sizeOf(key: String, value: Bitmap): Int = value.sizeKBytes()

    override fun nextRemovableCandidate(): String = linkedHashMap.entries.iterator().next().key
}