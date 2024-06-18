package libs.core.worker.recruiter

import libs.core.worker.SharedRepository
import libs.common.messages.PeerMsg
import libs.common.messages.PeerMsgPartChecked
import java.util.*

class PendingMsg(val total: Int, val repository: SharedRepository, val recruiterId: String) {

    private val timeoutTimer = Timer()
    val parts: MutableMap<Int, PeerMsgPartChecked> = mutableMapOf()

    init {
        timeoutTimer.schedule(
            object : TimerTask() {
                override fun run() {
                    repository.lock.execute {
                        if(repository.isRunning){
                            if(parts.size < total && repository.recruiters.contains(recruiterId)){
                                repository.removeRecruiter(recruiterId)
                            }
                        }
                    }
                }
            },
            repository.settings.recruitmentTimeoutMs
        )
    }

    fun cancelTimeout(){
        timeoutTimer.cancel()
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