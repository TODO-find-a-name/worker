package libs.core.worker.events

import libs.core.worker.SharedRepository

class RemoveRecruiterEvent(
    repository: SharedRepository, private val id: String
) : Event(repository) {

    override fun handleImpl() {
        println("removing recruiter")
        repository.removeRecruiter(id)
    }

}