package com.forceson.event

import com.forceson.EventHandler
import com.forceson.User

class PasswordHashChangedEventHandler : EventHandler<User, PasswordHashChanged> {
    override fun handleEvent(state: User, event: PasswordHashChanged): User {
        return state.handleEvent(event)
    }
}