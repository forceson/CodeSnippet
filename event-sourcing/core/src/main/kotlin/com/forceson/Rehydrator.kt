package com.forceson

import java.lang.reflect.ParameterizedType


abstract class Rehydrator<S> protected constructor(
    private val eventReader: EventReader,
    private val seedFactory: Function1<String, S>,
    eventHandlers: Iterable<EventHandler<S, out Any>>
) {
    private val eventHandlers: Map<Class<*>, EventHandler<S, Any>>
    protected val stateType: Class<S>
    protected val eventTypes: Iterable<Class<*>>
        get() = eventHandlers.keys

    init {
        this.stateType = getStateType(javaClass)
        this.eventHandlers = toDictionary(eventHandlers)
    }

    @Suppress("UNCHECKED_CAST")
    private fun getStateType(type: Class<*>): Class<S> {
        val generic = type.genericSuperclass as ParameterizedType
        return generic.actualTypeArguments[0] as Class<S>
    }

    @Suppress("UNCHECKED_CAST")
    private fun <S> toDictionary(eventHandlers: Iterable<EventHandler<S, out Any>>): Map<Class<*>, EventHandler<S, Any>> {
        return eventHandlers
            .map { handler -> failIfHandlerIsGeneric(handler) }
            .associateBy { it.eventType } as Map<Class<*>, EventHandler<S, Any>>
    }

    private fun <S> failIfHandlerIsGeneric(handler: EventHandler<S, out Any>): EventHandler<S, out Any> {
        if (isGeneric(handler.javaClass)) {
            val message = "Non-generic class expected for event handler." +
                    " Type argument cannot be resolved." +
                    " Make sure to specify the type argument" +
                    " when declaring the event handler class."
            throw RuntimeException(message)
        }
        return handler
    }

    private fun isGeneric(type: Class<*>): Boolean {
        return type.typeParameters.isNotEmpty()
    }

    fun rehydrateState(streamId: String): Snapshot<S> {
        return eventReader.queryEvents(stateType, streamId, 1)
            .fold(Snapshot.seed(streamId, seedFactory.invoke(streamId))) { snapshot, event ->
                handleEvent(snapshot, event)
            }
    }

    private fun handleEvent(snapshot: Snapshot<S>, event: Any): Snapshot<S> {
        val handler = getHandler(event)
        val nextState = handler.handleEvent(snapshot.state, event)
        return snapshot.next(nextState)
    }

    private fun getHandler(event: Any): EventHandler<S, Any> {
        return findHandler(event)
            ?: throw RuntimeException("No event handler registered for event " + event.javaClass.name)
    }

    private fun findHandler(event: Any): EventHandler<S, Any>? {
        return eventHandlers[event.javaClass]
    }
}
