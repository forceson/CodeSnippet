package com.forceson

import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap

class InMemoryEventStore(
    private val eventStore: Map<Type, Map<String, List<Any>>> = ConcurrentHashMap()
) : EventStore {

    override fun collectEvents(
        stateType: Type,
        streamId: String,
        startVersion: Long,
        events: Iterable<Any>
    ) {
        val streams = (eventStore as MutableMap).computeIfAbsent(stateType) { ConcurrentHashMap() }
        val stream = (streams as MutableMap).computeIfAbsent(streamId) { arrayListOf() }
        if ((stream.size + 1).toLong() != startVersion) {
            throw IllegalStateException(
                "Invalid start version: expected ${(stream.size + 1)}, got + $startVersion"
            )
        }

        events.forEach { (stream as MutableList).add(it) }
    }


    fun collectEvents(
        stateType: Type,
        streamId: String,
        events: Iterable<Any>
    ) {
        val startVersion = 1L
        collectEvents(stateType, streamId, startVersion, events)
    }

    override fun queryEvents(stateType: Type, streamId: String, fromVersion: Long): Iterable<Any> {
        val streams = eventStore[stateType] ?: return emptyList()
        val events = streams[streamId] ?: return emptyList()
        val fromIndexInclusive = (fromVersion - 1).toInt()
        val toIndexExclusive = events.size
        return events.subList(fromIndexInclusive, toIndexExclusive)
    }
}