package com.forceson.event

import com.forceson.EventHandler
import com.forceson.User

class UserCreatedEventHandler: EventHandler<User, UserCreated> {
    override fun handleEvent(state: User, event: UserCreated): User {
        return state.handleEvent(event)
    }
}