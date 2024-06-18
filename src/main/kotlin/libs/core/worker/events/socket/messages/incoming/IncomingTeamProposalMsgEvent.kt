package libs.core.worker.events.socket.messages.incoming

import libs.core.worker.events.socket.messages.data.TeamProposalMsg
import libs.core.worker.events.socket.messages.data.abstractions.SocketMsgType
import libs.core.worker.Repository
import libs.core.worker.events.Event
import libs.core.worker.events.socket.messages.outgoing.OutgoingTeamApplicationMsgEvent
import libs.core.worker.events.socket.messages.data.TeamProposalMsgChecked
import libs.core.worker.utils.LoggerLvl

class IncomingTeamProposalMsgEvent(
    repository: Repository, private val payload:String
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
            OutgoingTeamApplicationMsgEvent(repository, recruiterId).handle()
        }
    }

    private fun handleErrorOnMsgStructure(cause: String){
        repository.logger.errorSocket(SocketMsgType.TEAM_PROPOSAL_NAME, "$cause msg, discarding it:\n$payload")
    }

    private fun logMidIncoming(recruiterId: String, msg: String) {
        repository.logger.logSocketIncoming(LoggerLvl.MID, SocketMsgType.TEAM_PROPOSAL_NAME, recruiterId, msg)
    }

}