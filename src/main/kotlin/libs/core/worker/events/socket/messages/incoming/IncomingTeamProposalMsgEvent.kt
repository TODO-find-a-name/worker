package libs.core.worker.events.socket.messages.incoming

import libs.core.worker.events.socket.messages.data.TeamProposalMsgParsable
import libs.core.worker.events.socket.messages.data.abstractions.SocketMsgType
import libs.core.worker.Repository
import libs.core.worker.events.Event
import libs.core.worker.events.socket.messages.outgoing.OutgoingTeamApplicationMsgEvent
import libs.core.worker.events.socket.messages.data.TeamProposalMsg
import libs.core.worker.utils.LoggerLvl

class IncomingTeamProposalMsgEvent(
    repository: Repository, private val payload:String
) : Event(IncomingTeamProposalMsgEvent::class.simpleName.toString(), repository) {

    override fun handleImpl() {
        repository.parser.fromJson(payload, TeamProposalMsgParsable::class.java).ifPresentOrElse(
            { parsedMsg -> parsedMsg.toChecked().ifPresentOrElse(
                {handleCheckedMsg(it)},
                {handleErrorOnMsgStructure("Incomplete")}
            )},
            {handleErrorOnMsgStructure("Unparsable")}
        )
    }

    private fun handleCheckedMsg(msg: TeamProposalMsg) {
        if(repository.modules.contains(msg.module)){
            val myId: String = repository.socket.id()
            val recruiterId: String = msg.from
            if(msg.ignore.contains(myId) || repository.recruiters.contains(recruiterId)){
                logMidIncoming(recruiterId, "Already in contact with Recruiter, ignoring message")
            } else {
                logMidIncoming(recruiterId, "New Recruiter requested Team creation")
                OutgoingTeamApplicationMsgEvent(repository, recruiterId).handle()
            }
        } else {
            println("No module found")
            // TODO
        }
    }

    private fun handleErrorOnMsgStructure(cause: String){
        repository.logger.errorSocketMsg(SocketMsgType.TEAM_PROPOSAL_NAME, "$cause msg, discarding it:\n$payload")
    }

    private fun logMidIncoming(recruiterId: String, msg: String) {
        repository.logger.logSocketIncoming(LoggerLvl.MID, SocketMsgType.TEAM_PROPOSAL_NAME, recruiterId, msg)
    }

}