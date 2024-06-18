package libs.core.worker.events.recruiter.negotiation

import libs.core.worker.Repository
import libs.core.worker.events.Event

class CreateAnswerNegotiationEvent(repository: Repository, val recruiterId: String) : Event(repository) {

    override fun handleImpl() {
        repository.recruiters[recruiterId]?.createAnswer()
    }

}