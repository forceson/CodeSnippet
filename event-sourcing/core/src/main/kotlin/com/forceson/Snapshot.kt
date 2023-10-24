package com.forceson

class Snapshot<S>(
    val streamId: String,
    val version: Long,
    val state: S
) {
    fun next(newState: S): Snapshot<S> = Snapshot(streamId, version + 1, newState)

    companion object {
        fun <S> seed(streamId: String, seed: S): Snapshot<S> {
            return Snapshot(streamId, 0, seed)
        }
    }
}
