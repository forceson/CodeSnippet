package com.forceson.command

import com.forceson.CommandExecutor
import com.forceson.PasswordHasher
import com.forceson.User
import com.forceson.event.PasswordHashChanged

class ChangePasswordCommandExecutor(
    private val passwordHasher: PasswordHasher
) : CommandExecutor<User, ChangePassword> {
    override fun produceEvents(state: User, command: ChangePassword): Iterable<Any> {
        val hashedPassword = passwordHasher.hashPassword(command.password)
        return listOf(PasswordHashChanged(hashedPassword))
    }
}