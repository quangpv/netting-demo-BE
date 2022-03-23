package com.onehypernet.demo.extension

import com.onehypernet.demo.exception.BadRequestException
import com.onehypernet.demo.exception.InvalidParameterException
import java.math.BigDecimal


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


fun BigDecimal?.safe(def: Double = 0.0): BigDecimal {
    return this ?: BigDecimal(def)
}


fun BigDecimal.divideTo(amount: BigDecimal): BigDecimal {
    val zero = BigDecimal(0.0)
    if (amount.compareTo(zero) == 0) {
        if (this > zero) return BigDecimal(1.0)
        return zero
    }
    return this / amount
}