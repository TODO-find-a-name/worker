package libs.core.worker.utils

data class WorkerSettings(
    val brokerAddr: String,
    val organization: String,
    val loggingLvl: LoggerLvl = LoggerLvl.LOW,
    val p2pPayloadSizeBytes: Int = 10000,
    val timeoutCheckFrequencyMs: Long = 1000,
    val recruitmentTimeoutMs: Long = 5000,
    val p2pMsgTimeoutMs: Long = 60000
)
