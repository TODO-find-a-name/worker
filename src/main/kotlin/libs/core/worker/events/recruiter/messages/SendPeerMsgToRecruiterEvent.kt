package libs.core.worker.events.recruiter.messages

import libs.core.worker.Repository
import libs.core.worker.events.Event
import libs.core.worker.utils.LoggerLvl
import libs.common.messages.PeerMsg

class SendPeerMsgToRecruiterEvent(
    repository: Repository, private val recruiterId: String, private val msg: PeerMsg
): Event(repository) {

    override fun handleImpl() {
        repository.recruiters[recruiterId]?.let {
            if(it.isConnected()){
                log(LoggerLvl.MID, "Sending peer msg to Recruiter")
                it.sendMsg(msg) // TODO check se ci è riuscito
            } else {
                log(LoggerLvl.COMPLETE, "Recruiter's peer is not connected, postponing msg")
                println("CASO MOLTO MOLTO MOLTO BRUTTO PORCO DIO")
                //repository.eventQueues.recruiter.add(this)

            }
            return
        }
        repository.logger.errorRecruiter(recruiterId, "Recruiter not found while trying to send peer msg")
    }

    private fun log(lvl: LoggerLvl, log: String){
        repository.logger.log(lvl, log, "Recruiter $recruiterId", "Type: ${msg.msgType}", "Id: ${msg.msgId}")
    }
}