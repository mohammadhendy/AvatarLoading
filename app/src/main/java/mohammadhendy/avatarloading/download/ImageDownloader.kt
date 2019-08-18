package mohammadhendy.avatarloading.download

import mohammadhendy.avatarloading.utils.Logger
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLDecoder

class ImageDownloader(
    private val imageUrl: String,
    private val downloadCallback: DownloadResultCallback,
    private val progressCallback: DownloadProgressCallback?
) {
    companion object {
        private const val TAG = "ImageDownloader"
        private const val BUFFER_SIZE = 1024
    }

    fun download() {
        Logger.d(TAG, "download image: $imageUrl")
        var connection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        try {
            checkInterrupted()
            val decodedUrl = URLDecoder.decode(imageUrl, "UTF-8")
            val url = URL(decodedUrl)
            connection = url.openConnection() as HttpURLConnection
            if (connection.responseCode % HttpURLConnection.HTTP_OK == 0) {
                checkInterrupted()
                inputStream = connection.inputStream
                notifyData(readResponse(inputStream, connection.contentLength))
            } else {
                checkInterrupted()
                Logger.d(TAG, "Error http response code ${connection.responseCode}")
                notifyNetworkError("Server responded with ${connection.responseMessage}", connection.responseCode)
            }
        } catch (e: MalformedURLException) {
            Logger.d(TAG, "Malformed URL", e)
            notifyInvalidUrlError(e.localizedMessage)
        } catch (e: OutOfMemoryError) {
            Logger.d(TAG, "Out of memory", e)
            notifyGeneralError(e.localizedMessage)
        } catch (e: UnsupportedEncodingException) {
            Logger.d(TAG, "Unsupported encoding", e)
            notifyInvalidUrlError(e.localizedMessage)
        } catch (e: SecurityException) {
            Logger.d(TAG, "Permission Denied", e)
            notifyGeneralError(e.localizedMessage)
        } catch (e: IOException) {
            Logger.d(TAG, "IO exception", e)
            if (e is InterruptedIOException) {
                notifyCancelled()
            } else {
                notifyNetworkError(e.localizedMessage)
            }
        } catch (e: InterruptedException) {
            Logger.d(TAG, "Interrupted exception", e)
            notifyCancelled()
        } finally {
            try {
                connection?.disconnect()
                inputStream?.close()
            } catch (e: IOException) {
                Logger.d(TAG, "Closing connection or stream exception", e)
                notifyGeneralError(e.localizedMessage)
            }
        }
    }

    private fun readResponse(inputStream: InputStream, totalBytes: Int) : ByteArray =
        if (totalBytes <= 0) {
            inputStream.readBytes()
        } else {
            var bytesRead = 0
            val data = ByteArrayOutputStream(inputStream.available())
            val buffer = ByteArray(BUFFER_SIZE)
            var bytes = inputStream.read(buffer)
            while (bytes >= 0) {
                checkInterrupted()
                data.write(buffer, 0, bytes)
                bytesRead += bytes
                notifyProgress(bytesRead, totalBytes)
                bytes = inputStream.read(buffer)
            }
            data.toByteArray()
        }

    private fun notifyNetworkError(reason: String, errorCode: Int = -1) {
        downloadCallback.onDownloadError(LoadingError.Network(reason, errorCode))
    }

    private fun notifyInvalidUrlError(reason: String) {
        downloadCallback.onDownloadError(LoadingError.InvalidUrl(reason))
    }

    private fun notifyGeneralError(reason: String) {
        downloadCallback.onDownloadError(LoadingError.General(reason))
    }

    private fun notifyCancelled() {
        downloadCallback.onDownloadError(LoadingError.Cancelled)
    }

    private fun notifyProgress(downloadedBytes: Int, totalBytes: Int) {
        progressCallback?.onProgressUpdate((downloadedBytes.toFloat() / totalBytes.toFloat() * 100).toInt())
    }

    private fun notifyData(data: ByteArray) {
        downloadCallback.onDownloadCompleted(data)
    }

    private fun checkInterrupted() {
        if (Thread.interrupted()) {
            throw InterruptedException()
        }
    }
}