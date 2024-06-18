package libs.core.worker.events.recruiter.messages

import libs.core.worker.Repository
import libs.core.worker.recruiter.PendingMsg
import libs.core.worker.recruiter.Recruiter
import libs.core.worker.utils.LoggerLvl
import libs.common.messages.PeerMsg
import libs.common.messages.PeerMsgPart
import libs.common.messages.PeerMsgPartChecked
import libs.core.worker.events.RecruiterEvent
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

class IncomingRecruiterMsgPartEvent(
    repository: Repository, recruiterId: String, private val msg: ByteBuffer
) : RecruiterEvent(repository, recruiterId) {

    override fun handleImpl(recruiter: Recruiter) {
        val decoded = decodeByteBuffer(msg)
        repository.parser.fromJson(decoded, PeerMsgPart::class.java).ifPresentOrElse(
            { msgPartParsed -> msgPartParsed.toChecked().ifPresentOrElse(
                { handleCheckedMsgPart(it, recruiter) },
                { removeRecruiterOnError("Parsed msg part does not contain all necessary values", recruiter) }
            ) },
            { removeRecruiterOnError("$decoded\nMsg part could not be parsed", recruiter) }
        )
    }

    private fun handleCheckedMsgPart(peerMsgPart: PeerMsgPartChecked, recruiter: Recruiter){
        repository.logger.logP2PIncomingPart(LoggerLvl.HIGH, peerMsgPart, recruiterId)
        if(peerMsgPart.total == 1){
            redirectCompleteMsgToModule(peerMsgPart, recruiter)
        } else {
            handleMsgPart(peerMsgPart, recruiter)
        }
    }

    private fun redirectCompleteMsgToModule(msg: PeerMsg, recruiter: Recruiter) {
        repository.logger.logP2PIncomingComplete(LoggerLvl.MID, msg, recruiterId)
        val module = repository.modules[msg.module]
        if(module == null){
            removeRecruiterOnError("Requested module not found", msg, recruiter)
        } else {
            module.incomingPeerMsg(recruiterId, msg)
        }
    }

    private fun handleMsgPart(msgPart: PeerMsgPartChecked, recruiter: Recruiter){
        var pendingMsg: PendingMsg? = recruiter.pendingMessages[msgPart.msgId]
        if(pendingMsg == null){
            pendingMsg = PendingMsg(msgPart.total, repository, recruiterId, msgPart.msgId)
            recruiter.pendingMessages[msgPart.msgId] = pendingMsg
        }
        if(pendingMsg.total != msgPart.total){
            removeRecruiterOnError("Received msg part with discording total for msg", msgPart, recruiter)
        } else if(pendingMsg.parts[msgPart.part] !== null){
            removeRecruiterOnError("Duplicate msg part for msg", msgPart, recruiter)
        } else {
            pendingMsg.parts[msgPart.part] = msgPart
            if(pendingMsg.parts.size == pendingMsg.total){
                pendingMsg.cancelTimeout()
                recruiter.pendingMessages.remove(msgPart.msgId)
                pendingMsg.mergeMessages().ifPresentOrElse(
                    { redirectCompleteMsgToModule(it, recruiter) },
                    { removeRecruiterOnError(
                        "Total msg parts reached but parts were not sequential", msgPart, recruiter
                    ) }
                )
            }
        }
    }

    private fun decodeByteBuffer(byteBuffer: ByteBuffer): String {
        return StandardCharsets.UTF_8.decode(byteBuffer).toString()
    }

    private fun removeRecruiterOnError(log: String, recruiter: Recruiter){
        repository.logger.errorRecruiter(recruiterId, "$log, removing Recruiter")
        repository.removeRecruiter(recruiter)
    }

    private fun removeRecruiterOnError(log: String, peerMsg: PeerMsg, recruiter: Recruiter){
        repository.logger.errorIncomingP2PMsg(recruiterId, peerMsg, "$log, removing Recruiter")
        repository.removeRecruiter(recruiter)
    }
}