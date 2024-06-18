package libs.core.worker.socket.messages

import libs.core.worker.socket.messages.abstractions.SessionDescriptionMsg
import dev.onvoid.webrtc.RTCSessionDescription
import java.util.*

class InterviewProposalMsg: SessionDescriptionMsg(){

    fun toChecked(): Optional<InterviewProposalMsgChecked> {
        if(from == null || to == null || sessionDescription == null) {
            return Optional.empty()
        }
        val rtcSessionDescription = sessionDescription!!.toConcrete()
        if(rtcSessionDescription.isEmpty){
            return Optional.empty()
        }
        return Optional.of(InterviewProposalMsgChecked(from!!, to!!, rtcSessionDescription.get()))
    }

}

data class InterviewProposalMsgChecked(
    val from: String,
    val to: String,
    val sessionDescription: RTCSessionDescription
)