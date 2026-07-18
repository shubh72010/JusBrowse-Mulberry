package com.jusdots.jusbrowse.storage

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

enum class WritePriority {
    IMMEDIATE,
    DELAYED,
    BATCHED
}

data class WriteOperation(
    val key: String,
    val data: String,
    val priority: WritePriority,
    val timestamp: Long = System.currentTimeMillis()
)

class StorageWritePolicyEngine {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val delayedOps = mutableListOf<WriteOperation>()
    private val batchedOps = mutableListOf<WriteOperation>()

    private var delayedJob: Job? = null
    private var batchedJob: Job? = null
    private var crashSafetyJob: Job? = null

    private val _flushRequest = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val flushRequest: SharedFlow<Unit> = _flushRequest.asSharedFlow()

    private var onImmediateWrite: ((WriteOperation) -> Unit)? = null
    private var onDelayedWrite: ((List<WriteOperation>) -> Unit)? = null
    private var onBatchedWrite: ((List<WriteOperation>) -> Unit)? = null
    private var onForcedFlush: ((List<WriteOperation>) -> Unit)? = null

    fun setImmediateHandler(handler: (WriteOperation) -> Unit) {
        onImmediateWrite = handler
    }

    fun setDelayedHandler(handler: (List<WriteOperation>) -> Unit) {
        onDelayedWrite = handler
    }

    fun setBatchedHandler(handler: (List<WriteOperation>) -> Unit) {
        onBatchedWrite = handler
    }

    fun setForcedFlushHandler(handler: (List<WriteOperation>) -> Unit) {
        onForcedFlush = handler
    }

    fun writeImmediate(key: String, data: String) {
        val op = WriteOperation(key, data, WritePriority.IMMEDIATE)
        onImmediateWrite?.invoke(op)
    }

    fun writeDelayed(key: String, data: String) {
        delayedOps.add(WriteOperation(key, data, WritePriority.DELAYED))
        scheduleDelayedFlush()
    }

    fun writeBatched(key: String, data: String) {
        batchedOps.add(WriteOperation(key, data, WritePriority.BATCHED))
        scheduleBatchedFlush()
    }

    private fun scheduleDelayedFlush() {
        if (delayedJob?.isActive == true) return
        delayedJob = scope.launch {
            delay(5000)
            flushDelayed()
        }
    }

    private fun scheduleBatchedFlush() {
        if (batchedJob?.isActive == true) return
        batchedJob = scope.launch {
            delay(30000)
            flushBatched()
        }
    }

    private fun flushDelayed() {
        if (delayedOps.isEmpty()) return
        val batch = delayedOps.toList()
        delayedOps.clear()
        onDelayedWrite?.invoke(batch)
    }

    private fun flushBatched() {
        if (batchedOps.isEmpty()) return
        val batch = batchedOps.toList()
        batchedOps.clear()
        onBatchedWrite?.invoke(batch)
    }

    fun forceFlush() {
        val allOps = mutableListOf<WriteOperation>()
        allOps.addAll(delayedOps)
        allOps.addAll(batchedOps)
        delayedOps.clear()
        batchedOps.clear()

        if (allOps.isNotEmpty()) {
            onForcedFlush?.invoke(allOps)
        }

        delayedJob?.cancel()
        batchedJob?.cancel()
    }

    fun startCrashSafetyTimer(intervalMs: Long = 30000) {
        crashSafetyJob?.cancel()
        crashSafetyJob = scope.launch {
            while (isActive) {
                delay(intervalMs)
                forceFlush()
                _flushRequest.tryEmit(Unit)
            }
        }
    }

    fun stopCrashSafetyTimer() {
        crashSafetyJob?.cancel()
    }

    fun cancelAllPending() {
        delayedOps.clear()
        batchedOps.clear()
        delayedJob?.cancel()
        batchedJob?.cancel()
        crashSafetyJob?.cancel()
    }
}
