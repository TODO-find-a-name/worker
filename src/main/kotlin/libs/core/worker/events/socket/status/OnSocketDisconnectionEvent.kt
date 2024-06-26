package libs.core.worker.events.socket.status

import libs.core.worker.Repository
import libs.core.worker.events.Event
import libs.core.worker.utils.LoggerLvl

class OnSocketDisconnectionEvent(
    repository: Repository
) : Event(OnSocketDisconnectionEvent::class.simpleName.toString(), repository) {

    override fun handleImpl() {
        repository.logger.log(LoggerLvl.LOW, "Disconnection from Broker")
        repository.isRunning = false
        repository.recruiters.keys.forEach{
            repository.removeRecruiter(it, "Disconnection from Broker")
        }
        repository.viewCallbacks.onBrokerDisconnection()
    }

}