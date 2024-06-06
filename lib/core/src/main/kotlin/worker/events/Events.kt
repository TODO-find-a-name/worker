package com.todo.todo.worker.events

import com.todo.todo.worker.SharedRepository

abstract class Event(val repository: SharedRepository) {

    fun handle(){
        if(repository.isRunning){
            handleImpl()
        }
    }

    abstract fun handleImpl()

}

abstract class GeneralEvent(repository: SharedRepository) : Event(repository)
abstract class SocketEvent(repository: SharedRepository) : Event(repository)
abstract class RecruiterEvent(repository: SharedRepository) : Event(repository)
