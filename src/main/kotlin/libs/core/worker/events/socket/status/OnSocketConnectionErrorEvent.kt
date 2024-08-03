package libs.core.worker.events.socket.status

import libs.core.worker.Repository
import libs.core.worker.events.Event
import libs.core.worker.gui.BrokerConnectionErrorGuiMsg
import libs.core.worker.utils.LoggerLvl

class OnSocketConnectionErrorEvent(
    repository: Repository
) : Event(OnSocketConnectionErrorEvent::class.simpleName.toString(), repository) {

    override fun handleImpl() {
        repository.logger.log(LoggerLvl.LOW, "Error while connecting to Broker")
        repository.isRunning = false
        repository.recruiters.keys.forEach{
            repository.removeRecruiter(it, "Error while connecting to Broker")
        }

        repository.guiSocket.send(BrokerConnectionErrorGuiMsg(), repository.parser)
    }

}