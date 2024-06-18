package libs.core.worker.socket.messages

import libs.core.worker.SharedRepository
import libs.core.worker.socket.messages.abstractions.DirectMsg
import libs.core.worker.socket.messages.abstractions.SocketMsgType

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