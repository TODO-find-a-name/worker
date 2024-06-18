package com.todo.todo.worker.events.socket.outgoing

import com.todo.todo.worker.SharedRepository
import com.todo.todo.worker.events.Event
import com.todo.todo.worker.socket.messages.TeamApplicationMsg
import com.todo.todo.worker.socket.messages.abstractions.SocketMsgType
import com.todo.todo.worker.utils.LoggerLvl

class OutgoingTeamApplicationMsgEvent(
    repository: SharedRepository, private val recruiterId: String
) : Event(repository) {

    override fun handleImpl() {
        repository.logger.logSocketOutgoing(
            LoggerLvl.HIGH, SocketMsgType.TEAM_APPLICATION_NAME, recruiterId, "Applying for team"
        )
        TeamApplicationMsg.send(repository, recruiterId){
            repository.logger.logSocketOutgoingAck(
                LoggerLvl.COMPLETE, SocketMsgType.TEAM_APPLICATION_NAME, recruiterId, it
            )
        }
    }

}