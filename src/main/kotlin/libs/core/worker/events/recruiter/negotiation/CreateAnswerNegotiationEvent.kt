package libs.core.worker.events.recruiter.negotiation

import libs.core.worker.Recruiter
import libs.core.worker.Repository
import libs.core.worker.events.RecruiterEvent
import libs.core.worker.utils.LoggerLvl

class CreateAnswerNegotiationEvent(
    repository: Repository, recruiterId: String
) : RecruiterEvent(CreateAnswerNegotiationEvent::class.simpleName.toString(), repository, recruiterId) {

    override fun handleImpl(recruiter: Recruiter) {
        repository.logger.log(LoggerLvl.COMPLETE, "Creating answer", "Recruiter $recruiterId")
        recruiter.createAnswer()
    }

}