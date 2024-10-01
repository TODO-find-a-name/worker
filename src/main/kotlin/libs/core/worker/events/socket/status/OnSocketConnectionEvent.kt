package libs.core.worker.events.socket.status

import libs.core.worker.Repository
import libs.core.worker.events.Event
import libs.core.worker.utils.LoggerLvl
import io.socket.client.Socket as SocketIo

class OnSocketConnectionEvent(
    repository: Repository, private val socket: SocketIo
) : Event(OnSocketConnectionEvent::class.simpleName.toString(), repository) {

    override fun handleImpl() {
        repository.logger.log(LoggerLvl.LOW, "Connected to Broker as " + socket.id() + ", waiting for authentication completion")
    }

}