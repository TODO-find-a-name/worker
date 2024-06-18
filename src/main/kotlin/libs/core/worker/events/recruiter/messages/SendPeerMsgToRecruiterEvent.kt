package libs.core.worker.events.recruiter.messages

import dev.onvoid.webrtc.RTCDataChannelBuffer
import libs.core.worker.Repository
import libs.core.worker.utils.LoggerLvl
import libs.common.messages.PeerMsg
import libs.core.worker.events.RecruiterEvent
import libs.core.worker.recruiter.Recruiter
import libs.core.worker.utils.JsonParser
import java.nio.ByteBuffer

class SendPeerMsgToRecruiterEvent(
    repository: Repository, recruiterId: String, private val msg: PeerMsg
): RecruiterEvent(repository, recruiterId) {

    override fun handleImpl(recruiter: Recruiter) {
        if(recruiter.isConnected()){
            log(LoggerLvl.MID, "Sending peer msg to Recruiter")
            sendMsg(msg, recruiter, repository.parser) // TODO check se ci è riuscito
        } else {
            log(LoggerLvl.COMPLETE, "Recruiter's peer is not connected, postponing msg")
            println("CASO MOLTO MOLTO MOLTO BRUTTo")
            //repository.eventQueues.recruiter.add(this)
        }
    }

    private fun log(lvl: LoggerLvl, log: String){
        repository.logger.log(lvl, log, "Recruiter $recruiterId", "Type: ${msg.msgType}", "Id: ${msg.msgId}")
    }

    private fun sendMsg(msg: PeerMsg, recruiter: Recruiter, parser: JsonParser): Boolean {
        if(recruiter.dataChannel == null || !recruiter.isConnected()){
            return false
        }
        try {
            msg.splitIntoParts(repository.settings.p2pPayloadSizeBytes).forEach {
                recruiter.dataChannel?.send(RTCDataChannelBuffer(
                    ByteBuffer.wrap(parser.toJson(it).toByteArray()), false
                ))
            }
        } catch(e: Exception){
            return false
        }
        return true
    }
}