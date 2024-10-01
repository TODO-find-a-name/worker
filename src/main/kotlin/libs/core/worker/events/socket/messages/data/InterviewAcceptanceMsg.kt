package libs.core.worker.events.socket.messages.data

import libs.core.worker.Repository
import libs.core.worker.events.socket.messages.data.abstractions.SessionDescriptionMsg
import libs.core.worker.events.socket.messages.data.abstractions.SocketMsgType
import libs.core.worker.events.socket.messages.data.adapters.AgnosticRTCSessionDescription

class InterviewAcceptanceMsg: SessionDescriptionMsg() {
    companion object {
        fun send(
            repository: Repository,
            sessionToken: String,
            to: String,
            sessionDescription: AgnosticRTCSessionDescription,
            onAck: (ack: Boolean) -> Any
        ){
            val msg = SessionDescriptionMsg()
            msg.sessionToken = sessionToken
            msg.to = to
            msg.from = repository.socket.id()
            msg.sessionDescription = sessionDescription
            repository.socket.sendMsg(SocketMsgType.INTERVIEW_ACCEPTANCE, repository.parser.toJson(msg), onAck)
        }
    }
}