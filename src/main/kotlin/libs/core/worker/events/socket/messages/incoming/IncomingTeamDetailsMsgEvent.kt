package libs.core.worker.events.socket.messages.incoming

import libs.core.worker.events.socket.messages.data.TeamDetailsMsgParsable
import libs.core.worker.Repository
import libs.core.worker.events.Event
import libs.core.worker.events.socket.messages.data.TeamDetailsMsg
import libs.core.worker.events.socket.messages.data.abstractions.SocketMsgType
import libs.core.worker.utils.LoggerLvl

class IncomingTeamDetailsMsgEvent(repository: Repository, private val payload:String) : Event(repository) {

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
            repository.logger.errorSocket(SocketMsgType.TEAM_DETAILS_NAME, "Recruiter not found", checkedMsg.from)
        } else {
            if(recruiter.isConnected()){
                logHigh(checkedMsg.from, "Recruiter already connected, ignoring msg")
            } else {
                logHigh(checkedMsg.from, "Adding candidate")
                recruiter.addIceCandidate(checkedMsg.candidate)
            }
        }
    }

    private fun handleErrorOnMsgStructure(cause: String){
        repository.logger.errorSocket(SocketMsgType.TEAM_DETAILS_NAME, "$cause msg, discarding it:\n$payload")
    }

    private fun logHigh(from: String, msg: String){
        repository.logger.logSocketIncoming(LoggerLvl.HIGH, SocketMsgType.TEAM_DETAILS_NAME, from, msg)
    }

}