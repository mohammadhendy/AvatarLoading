package mohammadhendy.avatarloading.download

sealed class LoadingError(val message: String) {
    data class InvalidUrl(val reason: String) : LoadingError(reason)
    data class Network(val reason: String, val errorCode: Int) : LoadingError(reason)
    data class General(val reason: String) : LoadingError(reason)
}