package libs.core.worker.socket.messages

import libs.core.worker.socket.messages.abstractions.SessionDescriptionMsg
import libs.core.worker.socket.messages.abstractions.SocketMsgType
import libs.core.worker.socket.messages.data.AgnosticRTCSessionDescription
import libs.core.worker.SharedRepository

class InterviewAcceptanceMsg: SessionDescriptionMsg() {
    companion object {
        fun send(
            repository: SharedRepository,
            to: String,
            sessionDescription: AgnosticRTCSessionDescription,
            onAck: (ack: Boolean) -> Any
        ){
            val msg = SessionDescriptionMsg()
            msg.to = to
            msg.from = repository.socket.id()
            msg.sessionDescription = sessionDescription
            repository.socket.sendMsg(SocketMsgType.INTERVIEW_ACCEPTANCE, repository.parser.toJson(msg), onAck)
        }
    }
}