package com.example.moneymate

object TransactionManager {
    private var currentId = 0

    fun generateId(): Int {
        return currentId++
    }

    fun resetId(startFrom: Int) {
        currentId = startFrom
    }
}
