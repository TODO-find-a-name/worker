package libs.core.worker.events.recruiter.messages

import libs.core.worker.Repository
import libs.core.worker.utils.PendingMsg
import libs.core.worker.Recruiter
import libs.core.worker.utils.LoggerLvl
import libs.common.messages.PeerMsg
import libs.common.messages.PeerMsgPartParsable
import libs.common.messages.PeerMsgPart
import libs.core.worker.events.RecruiterEvent
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

class IncomingRecruiterMsgPartEvent(
    repository: Repository, recruiterId: String, private val msg: ByteBuffer
) : RecruiterEvent(IncomingRecruiterMsgPartEvent::class.simpleName.toString(), repository, recruiterId) {

    override fun handleImpl(recruiter: Recruiter) {
        val decoded = StandardCharsets.UTF_8.decode(msg).toString()
        repository.parser.fromJson(decoded, PeerMsgPartParsable::class.java).ifPresentOrElse(
            { msgPartParsed -> msgPartParsed.toChecked().ifPresentOrElse(
                { handleCheckedMsgPart(it, recruiter) },
                { removeRecruiterOnError("Parsed msg part does not contain all necessary values", recruiter) }
            ) },
            { removeRecruiterOnError("$decoded\nMsg part could not be parsed", recruiter) }
        )
    }

    private fun handleCheckedMsgPart(peerMsgPart: PeerMsgPart, recruiter: Recruiter){
        logP2PIncomingPartialMsg(peerMsgPart)
        if(peerMsgPart.total == 1){
            redirectCompleteMsgToModule(peerMsgPart, recruiter)
        } else {
            handleMsgPart(peerMsgPart, recruiter)
        }
    }

    private fun redirectCompleteMsgToModule(msg: PeerMsg, recruiter: Recruiter) {
        logP2PIncomingCompleteMsg(msg, recruiter)
        recruiter.module.incomingPeerMsg(recruiterId, msg)
    }

    private fun handleMsgPart(msgPart: PeerMsgPart, recruiter: Recruiter){
        var pendingMsg: PendingMsg? = recruiter.pendingMessages[msgPart.msgId]
        if(pendingMsg == null){
            pendingMsg = PendingMsg(msgPart.total, repository, recruiterId, msgPart.msgId)
            recruiter.pendingMessages[msgPart.msgId] = pendingMsg
        }
        if(pendingMsg.total != msgPart.total){
            removeRecruiterOnError("Received msg part with discording total", msgPart, recruiter)
        } else if(pendingMsg.parts[msgPart.part] !== null){
            removeRecruiterOnError("Duplicate msg part", msgPart, recruiter)
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

    private fun removeRecruiterOnError(log: String, recruiter: Recruiter){
        repository.removeRecruiter(recruiter, log)
    }

    private fun removeRecruiterOnError(log: String, msgPart: PeerMsgPart, recruiter: Recruiter){
        removeRecruiterOnError(log + " for msg ID " + msgPart.msgId, recruiter)
    }

    private fun logP2PIncomingCompleteMsg(peerMsg: PeerMsg, recruiter: Recruiter){
        logP2PIncomingMsg(
            LoggerLvl.MID,
            peerMsg,
            "Forwarding incoming msg from Recruiter to module " + recruiter.module.id()
        )
    }

    private fun logP2PIncomingPartialMsg(peerMsgPart: PeerMsgPart){
        logP2PIncomingMsg(
            LoggerLvl.COMPLETE,
            peerMsgPart,
            "Received p2p msg part with index " + peerMsgPart.part.toString() + " (total: " + peerMsgPart.total + ")"
        )
    }

    private fun logP2PIncomingMsg(lvl: LoggerLvl, peerMsg: PeerMsg, log: String){
        repository.logger.log(
            lvl,
            log,
            "Incoming P2P msg ${peerMsg.msgType} from $recruiterId",
            "Msg ID: ${peerMsg.msgId}"
        )
    }

}