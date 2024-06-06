package libs.common.messages

import com.google.gson.annotations.SerializedName
import java.util.Optional
import kotlin.math.ceil

open class PeerMsg(
    val msgId: Int,
    val msgType: String,
    val jobId: String,
    val jobType: String,
    val payload: String
){
    fun splitIntoParts(payloadSizeBytes: Int): List<PeerMsgPartParsable> {
        val payloadLen = payload.length
        var totalParts: Int = ceil(payloadLen.toDouble() / payloadSizeBytes).toInt()
        if(totalParts == 0){
            totalParts = 1
        }
        val result = mutableListOf<PeerMsgPartParsable>()
        for(i in 0..< totalParts){
            val part = PeerMsgPartParsable()
            part.total = totalParts
            part.part = i
            part.msgId = msgId
            part.msgType = msgType
            part.jobId = jobId
            part.jobType = jobType
            val chunkStart = i * payloadSizeBytes
            var chunkEnd = chunkStart + payloadSizeBytes
            if(chunkEnd > payloadLen){
                chunkEnd = payloadLen
            }
            part.payload = payload.substring(chunkStart, chunkEnd)
            result.add(part)
        }
        return result
    }
}

class PeerMsgPart(
    msgId: Int,
    msgType: String,
    jobId: String,
    jobType: String,
    payload: String,
    val part: Int,
    val total: Int
): PeerMsg(msgId, msgType, jobId, jobType, payload)

class PeerMsgPartParsable {
    @SerializedName("msgId") var msgId: Int? = null
    @SerializedName("msgType") var msgType: String? = null
    @SerializedName("jobId") var jobId: String? = null
    @SerializedName("jobType") var jobType: String? = null
    @SerializedName("payload") var payload: String? = null
    @SerializedName("part") var part: Int? = null
    @SerializedName("total") var total: Int? = null

    fun toChecked(): Optional<PeerMsgPart>{
        return if(isSomethingAbsent()){
            Optional.empty()
        } else {
            Optional.of(PeerMsgPart(msgId!!, msgType!!, jobId!!, jobType!!, payload!!, part!!, total!!))
        }
    }

    private fun isSomethingAbsent(): Boolean{
        return msgId == null || msgType == null || jobId == null || jobType == null ||
                payload == null || part == null || total == null
    }
}
