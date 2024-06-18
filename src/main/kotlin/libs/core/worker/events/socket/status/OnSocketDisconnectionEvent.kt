package libs.core.worker.events.socket.status

import libs.core.worker.Repository
import libs.core.worker.events.Event
import libs.core.worker.events.RemoveRecruiterEvent
import libs.core.worker.utils.LoggerLvl

class OnSocketDisconnectionEvent(repository: Repository) : Event(repository) {

    override fun handleImpl() {
        repository.logger.logRegular(LoggerLvl.LOW, "Disconnection from Broker")
        repository.isRunning = false
        repository.recruiters.keys.forEach{
            RemoveRecruiterEvent(repository, it).handle()
        }
        repository.viewCallbacks.onBrokerDisconnection()
    }

}