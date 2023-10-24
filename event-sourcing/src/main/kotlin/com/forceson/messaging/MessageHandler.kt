package com.forceson.messaging

interface MessageHandler {
    fun canHandle(message: Message): Boolean
    fun handle(message: Message)
}