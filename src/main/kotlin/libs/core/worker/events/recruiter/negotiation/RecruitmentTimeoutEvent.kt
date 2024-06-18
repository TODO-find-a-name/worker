package libs.core.worker.events.recruiter.negotiation

import libs.core.worker.Repository
import libs.core.worker.events.Event
import libs.core.worker.events.RemoveRecruiterEvent

class RecruitmentTimeoutEvent(repository: Repository, private val recruiterId: String) : Event(repository) {

    override fun handleImpl() {
        repository.recruiters[recruiterId]?.let {
            if(!it.isConnected()){
                RemoveRecruiterEvent(repository, recruiterId).handle()
            }
        }
    }

}