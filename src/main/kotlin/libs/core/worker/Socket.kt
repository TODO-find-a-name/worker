package libs.core.worker

import io.socket.client.Ack
import io.socket.client.IO
import io.socket.engineio.client.transports.WebSocket
import libs.core.worker.events.Event
import libs.core.worker.events.socket.messages.incoming.IncomingInterviewProposalMsgEvent
import libs.core.worker.events.socket.messages.incoming.IncomingTeamDetailsMsgEvent
import libs.core.worker.events.socket.messages.incoming.IncomingTeamProposalMsgEvent
import libs.core.worker.events.socket.status.OnSocketConnectionErrorEvent
import libs.core.worker.events.socket.status.OnSocketConnectionEvent
import libs.core.worker.events.socket.status.OnSocketDisconnectionEvent
import libs.core.worker.events.socket.messages.data.abstractions.SocketMsgType
import libs.core.worker.utils.LoggerLvl
import java.net.URI
import io.socket.client.Socket as SocketIo

class Socket(repository: Repository) {

    private val socketIO: SocketIo = createSocketIO(repository)

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

private const val WORKER_ROLE = "W"

private fun createSocketIO(repository: Repository): SocketIo {
    val socket: SocketIo = IO.socket(
        URI.create(repository.settings.brokerAddr),
        IO.Options.builder()
            .setTransports(arrayOf(WebSocket.NAME))
            .setQuery("role=" + WORKER_ROLE + "&organization=" + repository.settings.organization)
            .build()
    )
    registerEventListeners(socket, repository)
    registerMsgListeners(socket, repository)
    return socket
}

private fun registerEventListeners(socket: SocketIo, repository: Repository){
    socket.on(SocketIo.EVENT_CONNECT) {
        repository.isRunning = true
        OnSocketConnectionEvent(repository, socket).handle()
    }
    socket.on(SocketIo.EVENT_CONNECT_ERROR) {
        OnSocketConnectionErrorEvent(repository).handle()
    }
    socket.on(SocketIo.EVENT_DISCONNECT) {
        OnSocketDisconnectionEvent(repository).handle()
    }
}

private fun registerMsgListeners(socket: SocketIo, repository: Repository){
    registerSpecificMsgListener(socket, repository, SocketMsgType.TEAM_PROPOSAL) {
        IncomingTeamProposalMsgEvent(repository, it)
    }
    registerSpecificMsgListener(socket, repository, SocketMsgType.INTERVIEW_PROPOSAL) {
        IncomingInterviewProposalMsgEvent(repository, it)
    }
    registerSpecificMsgListener(socket, repository, SocketMsgType.TEAM_DETAILS) {
        IncomingTeamDetailsMsgEvent(repository, it)
    }
}

private fun registerSpecificMsgListener(
    socket: SocketIo, repository: Repository, msgType: String, eventStrategy: (payload: String) -> Event
){
    socket.on(msgType){
        if(it !== null && it.size == 1){
            repository.logger.log(
                LoggerLvl.COMPLETE,
                "Incoming " + SocketMsgType.toHumanReadableMsgType(msgType) + " socket msg"
            )
            eventStrategy(it[0].toString()).handle()
        } else {
            repository.logger.errorSocketMsg(
                SocketMsgType.toHumanReadableMsgType(msgType),
                "Incoming invalid msg, discarding it:\n " +
                        "payload == null is " + (it == null) +
                        ", payload.size == 1 is " + (it.size == 1)
            )
        }
    }
}