package libs.core.worker.events.socket.status

import libs.core.worker.Repository
import libs.core.worker.events.Event
import libs.core.worker.gui.BrokerConnectedGuiMsg
import libs.core.worker.utils.LoggerLvl

class OnSocketAuthenticatedEvent(
    repository: Repository
) : Event(OnSocketAuthenticatedEvent::class.simpleName.toString(), repository){
    override fun handleImpl() {
        repository.logger.log(LoggerLvl.LOW, "Authentication successfully completed")
        repository.recruiters.keys.forEach{
            repository.removeRecruiter(it, "New connection to Broker")
        }
        repository.guiSocket.send(BrokerConnectedGuiMsg(), repository.parser)
    }

}