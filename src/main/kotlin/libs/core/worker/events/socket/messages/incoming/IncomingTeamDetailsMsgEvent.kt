package libs.core.worker.events.socket.messages.incoming

import libs.core.worker.Repository
import libs.core.worker.events.Event
import libs.core.worker.events.RemoveRecruiterEvent
import libs.core.worker.events.socket.messages.data.TeamDetailsMsg
import libs.core.worker.events.socket.messages.data.TeamDetailsMsgParsable
import libs.core.worker.events.socket.messages.data.abstractions.SocketMsgType
import libs.core.worker.utils.LoggerLvl

class IncomingTeamDetailsMsgEvent(
    repository: Repository, private val payload:String
) : Event(IncomingTeamDetailsMsgEvent::class.simpleName.toString(), repository) {

    override fun handleImpl() {
        repository.parser.fromJson(payload, TeamDetailsMsgParsable::class.java).ifPresentOrElse(
            { parsedMsg -> parsedMsg.toChecked().ifPresentOrElse(
                {handleCheckedMsg(it)},
                {handleErrorOnMsgStructure("Incomplete")}
            )},
            {handleErrorOnMsgStructure("Unparsable")}
        )
    }

    private fun handleCheckedMsg(checkedMsg: TeamDetailsMsg) {
        val recruiter = repository.recruiters[checkedMsg.from]
        if(recruiter == null){
            repository.logger.errorSocketMsg(SocketMsgType.TEAM_DETAILS_NAME, "Recruiter not found", checkedMsg.from)
        } else {
            if(recruiter.sessionToken != checkedMsg.sessionToken){
                RemoveRecruiterEvent(
                    repository,
                    checkedMsg.from,
                    "Session token mismatch on " + SocketMsgType.TEAM_DETAILS_NAME + " msg"
                ).handle()
            } else if(recruiter.isConnected){
                repository.logger.logSocketIncoming(
                    LoggerLvl.COMPLETE,
                    SocketMsgType.TEAM_DETAILS_NAME,
                    checkedMsg.from,
                    "Recruiter already connected, ignoring ice candidate"
                )
            } else {
                repository.logger.logSocketIncoming(
                    LoggerLvl.HIGH,
                    SocketMsgType.TEAM_DETAILS_NAME,
                    checkedMsg.from,
                    "Adding ice candidate to Recruiter"
                )
                recruiter.addIceCandidate(checkedMsg.candidate)
            }
        }
    }

    private fun handleErrorOnMsgStructure(cause: String){
        repository.logger.errorSocketMsg(SocketMsgType.TEAM_DETAILS_NAME, "$cause msg, discarding it:\n$payload")
    }

}