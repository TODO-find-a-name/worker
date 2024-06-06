package com.todo.todo.worker.events.socket

import com.todo.todo.worker.socket.messages.TeamDetailsMsg
import com.todo.todo.worker.SharedRepository
import com.todo.todo.worker.events.SocketEvent

class TeamDetailsMsgEvent(repository: SharedRepository, private val payload:String) : SocketEvent(repository) {

    override fun handleImpl() {
        repository.parser.fromJson(payload, TeamDetailsMsg::class.java).ifPresent { msg ->
            msg.from?.let { from ->
                repository.recruiters[from]?.let { recruiter ->
                    msg.candidate?.let { agnosticCandidate ->
                        agnosticCandidate.toConcrete().ifPresent {
                            recruiter.peer.addIceCandidate(it)
                        }
                    }
                }
            }
        }
    }

}