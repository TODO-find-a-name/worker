package libs.core.worker.events.socket.messages.outgoing

import dev.onvoid.webrtc.RTCSessionDescription
import libs.core.worker.Recruiter
import libs.core.worker.Repository
import libs.core.worker.events.RecruiterEvent
import libs.core.worker.events.RemoveRecruiterEvent
import libs.core.worker.events.socket.messages.data.InterviewAcceptanceMsg
import libs.core.worker.events.socket.messages.data.abstractions.SocketMsgType
import libs.core.worker.events.socket.messages.data.adapters.AgnosticRTCSessionDescription
import libs.core.worker.utils.LoggerLvl

class OutgoingInterviewAcceptanceMsgEvent(
    repository: Repository,
    recruiterId: String,
    private val sessionToken: String,
    private val sessionDescription: RTCSessionDescription
) : RecruiterEvent(OutgoingInterviewAcceptanceMsgEvent::class.simpleName.toString(), repository, recruiterId) {

    override fun handleImpl(recruiter: Recruiter) {
        AgnosticRTCSessionDescription.adaptConcrete(sessionDescription).ifPresent{
            repository.logger.logSocketOutgoing(
                LoggerLvl.MID,
                SocketMsgType.INTERVIEW_ACCEPTANCE_NAME,
                recruiterId,
                "Sending session description"
            )
            InterviewAcceptanceMsg.send(repository, sessionToken, recruiterId, it){ ack ->
                repository.logger.logSocketOutgoingAck(
                    LoggerLvl.COMPLETE, SocketMsgType.INTERVIEW_ACCEPTANCE_NAME, recruiterId, ack
                )
                if(!ack){
                    RemoveRecruiterEvent(
                        repository,
                        recruiterId,
                        "Ack is false on " + SocketMsgType.INTERVIEW_ACCEPTANCE_NAME + " sent msg"
                    ).handle()
                }
            }
        }
    }

}