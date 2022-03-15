package com.onehypernet.demo.model.enumerate

enum class TransactionType {
    None,
    Payable,
    Receivable;

    companion object {
        operator fun get(value: String?): TransactionType {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: None
        }
    }
}