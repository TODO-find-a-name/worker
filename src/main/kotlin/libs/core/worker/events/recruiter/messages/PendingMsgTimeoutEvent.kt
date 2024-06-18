package libs.core.worker.events.recruiter.messages

import libs.core.worker.Repository
import libs.core.worker.events.Event
import libs.core.worker.events.RemoveRecruiterEvent

class PendingMsgTimeoutEvent(
    repository: Repository, private val recruiterId: String, private val msgId: String
) : Event(repository) {

    override fun handleImpl() {
        repository.recruiters[recruiterId]?.let { recruiter ->
            recruiter.pendingMessages[msgId]?.let { pendingMsg ->
                if(pendingMsg.parts.size < pendingMsg.total){
                    RemoveRecruiterEvent(repository, recruiterId).handle()
                }
            }
        }
    }

}