package com.todo.todo.worker.events.socket.incoming

import com.todo.todo.worker.socket.messages.TeamDetailsMsg
import com.todo.todo.worker.SharedRepository
import com.todo.todo.worker.events.Event
import com.todo.todo.worker.socket.messages.TeamDetailsMsgChecked
import com.todo.todo.worker.socket.messages.abstractions.SocketMsgType
import com.todo.todo.worker.utils.LoggerLvl

class IncomingTeamDetailsMsgEvent(repository: SharedRepository, private val payload:String) : Event(repository) {

    override fun handleImpl() {
        repository.parser.fromJson(payload, TeamDetailsMsg::class.java).ifPresentOrElse(
            { parsedMsg -> parsedMsg.toChecked().ifPresentOrElse(
                {handleCheckedMsg(it)},
                {handleErrorOnMsgStructure("Incomplete")}
            )},
            {handleErrorOnMsgStructure("Unparsable")}
        )
    }

    private fun handleCheckedMsg(checkedMsg: TeamDetailsMsgChecked) {
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