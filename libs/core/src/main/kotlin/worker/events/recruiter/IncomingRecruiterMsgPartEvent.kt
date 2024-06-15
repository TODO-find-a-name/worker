package com.todo.todo.worker.events.recruiter

import com.todo.todo.worker.SharedRepository
import com.todo.todo.worker.events.Event
import com.todo.todo.worker.recruiter.PendingMsg
import com.todo.todo.worker.recruiter.Recruiter
import com.todo.todo.worker.utils.LoggerLvl
import messages.PeerMsg
import messages.PeerMsgPart
import messages.PeerMsgPartChecked
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime

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
        if(peerMsgPart.total == 1.toLong()){
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
            pendingMsg = PendingMsg(msgPart.total, LocalDateTime.now())
        }
        if(pendingMsg.total != msgPart.total){
            removeRecruiterOnError("Received msg part with discording total for msg", msgPart, recruiter)
        } else if(pendingMsg.parts[msgPart.part] !== null){
            removeRecruiterOnError("Duplicate msg part for msg", msgPart, recruiter)
        } else {
            pendingMsg.parts[msgPart.part] = msgPart
            if(pendingMsg.parts.size.toLong() == pendingMsg.total){
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

    private fun removeRecruiterOnError(log: String){
        repository.logger.errorRecruiter(recruiterId, "$log, removing Recruiter")
        repository.removeRecruiter(recruiterId)
    }

    private fun removeRecruiterOnError(log: String, peerMsg: PeerMsg, recruiter: Recruiter){
        repository.logger.errorIncomingP2PMsg(recruiterId, peerMsg, "$log, removing Recruiter")
        repository.removeRecruiter(recruiter)
    }
}