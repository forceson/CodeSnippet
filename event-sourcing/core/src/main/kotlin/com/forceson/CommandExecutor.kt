package com.forceson

import java.lang.reflect.ParameterizedType


interface CommandExecutor<S, C> {

    fun produceEvents(state: S, command: C): Iterable<Any>

    val commandType: Class<*>
        get() = javaClass.genericInterfaces.map { it as ParameterizedType }
            .first { it.rawType == CommandExecutor::class.java }
            .actualTypeArguments[1] as Class<*>
}
