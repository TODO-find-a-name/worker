package com.todo.todo.worker

import com.todo.todo.ViewCallbacks
import com.todo.todo.worker.utils.WorkerSettings
import module.WorkerModulePack

class Worker(settings: WorkerSettings, modulePacks: List<WorkerModulePack>, viewCallbacks: ViewCallbacks) {

    private val repository: SharedRepository = SharedRepository(settings, modulePacks, viewCallbacks)

    fun connect() {
        if(!repository.isRunning && !repository.socket.isConnected()){
            repository.isRunning = true
            repository.socket.connect()
        }
    }

    fun disconnect() {
        if(repository.isRunning && repository.socket.isConnected()){
            repository.isRunning = false
            repository.socket.disconnect()
        }
    }

}