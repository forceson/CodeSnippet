package com.forceson

import java.lang.reflect.Type

interface EventReader {

    fun queryEvents(stateType: Type, streamId: String, fromVersion: Long): Iterable<Any>

    fun queryEvents(stateType: Type, streamId: String): Iterable<Any> {
        return queryEvents(stateType, streamId, 1)
    }
}
