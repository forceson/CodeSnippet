package com.forceson

import com.forceson.messaging.Message.Command

class CommandWrapper<T : Any>(
    val id: String,
    val streamId: String,
    val data: T
) {
    fun get(): Command<T> {
        return Command(id, streamId, data)
    }
}