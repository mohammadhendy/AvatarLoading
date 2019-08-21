package mohammadhendy.avatarloading.cache

import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File

class DiskCacheTest {
    companion object {
        private const val TEST_KEY_1 = "key1"
        private const val TEST_KEY_2 = "key2"
        private const val TEST_KEY_3 = "key3"
        private const val TEST_DATA = "1234567890"
        private const val TEST_DATA_DIFF = "0987654321"
    }

    @Rule
    fun tempFolder() = TemporaryFolder()

    private lateinit var cacheDir: File
    lateinit var diskCache: DiskCache

    @Before
    fun setUp() {
        val tempFolder = tempFolder().also { it.create() }
        cacheDir = tempFolder.root
        diskCache = DiskCache(cacheDir, 50, 10)
    }

    @After
    fun tearDown() {
        diskCache.listFiles().forEach {
            it.delete()
        }
    }

    @Test
    fun get_WhenKeyExist_ReturnValue() {
        diskCache.put(TEST_KEY_1, CacheEntry(TEST_KEY_1, 5, TEST_DATA.toByteArray()))
        val entry = diskCache.get(TEST_KEY_1)
        assertEntry(entry, TEST_KEY_1, TEST_DATA, 5)
    }

    @Test
    fun get_WhenKeyNotExist_ReturnNull() {
        val entry = diskCache.get(TEST_KEY_1)
        assertNull(entry)
        assertFalse(binaryFile(TEST_KEY_1).exists())
    }

    @Test
    fun remove_WhenKeyExist_DeleteEntryAndReturnValue() {
        diskCache.put(TEST_KEY_1, CacheEntry(TEST_KEY_1, 5, TEST_DATA.toByteArray()))
        val entry = diskCache.remove(TEST_KEY_1)
        assertEquals(entry?.key, TEST_KEY_1)
        assertEquals(entry?.sizeKBytes, 5)
        assertNull(entry?.data)
        assertFalse(binaryFile(TEST_KEY_1).exists())
    }

    @Test
    fun remove_WhenKeyNotExist_ReturnNull() {
        val entry = diskCache.remove(TEST_KEY_1)
        assertNull(entry)
        assertFalse(binaryFile(TEST_KEY_1).exists())
    }

    @Test
    fun put_WhenDataSizeGreaterThanMaxSize_DontPut() {
        diskCache.put(TEST_KEY_1, CacheEntry(TEST_KEY_1, 100, TEST_DATA.toByteArray()))
        val entry = diskCache.get(TEST_KEY_1)
        assertNull(entry)
        assertFalse(binaryFile(TEST_KEY_1).exists())
    }

    @Test
    fun put_TestMaxSizeRespected() {
        diskCache = DiskCache(cacheDir, 10, 10)
        putTestEntries()

        val entry1 = diskCache.get(TEST_KEY_1)
        assertNull(entry1)
        assertFalse(binaryFile(TEST_KEY_1).exists())

        val entry2 = diskCache.get(TEST_KEY_2)
        assertNull(entry2)
        assertFalse(binaryFile(TEST_KEY_2).exists())

        val entry3 = diskCache.get(TEST_KEY_3)
        assertEntry(entry3, TEST_KEY_3, TEST_DATA, 10)
    }

    @Test
    fun put_TestMaxCountRespected() {
        diskCache = DiskCache(cacheDir, 100, 2)
        putTestEntries()

        val entry1 = diskCache.get(TEST_KEY_1)
        assertNull(entry1)
        assertFalse(binaryFile(TEST_KEY_1).exists())

        val entry2 = diskCache.get(TEST_KEY_2)
        assertEntry(entry2, TEST_KEY_2, TEST_DATA, 5)

        val entry3 = diskCache.get(TEST_KEY_3)
        assertEntry(entry3, TEST_KEY_3, TEST_DATA, 10)
    }

    @Test
    fun put_TestMaxSizeRespectedWhenPutAlreadyExistingEntries() {
        diskCache = DiskCache(cacheDir, 10, 10)
        putTestEntries()
        diskCache.put(TEST_KEY_3, CacheEntry(TEST_KEY_3, 5, TEST_DATA_DIFF.toByteArray()))
        diskCache.put(TEST_KEY_2, CacheEntry(TEST_KEY_2, 5, TEST_DATA.toByteArray()))

        val entry1 = diskCache.get(TEST_KEY_1)
        assertNull(entry1)
        assertFalse(binaryFile(TEST_KEY_1).exists())

        val entry2 = diskCache.get(TEST_KEY_2)
        assertEntry(entry2, TEST_KEY_2, TEST_DATA, 5)

        val entry3 = diskCache.get(TEST_KEY_3)
        assertEntry(entry3, TEST_KEY_3, TEST_DATA_DIFF, 5)
    }

    @Test
    fun put_TestMaxCountRespectedWhenPutAlreadyExistingEntries() {
        diskCache = DiskCache(cacheDir, 100, 2)

        putTestEntries()
        diskCache.put(TEST_KEY_3, CacheEntry(TEST_KEY_3, 5, TEST_DATA_DIFF.toByteArray()))

        val entry1 = diskCache.get(TEST_KEY_1)
        assertNull(entry1)
        assertFalse(binaryFile(TEST_KEY_1).exists())

        val entry2 = diskCache.get(TEST_KEY_2)
        assertEntry(entry2, TEST_KEY_2, TEST_DATA, 5)

        val entry3 = diskCache.get(TEST_KEY_3)
        assertEntry(entry3, TEST_KEY_3, TEST_DATA_DIFF, 5)
    }

    private fun binaryFile(key: String) = File(File(cacheDir, DiskCache.CACHE_NAME) , key)

    private fun assertEntry(entry: CacheEntry?, key: String, data: String, size: Int) {
        assertEquals(entry?.key, key)
        assertEquals(entry?.sizeKBytes, size)
        assertArrayEquals(entry?.data, data.toByteArray())
        assertTrue(binaryFile(key).exists())
    }

    private fun putTestEntries() {
        diskCache.put(TEST_KEY_1, CacheEntry(TEST_KEY_1, 5, TEST_DATA.toByteArray()))
        diskCache.put(TEST_KEY_2, CacheEntry(TEST_KEY_2, 5, TEST_DATA.toByteArray()))
        diskCache.put(TEST_KEY_3, CacheEntry(TEST_KEY_3, 10, TEST_DATA.toByteArray()))
    }
}