package com.todo.todo.worker.events.socket

import com.todo.todo.worker.socket.messages.TeamApplicationMsg
import com.todo.todo.worker.socket.messages.TeamProposalMsg
import com.todo.todo.worker.socket.messages.abstractions.SocketMsgType
import com.todo.todo.worker.SharedRepository
import com.todo.todo.worker.events.SocketEvent

class TeamProposalMsgEvent(
    repository: SharedRepository, private val payload:String
) : SocketEvent(repository) {

    override fun handleImpl() {
        repository.parser.fromJson(payload, TeamProposalMsg::class.java).ifPresent{ incomingMsg ->
            incomingMsg.from?.let { recruiterId ->
                if(incomingMsg.ignore.contains(repository.socket.id()) || repository.recruiters.contains(recruiterId)){
                    println("ignoring team proposal msg") // TODO logger
                } else {
                    val outgoingMsg = TeamApplicationMsg()
                    outgoingMsg.from = repository.socket.id()
                    outgoingMsg.to = recruiterId
                    repository.socket.sendMsg(SocketMsgType.TEAM_APPLICATION, repository.parser.toJson(outgoingMsg)) {
                        ack: Boolean ->
                            println("ack team application: $ack") // TODO if ack is false
                    }
                }
            }
        }
    }

}