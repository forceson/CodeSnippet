package com.forceson

import java.lang.reflect.Type

interface EventCollector {

    fun collectEvents(
        stateType: Type,
        streamId: String,
        startVersion: Long,
        events: Iterable<Any>
    )
}