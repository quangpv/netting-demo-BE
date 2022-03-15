package com.onehypernet.demo.component

import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.util.*

@Component
class PasswordHashes {
    fun digest(password: String): String {
        return MessageDigest.getInstance("MD5")
            .digest(password.toByteArray())
            .let { Base64.getEncoder().encodeToString(it) }
    }
}