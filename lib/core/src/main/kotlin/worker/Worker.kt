package com.todo.todo.worker

import com.todo.todo.ViewCallbacks
import com.todo.todo.worker.utils.WorkerSettings
import module.WorkerModulePack

class Worker(settings: WorkerSettings, modulePacks: List<WorkerModulePack>, viewCallbacks: ViewCallbacks) {

    private val repository: SharedRepository = SharedRepository(settings, modulePacks, viewCallbacks)

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