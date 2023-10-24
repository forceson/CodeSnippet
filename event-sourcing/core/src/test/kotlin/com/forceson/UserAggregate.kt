package com.forceson

import com.forceson.command.ChangePasswordCommandExecutor
import com.forceson.command.CreateUserCommandExecutor
import com.forceson.command.RaiseUnknownEventCommandExecutor
import com.forceson.event.PasswordHashChangedEventHandler
import com.forceson.event.UserCreatedEventHandler

class UserAggregate(
    eventStore: EventStore
) : Aggregate<User>(
    eventStore,
    User::seedFactory,
    listOf(
        CreateUserCommandExecutor(passwordHasher),
        ChangePasswordCommandExecutor(passwordHasher),
        RaiseUnknownEventCommandExecutor()
    ),
    listOf(
        UserCreatedEventHandler(),
        PasswordHashChangedEventHandler()
    )
) {
    companion object {
        private val passwordHasher = SimplePasswordHasher()
    }
}