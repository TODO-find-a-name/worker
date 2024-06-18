package libs.core.worker.events.recruiter

import libs.core.worker.SharedRepository
import libs.core.worker.events.Event
import libs.core.worker.events.RemoveRecruiterEvent
import libs.core.worker.recruiter.PendingMsg
import libs.core.worker.recruiter.Recruiter
import libs.core.worker.utils.LoggerLvl
import libs.common.messages.PeerMsg
import libs.common.messages.PeerMsgPart
import libs.common.messages.PeerMsgPartChecked
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

class IncomingRecruiterMsgPartEvent(
    repository: SharedRepository, private val recruiterId: String, private val msg: ByteBuffer
) : Event(repository) {

    override fun handleImpl() {
        val recruiter = repository.recruiters[recruiterId]
        if(recruiter == null){
            println("recruiter not found on incoming p2p msg todo")
        } else {
            val decoded = decodeByteBuffer(msg)
            repository.parser.fromJson(decoded, PeerMsgPart::class.java).ifPresentOrElse(
                { msgPartParsed -> msgPartParsed.toChecked().ifPresentOrElse(
                    { handleCheckedMsgPart(it, recruiter) },
                    { removeRecruiterOnError("Parsed msg part does not contain all necessary values") }
                ) },
                { removeRecruiterOnError("$decoded\nMsg part could not be parsed") }
            )
        }
    }

    private fun handleCheckedMsgPart(peerMsgPart: PeerMsgPartChecked, recruiter: Recruiter){
        repository.logger.logP2PIncomingPart(LoggerLvl.HIGH, peerMsgPart, recruiterId)
        if(peerMsgPart.total == 1){
            redirectCompleteMsgToModule(peerMsgPart)
        } else {
            handleMsgPart(peerMsgPart, recruiter)
        }
    }

    private fun redirectCompleteMsgToModule(msg: PeerMsg) {
        repository.logger.logP2PIncomingComplete(LoggerLvl.MID, msg, recruiterId)
        val module = repository.modules[msg.module]
        if(module == null){
            removeRecruiterOnError("Requested module not found", msg)
        } else {
            module.incomingPeerMsg(recruiterId, msg)
        }
    }

    private fun handleMsgPart(msgPart: PeerMsgPartChecked, recruiter: Recruiter){
        var pendingMsg: PendingMsg? = recruiter.pendingMessages[msgPart.msgId]
        if(pendingMsg == null){
            pendingMsg = PendingMsg(msgPart.total, repository, recruiterId)
            recruiter.pendingMessages[msgPart.msgId] = pendingMsg
        }
        if(pendingMsg.total != msgPart.total){
            removeRecruiterOnError("Received msg part with discording total for msg", msgPart)
        } else if(pendingMsg.parts[msgPart.part] !== null){
            removeRecruiterOnError("Duplicate msg part for msg", msgPart)
        } else {
            pendingMsg.parts[msgPart.part] = msgPart
            if(pendingMsg.parts.size == pendingMsg.total){
                pendingMsg.cancelTimeout()
                recruiter.pendingMessages.remove(msgPart.msgId)
                pendingMsg.mergeMessages().ifPresentOrElse(
                    { redirectCompleteMsgToModule(it) },
                    { removeRecruiterOnError(
                        "Total msg parts reached but parts were not sequential", msgPart
                    ) }
                )
            }
        }
    }

    private fun decodeByteBuffer(byteBuffer: ByteBuffer): String {
        return StandardCharsets.UTF_8.decode(byteBuffer).toString()
    }

    private fun removeRecruiterOnError(log: String){
        repository.logger.errorRecruiter(recruiterId, "$log, removing Recruiter")
        RemoveRecruiterEvent(repository, recruiterId).handle()
    }

    private fun removeRecruiterOnError(log: String, peerMsg: PeerMsg){
        repository.logger.errorIncomingP2PMsg(recruiterId, peerMsg, "$log, removing Recruiter")
        RemoveRecruiterEvent(repository, recruiterId).handle()
    }
}