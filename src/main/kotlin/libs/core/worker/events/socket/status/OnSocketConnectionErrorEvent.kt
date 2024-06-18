package libs.core.worker.events.socket.status

import libs.core.worker.Repository
import libs.core.worker.events.Event
import libs.core.worker.utils.LoggerLvl

class OnSocketConnectionErrorEvent(repository: Repository) : Event(repository) {

    override fun handleImpl() {
        repository.logger.logRegular(LoggerLvl.LOW, "Error while connecting to Broker")
        repository.isRunning = false
        repository.recruiters.keys.forEach{
            repository.removeRecruiter(it)
        }
        repository.viewCallbacks.onBrokerConnectionError()
    }

}