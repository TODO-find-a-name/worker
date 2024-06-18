package libs.core.worker

import libs.core.ViewCallbacks
import libs.core.worker.utils.WorkerSettings
import libs.common.module.WorkerModulePack

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