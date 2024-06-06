package libs.core.worker.events.socket.messages.data

import libs.core.worker.Repository
import libs.core.worker.events.socket.messages.data.abstractions.DirectMsg
import libs.core.worker.events.socket.messages.data.abstractions.SocketMsgType

class TeamApplicationMsg: DirectMsg(){
    companion object {
        fun send(
            repository: Repository,
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