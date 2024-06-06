package com.todo.todo.worker.socket.messages

import com.google.gson.annotations.SerializedName
import com.todo.todo.worker.socket.messages.abstractions.DirectMsg
import com.todo.todo.worker.socket.messages.abstractions.SocketMsgType
import com.todo.todo.worker.socket.messages.data.AgnosticIceCandidate
import com.todo.todo.worker.SharedRepository

class TeamDetailsMsg: DirectMsg() {
    @SerializedName("candidate") var candidate: AgnosticIceCandidate? = null

    companion object {
        fun send(
            repository: SharedRepository,
            to: String,
            candidate: AgnosticIceCandidate,
            onAck: (ack: Boolean) -> Any
        ){
            val msg = TeamDetailsMsg()
            msg.to = to
            msg.from = repository.socket.id()
            msg.candidate = candidate
            repository.socket.sendMsg(SocketMsgType.TEAM_DETAILS, repository.parser.toJson(msg), onAck)
        }
    }
}