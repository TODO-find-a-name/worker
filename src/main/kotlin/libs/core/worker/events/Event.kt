package libs.core.worker.events

import libs.core.worker.Repository
import libs.core.worker.recruiter.Recruiter

abstract class Event(val repository: Repository) {

    fun handle(){
        repository.lock.execute {
            if(repository.isRunning){
                handleImpl()
            }
        }
    }

    abstract fun handleImpl()

}

abstract class RecruiterEvent(repository: Repository, val recruiterId: String): Event(repository){

    override fun handleImpl(){
        repository.recruiters[recruiterId]?.let {
            handleImpl(it)
        }
    }

    abstract fun handleImpl(recruiter: Recruiter)

}
