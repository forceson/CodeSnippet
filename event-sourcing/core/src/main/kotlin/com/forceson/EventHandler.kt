package com.forceson

import java.lang.reflect.ParameterizedType


interface EventHandler<S, E> {

    fun handleEvent(state: S, event: E): S

    val eventType: Class<*>
        get() = javaClass.genericInterfaces.map { it as ParameterizedType }
            .first { it.rawType == EventHandler::class.java }
            .actualTypeArguments[1] as Class<*>
}
