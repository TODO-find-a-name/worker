package libs.core.worker.events

import libs.core.worker.Repository
import libs.core.worker.recruiter.Recruiter

class RemoveRecruiterEvent(repository: Repository, recruiterId: String) : RecruiterEvent(repository, recruiterId) {

    override fun handleImpl(recruiter: Recruiter) {
        // TODO log
        repository.recruiters.remove(recruiterId)
    }
}