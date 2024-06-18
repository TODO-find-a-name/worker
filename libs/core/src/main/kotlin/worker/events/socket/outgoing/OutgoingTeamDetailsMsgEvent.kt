package com.todo.todo.worker.events.socket.outgoing

import com.todo.todo.worker.SharedRepository
import com.todo.todo.worker.events.Event
import com.todo.todo.worker.socket.messages.TeamDetailsMsg
import com.todo.todo.worker.socket.messages.abstractions.SocketMsgType
import com.todo.todo.worker.socket.messages.data.AgnosticIceCandidate
import com.todo.todo.worker.utils.LoggerLvl
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