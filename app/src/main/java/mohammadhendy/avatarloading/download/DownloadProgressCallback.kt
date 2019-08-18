package mohammadhendy.avatarloading.download

interface DownloadProgressCallback {
    /**
     * Notify callers with download progress
     * @param progress Percentage of download completeness
     */
    fun onProgressUpdate(progress: Int)
}