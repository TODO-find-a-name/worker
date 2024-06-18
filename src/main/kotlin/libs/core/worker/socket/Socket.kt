package libs.core.worker.socket

import libs.core.worker.SharedRepository
import io.socket.client.Ack
import io.socket.client.Socket as SocketIo

class Socket(repository: SharedRepository) {

    private val socketIO: SocketIo = SocketCreator.createSocketIO(repository)

    fun isConnected(): Boolean {
        return socketIO.connected()
    }

    fun id(): String {
        return socketIO.id()
    }

    fun connect() {
        socketIO.connect()
    }

    fun disconnect() {
        socketIO.disconnect()
    }

    fun sendMsg(type: String, msg: String, onBrokerResponse: (ack: Boolean) -> Any){
        socketIO.emit(type, msg, Ack { args ->
            if(args.size != 1 || args[0] !is Boolean){
                onBrokerResponse(false)
            } else {
                onBrokerResponse(args[0] as Boolean)
            }
        })
    }

}