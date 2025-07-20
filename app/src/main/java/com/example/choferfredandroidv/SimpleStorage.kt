package com.example.choferfredandroidv

import java.util.concurrent.ConcurrentHashMap

object SimpleStorage {
    private val storage = ConcurrentHashMap<Long, Any>()

    fun put(key: Long, value: Any) {
        storage[key] = value
    }

    fun get(key: Long): Any? = storage[key]

    fun remove(key: Long) {
        storage.remove(key)
    }

    fun containsKey(key: Long): Boolean = storage.containsKey(key)
}