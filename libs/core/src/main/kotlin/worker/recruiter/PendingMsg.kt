package com.todo.todo.worker.recruiter

import messages.PeerMsg
import messages.PeerMsgPartChecked
import java.time.LocalDateTime
import java.util.*

class PendingMsg(val total: Long, val tms: LocalDateTime) {

    val parts: MutableMap<Long, PeerMsgPartChecked> = mutableMapOf()

    fun mergeMessages(): Optional<PeerMsg> {
        // assumes total has been checked
        var i: Long = 0
        val sortedList = parts.toSortedMap().values
        if(sortedList.all { it.part == i++ }){
            val payload = sortedList.map { it.payload }.reduce { acc, payloadPart -> acc + payloadPart }
            val base = parts[0]!!
            return Optional.of(
                PeerMsg(
                base.msgId,
                base.msgType,
                base.module,
                base.jobId,
                base.jobType,
                payload
            )
            )
        } else {
            return Optional.empty()
        }
    }
}