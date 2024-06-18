package libs.core.worker.events.socket.messages.data.adapters

import com.google.gson.annotations.SerializedName
import dev.onvoid.webrtc.RTCIceCandidate
import java.util.Optional

class IceCandidateAdapter {
    @SerializedName("sdp") var sdp: String? = null
    @SerializedName("sdpMid") var sdpMid: String? = null
    @SerializedName("sdpMLineIndex") var sdpMLineIndex: Int? = null

    /**
     * TODO probably does not work outside lan, further analysis on mapping here and on javascript is required
     */

    fun toConcrete(): Optional<RTCIceCandidate>{
        if(sdp == null || sdpMid == null || sdpMLineIndex == null){
            return Optional.empty()
        }
        return Optional.of(RTCIceCandidate(sdpMid, sdpMLineIndex!!, sdp))
    }

    companion object {
        fun adaptConcrete(rtcIceCandidate: RTCIceCandidate): IceCandidateAdapter {
            val agnostic = IceCandidateAdapter()
            agnostic.sdp = rtcIceCandidate.sdp
            agnostic.sdpMid = rtcIceCandidate.sdpMid
            agnostic.sdpMLineIndex = rtcIceCandidate.sdpMLineIndex
            return agnostic
        }
    }
}