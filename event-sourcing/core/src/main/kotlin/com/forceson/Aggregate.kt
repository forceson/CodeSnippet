package com.forceson

import com.forceson.messaging.Message
import com.forceson.messaging.MessageHandler

abstract class Aggregate<S> protected constructor(
    private val eventStore: EventStore,
    seedFactory: Function1<String, S>,
    commandExecutors: Iterable<CommandExecutor<S, out Any>>,
    eventHandlers: Iterable<EventHandler<S, out Any>>
) : Rehydrator<S>(eventStore, seedFactory, eventHandlers), MessageHandler {
    private val commandExecutors: Map<Class<*>, CommandExecutor<S, Any>>

    init {
        this.commandExecutors = toDictionary(commandExecutors)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <S> toDictionary(commandExecutors: Iterable<CommandExecutor<S, out Any>>): Map<Class<*>, CommandExecutor<S, Any>> {
        return commandExecutors.associateBy { it.commandType } as Map<Class<*>, CommandExecutor<S, Any>>
    }

    override fun canHandle(message: Message): Boolean {
        if (message !is Message.Command<*>) {
            return false
        }

        return commandExecutors.containsKey(message.payload.javaClass)
    }

    override fun handle(message: Message) {
        val command = message as Message.Command<*>
        val snapshot = rehydrateState(command.streamId)
        eventStore.collectEvents(
            stateType,
            command.streamId,
            snapshot.version + 1,
            produceEvents(snapshot.state, command)
        )
    }

    private fun produceEvents(state: S, command: Message.Command<*>): Iterable<Any> {
        val payload = command.payload
        val executor = getExecutor(payload)
        val events = arrayListOf<Any>()
        executor?.produceEvents(state, payload)?.forEach(events::add)
        events.forEach(this::verifyEvent)
        return events
    }

    private fun verifyEvent(event: Any) {
        eventTypes.forEach {
            if (it.isInstance(event)) return
        }
        throw RuntimeException(
            "Event that cannot be handled was produced. Event payload type: '${event.javaClass.name}'."
        )
    }

    private fun getExecutor(payload: Any): CommandExecutor<S, Any>? {
        val payloadType = payload.javaClass
        if (!commandExecutors.containsKey(payloadType)) {
            throw RuntimeException("Unsupported command payload type '${payloadType.name}'.")
        }
        return commandExecutors[payloadType]
    }
}