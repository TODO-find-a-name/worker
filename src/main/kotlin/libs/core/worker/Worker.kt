package libs.core.worker

import libs.common.module.WorkerModulePack
import libs.core.worker.utils.WorkerSettings

class Worker(settings: WorkerSettings, modulePacks: List<WorkerModulePack>) {

    private val repository: Repository = Repository(settings, modulePacks)

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