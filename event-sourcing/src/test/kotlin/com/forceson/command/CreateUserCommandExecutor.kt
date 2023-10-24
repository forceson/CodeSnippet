package com.forceson.command

import com.forceson.CommandExecutor
import com.forceson.event.PasswordHashChanged
import com.forceson.PasswordHasher
import com.forceson.User
import com.forceson.event.UserCreated

class CreateUserCommandExecutor(
    private val passwordHasher: PasswordHasher
): CommandExecutor<User, CreateUser> {
    override fun produceEvents(state: User, command: CreateUser): Iterable<Any> {
        val password = command.password
        val hashedPassword = passwordHasher.hashPassword(password)
        return listOf(
            UserCreated(command.username),
            PasswordHashChanged(hashedPassword)
        )
    }
}