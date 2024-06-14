package js_module

import messages.PeerMsg
import module.WorkerModule

class JsWorkerModule(
    private val onCriticalErrorCallback: (recruiterId: String) -> Unit,
    private val sendPeerMsgCallback: (recruiterId: String, msg: PeerMsg) -> Unit
) : WorkerModule {

    var i = 0

    override fun incomingPeerMsg(recruiterId: String, msg: PeerMsg) {
        // TODO mock implementation
        if(msg.msgType == "NEW_JOB"){
            sendPeerMsgCallback(recruiterId, PeerMsg(
                msg.msgId,
                "NEW_JOB_ACK",
                msg.module,
                msg.jobId,
                msg.jobType,
                ""
            ))
        } else if (msg.msgType == "NEW_TASK") {
            sendPeerMsgCallback(recruiterId, PeerMsg(
                msg.msgId,
                "TASK_RESULT",
                msg.module,
                msg.jobId,
                msg.jobType,
                "{\"id\":" + i++ + ",\"data\":[]}"
            ))
        } else if (msg.msgType == "STOP_JOB") {
            sendPeerMsgCallback(recruiterId, PeerMsg(
                msg.msgId,
                "STOP_JOB_ACK",
                msg.module,
                msg.jobId,
                msg.jobType,
                ""
            ))
        }
    }

    override fun removeRecruiter(recruiterId: String) {
        TODO("Not yet implemented")
    }

}