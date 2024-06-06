package libs.core.worker.events.socket.messages.data.adapters

import com.google.gson.annotations.SerializedName
import dev.onvoid.webrtc.RTCSdpType
import dev.onvoid.webrtc.RTCSessionDescription
import java.util.Optional

const val AGNOSTIC_OFFER = "offer"
const val AGNOSTIC_ANSWER = "answer"

class AgnosticRTCSessionDescription{
    @SerializedName("type") var type: String? = null
    @SerializedName("sdp") var sdp: String? = null

    fun toConcrete(): Optional<RTCSessionDescription> {
        if(sdp == null){
            return Optional.empty()
        }
        return when(type){
            AGNOSTIC_OFFER -> Optional.of(RTCSessionDescription(RTCSdpType.OFFER, sdp))
            AGNOSTIC_ANSWER -> Optional.of(RTCSessionDescription(RTCSdpType.ANSWER, sdp))
            else -> Optional.empty()
        }
    }

    companion object {
        fun adaptConcrete(
            rtcSessionDescription: RTCSessionDescription
        ): Optional<AgnosticRTCSessionDescription>{
            val agnostic = AgnosticRTCSessionDescription()
            agnostic.sdp = rtcSessionDescription.sdp
            when(rtcSessionDescription.sdpType){
                RTCSdpType.OFFER -> agnostic.type = AGNOSTIC_OFFER
                RTCSdpType.ANSWER -> agnostic.type = AGNOSTIC_ANSWER
                else -> return Optional.empty()
            }
            return Optional.of(agnostic)
        }
    }

}
