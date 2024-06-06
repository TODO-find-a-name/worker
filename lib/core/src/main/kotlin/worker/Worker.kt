package com.todo.todo.worker

import com.todo.todo.ViewCallbacks
import com.todo.todo.worker.utils.WorkerSettings

class Worker(settings: WorkerSettings, viewCallbacks: ViewCallbacks) {

    private val repository: SharedRepository = SharedRepository(settings, viewCallbacks)

    fun connect() {
        if(!repository.loop.isPresent && !repository.socket.isConnected()){
            repository.socket.connect()
        }
    }

    fun disconnect() {
        if(repository.loop.isPresent && repository.socket.isConnected()){
            repository.socket.disconnect()
        }
    }

}