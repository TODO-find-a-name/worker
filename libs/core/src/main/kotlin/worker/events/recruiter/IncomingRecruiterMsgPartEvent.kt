package com.todo.todo.worker.events.recruiter

import com.todo.todo.worker.SharedRepository
import com.todo.todo.worker.events.RecruiterEvent
import com.todo.todo.worker.events.general.RemoveRecruiterEvent
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
) : RecruiterEvent(repository) {

    override fun handleImpl() {
        repository.parser.fromJson(decodeByteBuffer(msg), PeerMsgPart::class.java).ifPresentOrElse(
            {msgPartParsed ->
                msgPartParsed.toChecked().ifPresentOrElse(
                    {
                        val recruiter = repository.recruiters[recruiterId]
                        if(recruiter == null){
                            // TODO recruiter not found, error, ignore
                        } else {
                            repository.logger.logP2PIncomingPart(LoggerLvl.HIGH, it, recruiterId)
                            if(it.total == 1.toLong()){
                                redirectCompleteMsgToModule(it)
                            } else {
                                handleMsgPart(it, recruiter)
                            }
                        }
                    },
                    { onError("Parsed msg part does not contain all necessary values") }
                )
            },
            { onError("Msg part could not be parsed") }
        )
    }

    private fun decodeByteBuffer(byteBuffer: ByteBuffer): String {
        return StandardCharsets.UTF_8.decode(byteBuffer).toString()
    }

    private fun redirectCompleteMsgToModule(msg: PeerMsg) {
        repository.logger.logP2PIncomingComplete(LoggerLvl.MID, msg, recruiterId)
        val module = repository.modules[msg.module]
        if(module == null){
            onError("Requested module ${msg.module} not found")
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
            onError("Received msg part with discording total for msg ${msgPart.msgId}")
            return
        }
        if(pendingMsg.parts[msgPart.part] !== null){
            onError("Duplicate msg part for msg ${msgPart.msgId}")
            return
        }
        pendingMsg.parts[msgPart.part] = msgPart
        if(pendingMsg.parts.size.toLong() == pendingMsg.total){
            pendingMsg.mergeMessages().ifPresentOrElse(
                {redirectCompleteMsgToModule(it)},
                {onError("Total msg parts reached but parts were not sequential for msg ${msgPart.msgId}")}
            )
        }
    }

    private fun onError(msg: String){
        // recruiter id on msg log
        // msg + ", enqueueing RemoveRecruiterEvent"
        repository.eventQueues.general.add(RemoveRecruiterEvent(repository, recruiterId))
    }
}