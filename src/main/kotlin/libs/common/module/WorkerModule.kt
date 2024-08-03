package libs.common.module

import com.corundumstudio.socketio.AckCallback
import com.corundumstudio.socketio.SocketIOClient
import libs.common.messages.LocalPeerMsg
import libs.common.messages.PeerMsg
import libs.core.worker.GuiSocket
import libs.core.worker.gui.StartJobGuiMsg
import libs.core.worker.gui.StopJobGuiMsg
import libs.core.worker.utils.ExtremeSolution
import libs.core.worker.utils.JsonParser
import libs.core.worker.utils.Logger
import libs.core.worker.utils.ShutdownCode
import libs.core.worker.utils.WorkerSettings
import java.util.concurrent.CompletableFuture

const val NEW_RECRUITER_CHANNEL = "NEW_RECRUITER"
const val REMOVE_RECRUITER_CHANNEL = "REMOVE_RECRUITER"
const val NEW_JOB_CHANNEL = "NEW_JOB"
const val NEW_TASK_CHANNEL = "NEW_TASK"
const val STOP_JOB_CHANNEL = "STOP_JOB"
const val CRITICAL_ERROR_CHANNEL = "CRITICAL_ERROR"

class WorkerModule(
    private val id: String,
    private val client: SocketIOClient,
    private val parser: JsonParser,
    private val logger: Logger,
    private val settings: WorkerSettings,
    private val guiSocket: GuiSocket
){

    fun id(): String {
        return id
    }

    fun addRecruiter(recruiterId: String): Boolean {
        return sendWithAck(NEW_RECRUITER_CHANNEL, recruiterId, recruiterId)
    }

    fun incomingPeerMsg(recruiterId: String, msg: PeerMsg) {
        when(msg.msgType){
            NEW_TASK_CHANNEL -> redirectMsgToClient(recruiterId, msg){ }
            NEW_JOB_CHANNEL -> redirectMsgToClient(recruiterId, msg){
                guiSocket.send(StartJobGuiMsg(recruiterId, msg.jobId), parser)
            }
            STOP_JOB_CHANNEL -> redirectMsgToClient(recruiterId, msg){
                guiSocket.send(StopJobGuiMsg(recruiterId, msg.jobId), parser)
            }
        }
    }

    private fun sendWithAck(channel: String, recruiterId: String, payload: String): Boolean {
        val futureResult: CompletableFuture<Boolean> = CompletableFuture()
        client.sendEvent(
            channel,
            BooleanAck(futureResult, settings.moduleForwardingTimeoutSeconds, logger, id, recruiterId, payload),
            payload
        )
        return futureResult.get()
    }

    private fun redirectMsgToClient(recruiterId: String, msg: PeerMsg, onSuccess: () -> Unit) {
        if(sendWithAck(msg.msgType, recruiterId, parser.toJson(LocalPeerMsg.createFromPeerMsg(msg, recruiterId)))){
            onSuccess()
        }
    }

    fun removeRecruiter(recruiterId: String) {
        client.sendEvent(REMOVE_RECRUITER_CHANNEL, recruiterId)
    }

}

private class BooleanAck(
    private val result: CompletableFuture<Boolean>,
    timeoutSeconds: Int,
    private val logger: Logger,
    private val moduleId: String,
    private val recruiterId: String,
    private val payload: String
): AckCallback<Boolean>(Boolean::class.java, timeoutSeconds) {

    override fun onSuccess(p0: Boolean?) {
        result.complete(p0)
    }

    override fun onTimeout() {
        ExtremeSolution.shutdown(
            logger,
            ShutdownCode.MODULE_FORWARDING_TIMEOUT,
            "Timeout while forwarding msg to module $moduleId for Recruiter $recruiterId with payload $payload"
        )
    }
}