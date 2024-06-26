package libs.core.worker.events

import libs.core.worker.Repository
import libs.core.worker.Recruiter

class RemoveRecruiterEvent(
    repository: Repository, recruiterId: String, private val log: String
) : RecruiterEvent(RemoveRecruiterEvent::class.simpleName.toString(), repository, recruiterId) {

    override fun handleImpl(recruiter: Recruiter) {
        repository.removeRecruiter(recruiter, log)
    }
}