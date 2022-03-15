package com.onehypernet.demo.guard

import com.onehypernet.demo.model.enumerate.UserRole

annotation class Guard(
    vararg val roles: UserRole
)