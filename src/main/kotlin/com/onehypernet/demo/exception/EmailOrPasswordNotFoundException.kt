package com.onehypernet.demo.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class EmailOrPasswordNotFoundException : CheckedException("Email or password invalid")