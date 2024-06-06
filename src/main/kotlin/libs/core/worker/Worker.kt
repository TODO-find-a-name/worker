package libs.core.worker

import libs.common.ViewCallbacks
import libs.core.worker.utils.WorkerSettings
import libs.common.module.WorkerModulePack

class Worker(settings: WorkerSettings, modulePacks: List<WorkerModulePack>, viewCallbacks: ViewCallbacks) {

    private val repository: Repository = Repository(settings, modulePacks, viewCallbacks)

    fun connect() {
        if(!repository.isRunning && !repository.socket.isConnected()){
            repository.socket.connect()
        }
    }

    fun disconnect() {
        if(repository.isRunning && repository.socket.isConnected()){
            repository.socket.disconnect()
        }
    }

}