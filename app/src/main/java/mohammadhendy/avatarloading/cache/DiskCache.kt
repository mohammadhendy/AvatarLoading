package mohammadhendy.avatarloading.cache

import mohammadhendy.avatarloading.utils.sizeKBytes
import java.io.File

class DiskCache(
    private val cacheDirectory: File,
    maxSizeKBytes: Int,
    maxEntryCount: Int
) : Cache<String, ByteArray>(maxSizeKBytes, maxEntryCount) {
    private val linkedHashMap: LinkedHashMap<String, ByteArray> = LinkedHashMap(
        0,
        0.5f,
        true
    )

    override fun loadValue(key: String): ByteArray? = linkedHashMap[key]

    override fun saveValue(key: String, value: ByteArray) {
        linkedHashMap[key] = value
    }

    override fun delete(key: String): ByteArray? = linkedHashMap.remove(key)

    override fun isCacheEmpty(): Boolean = linkedHashMap.isEmpty()

    override fun sizeOf(key: String, value: ByteArray): Int = value.sizeKBytes()

    override fun nextRemovableCandidate(): String = linkedHashMap.entries.iterator().next().key
}