package com.todo.todo.worker.events.socket.outgoing

import com.todo.todo.worker.SharedRepository
import com.todo.todo.worker.events.Event
import com.todo.todo.worker.events.general.RemoveRecruiterEvent
import com.todo.todo.worker.socket.messages.InterviewAcceptanceMsg
import com.todo.todo.worker.socket.messages.abstractions.SocketMsgType
import com.todo.todo.worker.socket.messages.data.AgnosticRTCSessionDescription
import com.todo.todo.worker.utils.LoggerLvl
import dev.onvoid.webrtc.RTCSessionDescription

class OutgoingInterviewAcceptanceMsgEvent(
    repository: SharedRepository, private val recruiterId: String, private val sessionDescription: RTCSessionDescription
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
                    println("interview acceptance ack false")
                    RemoveRecruiterEvent(repository, recruiterId).handle()
                }
            }
        }
    }

}