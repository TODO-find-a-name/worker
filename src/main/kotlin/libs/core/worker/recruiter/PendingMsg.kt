package libs.core.worker.recruiter

import libs.core.worker.Repository
import libs.common.messages.PeerMsg
import libs.common.messages.PeerMsgPart
import libs.core.worker.events.recruiter.messages.PendingMsgTimeoutEvent
import libs.core.worker.utils.scheduleEvent
import java.util.*

class PendingMsg(val total: Int, val repository: Repository, val recruiterId: String, msgId: String) {

    private val timer = Timer()
    val parts: MutableMap<Int, PeerMsgPart> = mutableMapOf()

    init {
        timer.scheduleEvent(
            PendingMsgTimeoutEvent(repository, recruiterId, msgId), repository.settings.recruitmentTimeoutMs
        )
    }

    fun cancelTimeout(){
        timer.cancel()
    }

    fun mergeMessages(): Optional<PeerMsg> {
        // assumes total has been checked
        var i = 0
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