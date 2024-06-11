package com.todo.todo.worker.socket.messages

import com.todo.todo.worker.SharedRepository
import com.todo.todo.worker.socket.messages.abstractions.DirectMsg
import com.todo.todo.worker.socket.messages.abstractions.SocketMsgType

class TeamApplicationMsg: DirectMsg(){
    companion object {
        fun send(
            repository: SharedRepository,
            to: String,
            onAck: (ack: Boolean) -> Any
        ){
            val msg = TeamApplicationMsg()
            msg.to = to
            msg.from = repository.socket.id()
            repository.socket.sendMsg(SocketMsgType.TEAM_APPLICATION, repository.parser.toJson(msg), onAck)
        }
    }
}