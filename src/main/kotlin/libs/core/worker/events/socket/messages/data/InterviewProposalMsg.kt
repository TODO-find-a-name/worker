package libs.core.worker.events.socket.messages.data

import com.google.gson.annotations.SerializedName
import dev.onvoid.webrtc.RTCSessionDescription
import libs.core.worker.events.socket.messages.data.abstractions.SessionDescriptionMsg
import java.util.*

data class InterviewProposalMsg(
    val sessionToken: String,
    val from: String,
    val to: String,
    val module: String,
    val sessionDescription: RTCSessionDescription
)

class InterviewProposalMsgParsable(
    @SerializedName("module") var module: String? = null
): SessionDescriptionMsg(){

    fun toChecked(): Optional<InterviewProposalMsg> {
        if(sessionToken == null || from == null || to == null || sessionDescription == null || module == null) {
            return Optional.empty()
        }
        val rtcSessionDescription = sessionDescription!!.toConcrete()
        if(rtcSessionDescription.isEmpty){
            return Optional.empty()
        }
        return Optional.of(InterviewProposalMsg(sessionToken!!, from!!, to!!, module!!, rtcSessionDescription.get()))
    }

}
