package libs.core.worker.events.socket.outgoing

import libs.core.worker.SharedRepository
import libs.core.worker.events.Event
import libs.core.worker.socket.messages.TeamDetailsMsg
import libs.core.worker.socket.messages.abstractions.SocketMsgType
import libs.core.worker.socket.messages.data.AgnosticIceCandidate
import libs.core.worker.utils.LoggerLvl
import dev.onvoid.webrtc.RTCIceCandidate

class OutgoingTeamDetailsMsgEvent(
    repository: SharedRepository, private val recruiterId: String, private val candidate: RTCIceCandidate
) : Event(repository) {

    override fun handleImpl() {
        val recruiter = repository.recruiters[recruiterId]
        if(recruiter == null) {
            // TODO
        } else {
            if(recruiter.isConnected()){
                repository.logger.logSocketOutgoing(LoggerLvl.HIGH, SocketMsgType.TEAM_DETAILS_NAME, recruiterId, "Sending ice candidate")
                TeamDetailsMsg.send(
                    repository, recruiterId, AgnosticIceCandidate.adaptConcrete(candidate)
                ){
                    repository.logger.logSocketOutgoingAck(
                        LoggerLvl.COMPLETE, SocketMsgType.TEAM_APPLICATION_NAME, recruiterId, it
                    )
                    // TODO remove Recruiter if ack false
                }
            }
        }
    }

}