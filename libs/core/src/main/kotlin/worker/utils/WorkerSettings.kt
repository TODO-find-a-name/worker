package com.todo.todo.worker.utils

data class WorkerSettings(
    val brokerAddr: String,
    val organization: String,
    val recruiterEventsPerCycle: Int = 100,
    val loggingLvl: LoggerLvl = LoggerLvl.LOW,
    val p2pPayloadSizeBytes: Int = 10000
)
