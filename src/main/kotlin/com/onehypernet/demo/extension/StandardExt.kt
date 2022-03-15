package com.onehypernet.demo.extension

import com.onehypernet.demo.exception.BadRequestException
import com.onehypernet.demo.exception.InvalidParameterException


infix fun Boolean.throws(message: String) {
    if (this) throw  BadRequestException(message)
}

fun throws(message: String): Nothing = throw InvalidParameterException(message)

fun <E> List<E>.toMap(keyOf: (E) -> String): Map<String, E> {
    val map = hashMapOf<String, E>()
    forEach {
        map[keyOf(it)] = it
    }
    return map
}