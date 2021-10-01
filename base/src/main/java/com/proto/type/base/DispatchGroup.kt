package com.proto.type.base

class DispatchGroup {

    // MARK: - Private Variables
    private var count = 0
    private var runnable: (() -> Unit)? = null

    // MARK: - Init Function
    init {
        count = 0
    }

    // MARK: - Public Functions
    @Synchronized
    fun enter() {
        count++
    }

    @Synchronized
    fun leave() {
        count--
        notifyGroup()
    }

    fun notify(r: () -> Unit) {
        runnable = r
        notifyGroup()
    }

    // MARK: - Private Function
    private fun notifyGroup() {
        if (count <= 0 && runnable != null) {
            runnable!!()
        }
    }
}