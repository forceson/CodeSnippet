package com.forceson.command

import com.forceson.CommandExecutor
import com.forceson.User
import com.forceson.event.UnknownEvent

class RaiseUnknownEventCommandExecutor : CommandExecutor<User, RaiseUnknownEvent> {
    override fun produceEvents(state: User, command: RaiseUnknownEvent): Iterable<Any> {
        return listOf(UnknownEvent())
    }
}
