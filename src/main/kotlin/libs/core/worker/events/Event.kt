package libs.core.worker.events

import libs.core.worker.Repository

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
