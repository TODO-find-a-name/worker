package libs.core.worker.events.socket.messages.data

import libs.core.worker.events.socket.messages.data.abstractions.SessionDescriptionMsg
import dev.onvoid.webrtc.RTCSessionDescription
import java.util.*

data class InterviewProposalMsg(
    val from: String,
    val to: String,
    val sessionDescription: RTCSessionDescription
)

class InterviewProposalMsgParsable: SessionDescriptionMsg(){

    fun toChecked(): Optional<InterviewProposalMsg> {
        if(from == null || to == null || sessionDescription == null) {
            return Optional.empty()
        }
        val rtcSessionDescription = sessionDescription!!.toConcrete()
        if(rtcSessionDescription.isEmpty){
            return Optional.empty()
        }
        return Optional.of(InterviewProposalMsg(from!!, to!!, rtcSessionDescription.get()))
    }

}
