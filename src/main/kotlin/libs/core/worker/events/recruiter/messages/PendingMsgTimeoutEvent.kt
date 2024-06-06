package libs.core.worker.events.recruiter.messages

import libs.core.worker.Repository
import libs.core.worker.events.RecruiterEvent
import libs.core.worker.Recruiter

class PendingMsgTimeoutEvent(
    repository: Repository, recruiterId: String, private val msgId: Int
) : RecruiterEvent(PendingMsgTimeoutEvent::class.simpleName.toString(), repository, recruiterId) {

    override fun handleImpl(recruiter: Recruiter) {
        recruiter.pendingMessages[msgId]?.let { pendingMsg ->
            if(pendingMsg.parts.size < pendingMsg.total){
                repository.removeRecruiter(recruiter, "Timeout reached form multipart msg with ID $msgId")
            }
        }
    }

}