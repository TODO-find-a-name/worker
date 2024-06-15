package com.todo.todo.worker.events

import com.todo.todo.worker.SharedRepository

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
