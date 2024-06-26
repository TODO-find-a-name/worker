package libs.core.worker.events.socket.messages.outgoing

import libs.core.worker.Repository
import libs.core.worker.events.socket.messages.data.TeamDetailsMsgParsable
import libs.core.worker.events.socket.messages.data.abstractions.SocketMsgType
import libs.core.worker.events.socket.messages.data.adapters.IceCandidateAdapter
import libs.core.worker.utils.LoggerLvl
import dev.onvoid.webrtc.RTCIceCandidate
import libs.core.worker.events.RecruiterEvent
import libs.core.worker.events.RemoveRecruiterEvent
import libs.core.worker.Recruiter

class OutgoingTeamDetailsMsgEvent(
    repository: Repository, recruiterId: String, private val candidate: RTCIceCandidate
) : RecruiterEvent(OutgoingTeamDetailsMsgEvent::class.simpleName.toString(), repository, recruiterId) {

    override fun handleImpl(recruiter: Recruiter) {
        if(recruiter.isConnected){
            repository.logger.logSocketOutgoing(
                LoggerLvl.COMPLETE,
                SocketMsgType.TEAM_DETAILS_NAME,
                recruiterId,
                "Recruiter already connected, ice candidate will not be sent"
            )
        } else {
            repository.logger.logSocketOutgoing(LoggerLvl.HIGH, SocketMsgType.TEAM_DETAILS_NAME, recruiterId, "Sending ice candidate")
            TeamDetailsMsgParsable.send(
                repository, recruiterId, IceCandidateAdapter.adaptConcrete(candidate)
            ){
                repository.logger.logSocketOutgoingAck(
                    LoggerLvl.COMPLETE, SocketMsgType.TEAM_DETAILS_NAME, recruiterId, it
                )
                if(!it){
                    RemoveRecruiterEvent(
                        repository,
                        recruiterId,
                        "Ack is false on " + SocketMsgType.TEAM_DETAILS_NAME + " sent msg",
                    ).handle()
                }
            }
        }
    }

}