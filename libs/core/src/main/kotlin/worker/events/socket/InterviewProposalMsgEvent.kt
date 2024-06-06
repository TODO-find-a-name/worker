package com.todo.todo.worker.events.socket

import com.todo.todo.worker.recruiter.Recruiter
import com.todo.todo.worker.socket.messages.InterviewProposalMsg
import com.todo.todo.worker.SharedRepository
import com.todo.todo.worker.events.SocketEvent

class InterviewProposalMsgEvent(
    repository: SharedRepository, private val payload:String
) : SocketEvent(repository) {

    override fun handleImpl() {
        repository.parser.fromJson(payload, InterviewProposalMsg::class.java).ifPresent{ msg ->
            msg.from?.let { from ->
                if(repository.recruiters.contains(from)){
                    println("duplicated recruiter") // TODO logger
                } else {
                    msg.sessionDescription?.let { agnosticSessionDescription ->
                        agnosticSessionDescription.toConcrete().ifPresent {
                            println("creating new recruiter $from") // TODO logger
                            // TODO timeout event
                            val recruiter = Recruiter(from, repository)
                            repository.recruiters[from] = recruiter
                            recruiter.peer.setRemoteDescription(it)
                        }
                    }
                }
            }
        }
    }

}