package libs.core.worker.socket

import libs.core.worker.events.socket.incoming.IncomingInterviewProposalMsgEvent
import libs.core.worker.events.socket.incoming.IncomingTeamDetailsMsgEvent
import libs.core.worker.events.socket.incoming.IncomingTeamProposalMsgEvent
import libs.core.worker.socket.messages.abstractions.SocketMsgType
import libs.core.worker.SharedRepository
import libs.core.worker.events.Event
import libs.core.worker.utils.LoggerLvl
import io.socket.client.IO
import io.socket.engineio.client.transports.WebSocket
import libs.core.worker.events.RemoveRecruiterEvent
import java.net.URI
import io.socket.client.Socket as SocketIo

class SocketCreator {
    companion object {

        private const val WORKER_ROLE = "W"

        fun createSocketIO(repository: SharedRepository): SocketIo {
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

        private fun registerEventListeners(socket: SocketIo, repository: SharedRepository){
            socket.on(SocketIo.EVENT_CONNECT) {
                repository.logger.logRegular(LoggerLvl.LOW, "Connected to Broker as " + socket.id())
                reset(repository)
                repository.isRunning = true
                repository.viewCallbacks.onBrokerConnectionEstablished()
            }
            socket.on(SocketIo.EVENT_CONNECT_ERROR) {
                repository.logger.logRegular(LoggerLvl.LOW, "Error while connecting to Broker")
                reset(repository)
                repository.viewCallbacks.onBrokerConnectionError()
            }
            socket.on(SocketIo.EVENT_DISCONNECT) {
                repository.logger.logRegular(LoggerLvl.LOW, "Disconnection from Broker")
                reset(repository)
                repository.viewCallbacks.onBrokerDisconnection()
            }
        }

        private fun reset(repository: SharedRepository){
            repository.isRunning = false
            repository.recruiters.keys.forEach{
                RemoveRecruiterEvent(repository, it).handle()
            }
        }

        private fun registerMsgListeners(socket: SocketIo, repository: SharedRepository){
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
            socket: SocketIo,
            repository: SharedRepository,
            msgType: String,
            eventStrategy: (payload: String) -> Event
        ){
            socket.on(msgType){
                if(it !== null && it.size == 1){
                    repository.logger.logRegular(
                        LoggerLvl.COMPLETE,
                        "Incoming " + SocketMsgType.toHumanReadableMsgType(msgType) + " socket msg"
                    )
                    eventStrategy(it[0].toString()).handle()
                } else {
                    repository.logger.errorSocket(
                        SocketMsgType.toHumanReadableMsgType(msgType),
                        "Incoming invalid msg, discarding it:\n " +
                                "payload == null is " + (it == null) +
                                ", payload.size == 1 is " + (it.size == 1)
                    )
                }
            }
        }

    }
}