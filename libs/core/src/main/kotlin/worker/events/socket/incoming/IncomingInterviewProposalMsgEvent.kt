package com.todo.todo.worker.events.socket.incoming

import com.todo.todo.worker.recruiter.Recruiter
import com.todo.todo.worker.socket.messages.InterviewProposalMsg
import com.todo.todo.worker.SharedRepository
import com.todo.todo.worker.events.Event
import com.todo.todo.worker.socket.messages.InterviewProposalMsgChecked
import com.todo.todo.worker.socket.messages.abstractions.SocketMsgType
import com.todo.todo.worker.utils.LoggerLvl

class IncomingInterviewProposalMsgEvent(
    repository: SharedRepository, private val payload:String
) : Event(repository) {

    override fun handleImpl() {
        repository.parser.fromJson(payload, InterviewProposalMsg::class.java).ifPresentOrElse(
            { parsedMsg -> parsedMsg.toChecked().ifPresentOrElse(
                {handleCheckedMsg(it)},
                {handleErrorOnMsgStructure("Incomplete")}
            )},
            {handleErrorOnMsgStructure("Unparsable")}
        )
    }

    private fun handleCheckedMsg(checkedMsg: InterviewProposalMsgChecked) {
        val recruiterId = checkedMsg.from
        if(repository.recruiters.contains(recruiterId)){
            repository.logger.errorSocket(
                SocketMsgType.INTERVIEW_PROPOSAL_NAME, "Already working with Recruiter, discarding msg"
            )
        } else {
            repository.logger.logSocketIncoming(
                LoggerLvl.MID, SocketMsgType.INTERVIEW_PROPOSAL_NAME, recruiterId, "Creating new Recruiter"
            )
            val recruiter = Recruiter(recruiterId, repository, System.currentTimeMillis())
            repository.recruiters[recruiterId] = recruiter
            recruiter.peer.setRemoteDescription(checkedMsg.sessionDescription)
        }
    }

    private fun handleErrorOnMsgStructure(cause: String){
        repository.logger.errorSocket(SocketMsgType.INTERVIEW_PROPOSAL_NAME, "$cause msg, discarding it:\n$payload")
    }

}