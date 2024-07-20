package libs.core.worker.events

import libs.core.worker.Recruiter
import libs.core.worker.Repository

abstract class Event(private val eventName: String, val repository: Repository) {

    fun handle(){
        repository.lock.execute(eventName) {
            if(repository.isRunning){
                handleImpl()
            }
        }
    }

    abstract fun handleImpl()

}

abstract class RecruiterEvent(
    eventName: String, repository: Repository, val recruiterId: String
): Event(eventName, repository){

    override fun handleImpl(){
        repository.recruiters[recruiterId]?.let {
            handleImpl(it)
        }
    }

    abstract fun handleImpl(recruiter: Recruiter)

}
