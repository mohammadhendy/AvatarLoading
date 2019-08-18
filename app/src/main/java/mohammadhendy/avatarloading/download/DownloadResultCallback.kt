package mohammadhendy.avatarloading.download

interface DownloadResultCallback {
    /**
     * Notify callers with the downloaded data
     * @param data Image data as [ByteArray]
     */
    fun onDownloadCompleted(data: ByteArray)

    /**
     * Notify callers when error happened while download
     * @param error is [LoadingError] that describes the error type and reason
     */
    fun onDownloadError(error: LoadingError)
}