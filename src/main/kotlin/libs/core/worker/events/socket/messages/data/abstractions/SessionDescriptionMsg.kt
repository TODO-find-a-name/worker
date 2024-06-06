package libs.core.worker.events.socket.messages.data.abstractions

import com.google.gson.annotations.SerializedName
import libs.core.worker.events.socket.messages.data.adapters.AgnosticRTCSessionDescription

open class SessionDescriptionMsg: DirectMsg() {
    @SerializedName("sessionDescription") var sessionDescription: AgnosticRTCSessionDescription? = null
}
