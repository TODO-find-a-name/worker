package com.todo.todo.worker.events.recruiter

import com.todo.todo.worker.SharedRepository
import com.todo.todo.worker.events.RecruiterEvent
import com.todo.todo.worker.utils.LoggerLvl
import messages.PeerMsg

class SendPeerMsgToRecruiterEvent(
    repository: SharedRepository, private val recruiterId: String, private val msg: PeerMsg
): RecruiterEvent(repository) {

    override fun handleImpl() {
        repository.recruiters[recruiterId]?.let {
            if(it.peer.isConnected()){
                log(LoggerLvl.MID, "Sending peer msg to Recruiter")
                it.peer.sendMsg(msg)
            } else {
                log(LoggerLvl.COMPLETE, "Recruiter's peer is not connected, postponing msg")
                repository.eventQueues.recruiter.add(this)
            }
            return
        }
        repository.logger.errorRecruiter(recruiterId, "Recruiter not found while trying to send peer msg")
    }

    private fun log(lvl: LoggerLvl, log: String){
        repository.logger.log(lvl, log, "Recruiter $recruiterId", "Type: ${msg.msgType}", "Id: ${msg.msgId}")
    }
}