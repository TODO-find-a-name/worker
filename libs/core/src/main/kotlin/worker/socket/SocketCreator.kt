package com.todo.todo.worker.socket

import com.todo.todo.worker.MainLoop
import com.todo.todo.worker.events.socket.InterviewProposalMsgEvent
import com.todo.todo.worker.events.socket.TeamDetailsMsgEvent
import com.todo.todo.worker.events.socket.TeamProposalMsgEvent
import com.todo.todo.worker.socket.messages.abstractions.SocketMsgType
import com.todo.todo.worker.SharedRepository
import com.todo.todo.worker.events.SocketEvent
import com.todo.todo.worker.utils.LoggerLvl
import io.socket.client.IO
import io.socket.engineio.client.transports.WebSocket
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.net.URI
import java.util.*
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

        @OptIn(DelicateCoroutinesApi::class)
        private fun registerEventListeners(socket: SocketIo, repository: SharedRepository){
            socket.on(SocketIo.EVENT_CONNECT) {
                repository.logger.logRegular(LoggerLvl.LOW, "Connected to Broker as " + socket.id())
                stopMainLoop(repository)
                repository.isRunning = true
                repository.loop = Optional.of(GlobalScope.async {
                    MainLoop.start(repository)
                })
                repository.viewCallbacks.onBrokerConnectionEstablished()
            }
            socket.on(SocketIo.EVENT_CONNECT_ERROR) {
                repository.logger.logRegular(LoggerLvl.LOW, "Error while connecting to Broker")
                stopMainLoop(repository)
                repository.viewCallbacks.onBrokerConnectionError()
            }
            socket.on(SocketIo.EVENT_DISCONNECT) {
                repository.logger.logRegular(LoggerLvl.LOW, "Disconnection from Broker")
                stopMainLoop(repository)
                repository.viewCallbacks.onBrokerDisconnection()
            }
        }

        private fun stopMainLoop(repository: SharedRepository){
            repository.isRunning = false
            if(repository.loop.isPresent){
                repository.loop.get().cancel()
                repository.loop = Optional.empty()
            }
            // TODO remove workers, disconnect, etc...
        }

        private fun registerMsgListeners(socket: SocketIo, repository: SharedRepository){
            registerSpecificMsgListener(socket, repository, SocketMsgType.TEAM_PROPOSAL) {
                TeamProposalMsgEvent(repository, it)
            }
            registerSpecificMsgListener(socket, repository, SocketMsgType.INTERVIEW_PROPOSAL) {
                InterviewProposalMsgEvent(repository, it)
            }
            registerSpecificMsgListener(socket, repository, SocketMsgType.TEAM_DETAILS) {
                TeamDetailsMsgEvent(repository, it)
            }
        }

        private fun registerSpecificMsgListener(
            socket: SocketIo,
            repository: SharedRepository,
            msgType: String,
            eventStrategy: (payload: String) -> SocketEvent
        ){
            socket.on(msgType){
                if(it !== null && it.size == 1){
                    repository.logger.logRegular(
                        LoggerLvl.COMPLETE,
                        "Incoming " + SocketMsgType.toHumanReadableMsgType(msgType) + " socket msg, enqueueing its event"
                    )
                    repository.eventQueues.socket.add(eventStrategy(it[0].toString()))
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