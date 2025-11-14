package bob.colbaskin.umirhack7.common.utils

import android.util.Log
import bob.colbaskin.umirhack7.common.ApiResult
import kotlinx.coroutines.TimeoutCancellationException
import retrofit2.HttpException
import java.io.IOException

const val TAG = "Api Call Error"

suspend inline fun <reified T, reified R> safeApiCall(
    apiCall: suspend () -> T,
    successHandler: (T) -> R
): ApiResult<R> {
    return try {
        val response = apiCall()
        val result = successHandler(response)
        ApiResult.Success(data = result)
    } catch (e: Exception) {
        Log.e(TAG, e.toString())
        when (e) {
            is IOException -> ApiResult.Error(
                title = "Connection Problem!",
                text = e.message.toString()
            )
            is TimeoutCancellationException -> ApiResult.Error(
                title = "Request Timeout!",
                text = e.message.toString()
            )
            is HttpException -> {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = errorBody ?: e.message().toString()

                when (e.code()) {
                    400 -> ApiResult.Error(
                        title = "Такого пользователя несуществует!",
                        text = "Пожалуйста, проверьте ваши данные."
                    )
                    401 -> ApiResult.Error(
                        title = "Сессия истекла!",
                        text = "Your login session has expired. \\nPlease sign in again."
                    )
                    403 -> ApiResult.Error(
                        title = "Доступ запрещен!",
                        text = "У вас недостаточно прав для совершения данного дествия."
                    )
                    409 -> ApiResult.Error(
                        title = "Пользователь с таким именем уже существует! ${e.message}",
                        text = "У вас недостаточно прав для совершения данного дествия."
                    )
                    422 -> ApiResult.Error(
                        title = "Неправильно заполнены поля!",
                        text = "Пожалуйста, введите данные корректно."
                    )
                    in 500..599 -> ApiResult.Error(
                        title = "System Error!",
                        text = errorMessage
                    )
                    else -> ApiResult.Error(
                        title = "Sorry!",
                        text = errorMessage
                    )
                }
            }
            else -> ApiResult.Error(
                title = "Something Went Wrong!",
                text = e.message.toString()
            )
        }
    }
}
