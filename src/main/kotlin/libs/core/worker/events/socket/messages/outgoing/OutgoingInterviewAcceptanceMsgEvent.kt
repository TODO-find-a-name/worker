package libs.core.worker.events.socket.messages.outgoing

import libs.core.worker.Repository
import libs.core.worker.events.Event
import libs.core.worker.events.RemoveRecruiterEvent
import libs.core.worker.events.socket.messages.data.InterviewAcceptanceMsg
import libs.core.worker.events.socket.messages.data.abstractions.SocketMsgType
import libs.core.worker.events.socket.messages.data.adapters.AgnosticRTCSessionDescription
import libs.core.worker.utils.LoggerLvl
import dev.onvoid.webrtc.RTCSessionDescription

class OutgoingInterviewAcceptanceMsgEvent(
    repository: Repository, private val recruiterId: String, private val sessionDescription: RTCSessionDescription
) : Event(repository) {

    override fun handleImpl() {
        AgnosticRTCSessionDescription.adaptConcrete(sessionDescription).ifPresent{
            repository.logger.logSocketOutgoing(
                LoggerLvl.MID,
                SocketMsgType.INTERVIEW_ACCEPTANCE_NAME,
                recruiterId,
                "Sending session description"
            )
            InterviewAcceptanceMsg.send(repository, recruiterId, it){ ack ->
                repository.logger.logSocketOutgoingAck(
                    LoggerLvl.COMPLETE, SocketMsgType.INTERVIEW_ACCEPTANCE_NAME, recruiterId, ack
                )
                if(!ack){
                    println("interview acceptance ack false") // TODO
                    RemoveRecruiterEvent(repository, recruiterId).handle()
                }
            }
        }
    }

}