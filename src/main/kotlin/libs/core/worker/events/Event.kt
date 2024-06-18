package libs.core.worker.events

import libs.core.worker.SharedRepository

abstract class Event(val repository: SharedRepository) {

    fun handle(){
        repository.lock.execute {
            if(repository.isRunning){
                handleImpl()
            }
        }
    }

    abstract fun handleImpl()

}
