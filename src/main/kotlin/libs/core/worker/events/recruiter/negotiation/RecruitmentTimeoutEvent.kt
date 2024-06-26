package libs.core.worker.events.recruiter.negotiation

import libs.core.worker.Repository
import libs.core.worker.events.RecruiterEvent
import libs.core.worker.events.RemoveRecruiterEvent
import libs.core.worker.Recruiter

class RecruitmentTimeoutEvent(
    repository: Repository, recruiterId: String
) : RecruiterEvent(RecruitmentTimeoutEvent::class.simpleName.toString(), repository, recruiterId) {

    override fun handleImpl(recruiter: Recruiter) {
        if(!recruiter.isConnected){
            RemoveRecruiterEvent(repository, recruiterId, "Recruitment timeout").handle()
        }
    }

}