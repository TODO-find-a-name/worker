package messages

import com.google.gson.annotations.SerializedName
import java.util.Optional

open class PeerMsg(
    val msgId: String,
    val msgType: String,
    val module: String,
    val jobId: String,
    val jobType: String,
    val payload: String
)

class PeerMsgPart(){
    @SerializedName("msgId") var msgId: String? = null
    @SerializedName("msgType") var msgType: String? = null
    @SerializedName("module") var module: String? = null
    @SerializedName("jobId") var jobId: String? = null
    @SerializedName("jobType") var jobType: String? = null
    @SerializedName("payload") var payload: String? = null
    @SerializedName("part") var part: Long? = null
    @SerializedName("total") var total: Long? = null

    fun toChecked(): Optional<PeerMsgPartChecked>{
        if(
            msgId == null ||
            msgType == null ||
            module == null ||
            jobId == null ||
            jobType == null ||
            payload == null ||
            part == null ||
            total == null
        ){
            return Optional.empty()
        } else {
            return Optional.of(PeerMsgPartChecked(
                msgId!!, msgType!!, module!!, jobId!!, jobType!!, payload!!, part!!, total!!
            ))
        }

    }

}

class PeerMsgPartChecked(
    msgId: String,
    msgType: String,
    module: String,
    jobId: String,
    jobType: String,
    payload: String,
    val part: Long,
    val total: Long
): PeerMsg(msgId, msgType, module, jobId, jobType, payload)
