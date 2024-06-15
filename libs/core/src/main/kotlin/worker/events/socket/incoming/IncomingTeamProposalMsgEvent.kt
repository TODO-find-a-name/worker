package com.todo.todo.worker.events.socket.incoming

import com.todo.todo.worker.socket.messages.TeamApplicationMsg
import com.todo.todo.worker.socket.messages.TeamProposalMsg
import com.todo.todo.worker.socket.messages.abstractions.SocketMsgType
import com.todo.todo.worker.SharedRepository
import com.todo.todo.worker.events.Event
import com.todo.todo.worker.socket.messages.TeamProposalMsgChecked
import com.todo.todo.worker.utils.LoggerLvl

class IncomingTeamProposalMsgEvent(
    repository: SharedRepository, private val payload:String
) : Event(repository) {

    override fun handleImpl() {
        repository.parser.fromJson(payload, TeamProposalMsg::class.java).ifPresentOrElse(
            { parsedMsg -> parsedMsg.toChecked().ifPresentOrElse(
                {handleCheckedMsg(it)},
                {handleErrorOnMsgStructure("Incomplete")}
            )},
            {handleErrorOnMsgStructure("Unparsable")}
        )
    }

    private fun handleCheckedMsg(checkedMsg: TeamProposalMsgChecked) {
        val myId: String = repository.socket.id()
        val recruiterId: String = checkedMsg.from
        if(checkedMsg.ignore.contains(myId) || repository.recruiters.contains(recruiterId)){
            logMidIncoming(recruiterId, "Already in contact with Recruiter, ignoring message")
        } else {
            logMidIncoming(recruiterId, "New Recruiter requested Team creation")
            repository.logger.logSocketOutgoing(
                LoggerLvl.HIGH, SocketMsgType.TEAM_APPLICATION_NAME, recruiterId, "Applying for team"
            )
            TeamApplicationMsg.send(repository, recruiterId){
                repository.logger.logSocketOutgoingAck(
                    LoggerLvl.COMPLETE, SocketMsgType.TEAM_APPLICATION_NAME, recruiterId, it
                )
            }
        }
    }

    private fun handleErrorOnMsgStructure(cause: String){
        repository.logger.errorSocket(SocketMsgType.TEAM_PROPOSAL_NAME, "$cause msg, discarding it:\n$payload")
    }

    private fun logMidIncoming(recruiterId: String, msg: String) {
        repository.logger.logSocketIncoming(LoggerLvl.MID, SocketMsgType.TEAM_PROPOSAL_NAME, recruiterId, msg)
    }

}