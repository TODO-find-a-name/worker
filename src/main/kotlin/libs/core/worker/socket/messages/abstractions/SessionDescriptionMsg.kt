package libs.core.worker.socket.messages.abstractions

import com.google.gson.annotations.SerializedName
import libs.core.worker.socket.messages.abstractions.DirectMsg
import libs.core.worker.socket.messages.data.AgnosticRTCSessionDescription

open class SessionDescriptionMsg: DirectMsg() {
    @SerializedName("sessionDescription") var sessionDescription: AgnosticRTCSessionDescription? = null
}
