package mohammadhendy.avatarloading.cache

import mohammadhendy.avatarloading.utils.Logger
import mohammadhendy.avatarloading.utils.sizeKBytes
import java.io.File

/**
 * Each cached file has an entry object that contains key and size
 * Also, entry contains the loaded file data when get/put is executed
 */
data class CacheEntry(val key:String, val sizeKBytes: Int?, var data: ByteArray?) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CacheEntry

        if (key != other.key) return false
        if (sizeKBytes != other.sizeKBytes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + (sizeKBytes ?: 0)
        return result
    }
}

class DiskCache(
    cacheDirectory: File,
    maxSizeKBytes: Int,
    maxEntryCount: Int
) : Cache<String, CacheEntry>(maxSizeKBytes, maxEntryCount) {
    companion object {
        private const val CACHE_NAME = "Avatars"
        private const val CACHE_ENTRIES_NAME = "entries"
        private const val TAG = "DiskCache"
    }
    private val cachePath = File(cacheDirectory, CACHE_NAME).also { it.mkdirs() }
    private val cacheEntriesFile = File(cachePath, CACHE_ENTRIES_NAME)
    private val linkedHashMap: LinkedHashMap<String, CacheEntry> = LinkedHashMap(
        0,
        0.5f,
        true
    )

    override fun loadValue(key: String): CacheEntry? {
        if (size == 0 && count == 0 && isCacheEmpty()) {
            loadCacheEntries(cacheEntriesFile)
        }

        val entry = linkedHashMap[key]?.copy(data = null) ?: return null
        if (entry.binaryFile.exists()) {
            entry.data = entry.binaryFile.readBytes()
        } else {
            safeDelete(key)
            return null
        }
        return entry
    }

    override fun saveValue(key: String, value: CacheEntry) {
        Logger.d(TAG, "save new cache to file ${value.binaryFile}")
        value.data?.let {
            if (!value.binaryFile.exists()) {
                value.binaryFile.createNewFile()
            }
            value.binaryFile.writeBytes(it)
            linkedHashMap[key] = value.copy(data = null)
            saveCacheEntries(cacheEntriesFile)
        }
    }

    override fun delete(key: String): CacheEntry? = linkedHashMap.remove(key)?.also {
        if (it.binaryFile.exists()) {
            it.binaryFile.delete()
        }
    }

    override fun isCacheEmpty(): Boolean = linkedHashMap.isEmpty()

    override fun sizeOf(key: String, value: CacheEntry): Int = value.sizeKBytes ?: 0

    override fun nextRemovableCandidate(): String = linkedHashMap.entries.iterator().next().key.also { Logger.d(
        TAG, "nextRemovableCandidate is $it") }

    /**
     * loads the cache entries from file
     * this will be called once per session
     * ignores invalid formatted entries or entries without files
     */
    private fun loadCacheEntries(cacheEntriesFile: File) {
        if (!cacheEntriesFile.exists()) {
            return
        }
        cacheEntriesFile.forEachLine {
            val values = it.split(" ")
            if (values.size == 2) {
                val key = values[0]
                CacheEntry(key, values[1].toInt(), null).apply {
                    if (binaryFile.exists()) {
                        linkedHashMap[key] = this
                        count++
                        size += sizeKBytes ?: 0
                    }
                }
            } else {
                Logger.e(TAG, "invalid entry in cache entries file, values count = ${values.size}")
                // Todo: trigger deleting orphan cache files
            }
        }
    }

    /**
     * saves the cache entries to file
     * ignores invalid entries
     * No need to call this in delete because it only impacts when loading the deleted entries next session which [loadCacheEntries]
     * handles by verifying that for every entry loaded there is a file exists
     */
    private fun saveCacheEntries(cacheEntriesFile: File) {
        if (!cacheEntriesFile.exists()) {
            cacheEntriesFile.createNewFile()
        }
        // clear the file content
        cacheEntriesFile.writeText("")
        linkedHashMap.entries.iterator().forEach { entry ->
            entry.value.sizeKBytes?.let { size ->
                cacheEntriesFile.appendText("${entry.key} $size\n")
            }
        }
    }

    private val CacheEntry.binaryFile
    get() = File(cachePath, "$key")
}

