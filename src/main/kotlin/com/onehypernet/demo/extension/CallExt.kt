package com.onehypernet.demo.extension

import com.onehypernet.demo.exception.BadRequestException
import retrofit2.Call

fun <T> Call<T>.call(): T? {
    val result = try {
        execute()
    } catch (e: Throwable) {
        throw e
    }
    if (!result.isSuccessful) {
        val error = result.errorBody()?.string() ?: result.message()
        throw BadRequestException(error)
    }
    return result.body()
}