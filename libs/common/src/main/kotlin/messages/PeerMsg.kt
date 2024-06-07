package messages

data class PeerMsg(
    val msgId: String,
    val msgType: String,
    val module: String,
    val jobId: String,
    val jobType: String,
    val payload: String
)
