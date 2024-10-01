package libs.core.worker.events.socket.messages.incoming

import libs.core.worker.Repository
import libs.core.worker.events.Event
import libs.core.worker.events.RemoveRecruiterEvent
import libs.core.worker.events.socket.messages.data.TeamApplicationMsg
import libs.core.worker.events.socket.messages.data.TeamProposalMsg
import libs.core.worker.events.socket.messages.data.TeamProposalMsgParsable
import libs.core.worker.events.socket.messages.data.abstractions.SocketMsgType
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
                repository.logger.logSocketOutgoing(
                    LoggerLvl.MID, SocketMsgType.TEAM_APPLICATION_NAME, recruiterId, "Applying for team"
                )
                TeamApplicationMsg.send(repository, msg.sessionToken, recruiterId){
                    repository.logger.logSocketOutgoingAck(
                        LoggerLvl.COMPLETE, SocketMsgType.TEAM_APPLICATION_NAME, recruiterId, it
                    )
                    if(!it){
                        RemoveRecruiterEvent(
                            repository,
                            recruiterId,
                            "Ack is false on " + SocketMsgType.TEAM_APPLICATION_NAME + " sent msg",
                        ).handle()
                    }
                }
            }
        } else {
            repository.logger.logSocketIncoming(
                LoggerLvl.COMPLETE,
                SocketMsgType.TEAM_PROPOSAL,
                msg.from,
                "Received a request for an unavailable module ${msg.module}, ignoring"
            )
        }
    }

    private fun handleErrorOnMsgStructure(cause: String){
        repository.logger.errorSocketMsg(SocketMsgType.TEAM_PROPOSAL_NAME, "$cause msg, discarding it:\n$payload")
    }

    private fun logMidIncoming(recruiterId: String, msg: String) {
        repository.logger.logSocketIncoming(LoggerLvl.MID, SocketMsgType.TEAM_PROPOSAL_NAME, recruiterId, msg)
    }

}