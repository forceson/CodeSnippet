package com.forceson.messaging

import java.time.LocalDateTime

sealed class Message {
    data class Command<T : Any>(
        val id: String,
        val streamId: String,
        val payload: T
    ) : Message()

    data class Event<T : Any>(
        val id: String,
        val streamId: String,
        val version: Long,
        val raisedTimeUtc: LocalDateTime,
        val payload: T
    ) : Message()
}
