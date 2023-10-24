package com.forceson.messaging

interface MessageBus {
    fun send(partitionKey: String, messages: Iterable<Message>)
    fun send(partitionKey: String, message: Message) {
        send(partitionKey, listOf(message))
    }
}