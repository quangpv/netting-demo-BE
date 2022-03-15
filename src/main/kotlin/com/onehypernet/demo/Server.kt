package com.onehypernet.demo

import com.onehypernet.demo.exception.GlobalExceptionHandler
import org.springframework.boot.Banner
import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType.SERVLET
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(GlobalExceptionHandler::class)
private open class Starter

fun main(args: Array<String>) {
    val app = SpringApplication(Starter::class.java)
    app.setBannerMode(Banner.Mode.OFF)
    app.webApplicationType = SERVLET
    println("Swagger UI: <link>http://localhost:8081/swagger-ui.html#/</link>")
    app.run(*args)
}
