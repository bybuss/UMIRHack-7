package bob.colbaskin.umirhack7.common

sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class Error(val title: String, val text: String) : ApiResult<Nothing>
}

fun <T> ApiResult<T>.toUiState(): UiState<T> = when (this) {
    is ApiResult.Success -> UiState.Success(data = data)
    is ApiResult.Error -> UiState.Error(title = title, text = text)
}
