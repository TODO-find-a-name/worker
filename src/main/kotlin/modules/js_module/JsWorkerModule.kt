package modules.js_module

import libs.common.ViewCallbacks
import libs.common.messages.PeerMsg
import libs.common.module.WorkerModule

class JsWorkerModule(
    private val id: String,
    private val onCriticalErrorCallback: (recruiterId: String) -> Unit,
    private val sendPeerMsgCallback: (recruiterId: String, msg: PeerMsg) -> Unit,
    private val viewCallbacks: ViewCallbacks
) : WorkerModule {

    // TODO mock implementation

    private val counters = mutableMapOf<String, Int>()

    override fun id(): String {
        return id
    }

    override fun addRecruiter(recruiterId: String): Boolean {
        if(counters.containsKey(recruiterId)) {
            return false
        }
        counters[recruiterId] = 0
        return true
    }

    override fun incomingPeerMsg(recruiterId: String, msg: PeerMsg) {
        val counter = counters[recruiterId]
        if(counter == null) {
            onCriticalErrorCallback(recruiterId)
        } else {
            when (msg.msgType) {
                "NEW_JOB" -> {
                    viewCallbacks.onJobStarted(recruiterId, msg.jobId)
                    sendPeerMsgCallback(recruiterId, PeerMsg(
                        msg.msgId,
                        "NEW_JOB_ACK",
                        msg.jobId,
                        msg.jobType,
                        ""
                    ))
                }
                "NEW_TASK" -> {
                    sendPeerMsgCallback(recruiterId, PeerMsg(
                        msg.msgId,
                        "TASK_RESULT",
                        msg.jobId,
                        msg.jobType,
                        "{\"id\":$counter,\"data\":[\"\", \"\", \"\"]}"
                    ))
                    counters[recruiterId] = counter + 1
                }
                "STOP_JOB" -> {
                    viewCallbacks.onJobEnded(recruiterId, msg.jobId)
                    sendPeerMsgCallback(recruiterId, PeerMsg(
                        msg.msgId,
                        "STOP_JOB_ACK",
                        msg.jobId,
                        msg.jobType,
                        ""
                    ))
                }
            }
        }
    }

    override fun removeRecruiter(recruiterId: String) {
        counters.remove(recruiterId)
    }

}