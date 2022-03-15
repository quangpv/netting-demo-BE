package com.onehypernet.demo.component

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ApplicationProperties(
    @Value("\${admin.email}")
    val adminEmail: String,
    @Value("\${admin.default-password}")
    val adminDefaultPassword: String,
    @Value("\${forex.api-key}")
    val forexApiKey: String
)