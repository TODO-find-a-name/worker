package libs.core.worker.events.socket.status

import io.socket.client.Socket as SocketIo
import libs.core.worker.Repository
import libs.core.worker.events.Event
import libs.core.worker.utils.LoggerLvl

class OnSocketConnectionEvent(repository: Repository, private val socket: SocketIo) : Event(repository) {

    override fun handleImpl() {
        repository.logger.logRegular(LoggerLvl.LOW, "Connected to Broker as " + socket.id())
        repository.recruiters.keys.forEach{
            repository.removeRecruiter(it)
        }
        repository.viewCallbacks.onBrokerConnectionEstablished()
    }

}