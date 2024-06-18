package libs.core.worker.events.recruiter.negotiation

import libs.core.worker.SharedRepository
import libs.core.worker.events.Event

class CreateAnswerNegotiationEvent(repository: SharedRepository, val recruiterId: String) : Event(repository) {

    override fun handleImpl() {
        repository.recruiters[recruiterId]?.createAnswer()
    }

}