package com.forceson

import com.forceson.event.PasswordHashChanged
import com.forceson.event.UserCreated

class User(
    private val id: String,
    private val username: String,
    private val passwordHash: String
) {
    fun handleEvent(event: UserCreated): User {
        return User("", event.username, "")
    }

    fun handleEvent(event: PasswordHashChanged): User {
        return User("", "", event.passwordHash)
    }

    companion object {
        fun seedFactory(id: String): User {
            return User(id, "", "")
        }
    }
}