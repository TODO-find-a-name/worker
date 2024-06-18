package libs.core.worker.events

import libs.core.worker.Repository

class RemoveRecruiterEvent(
    repository: Repository, private val id: String
) : Event(repository) {

    override fun handleImpl() {
        println("removing recruiter")
        repository.removeRecruiter(id)
    }

}