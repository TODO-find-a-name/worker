package messages

import com.google.gson.annotations.SerializedName
import java.nio.ByteBuffer
import java.util.Optional
import kotlin.math.ceil

open class PeerMsg(
    val msgId: String,
    val msgType: String,
    val module: String,
    val jobId: String,
    val jobType: String,
    val payload: String
){
    fun splitIntoParts(payloadSizeBytes: Int): List<PeerMsgPart> {
        val payloadLen = payload.length
        var totalParts: Int = ceil(payloadLen.toDouble() / payloadSizeBytes).toInt()
        if(totalParts == 0){
            totalParts = 1
        }
        val result = mutableListOf<PeerMsgPart>()
        for(i in 0..< totalParts){
            val part = PeerMsgPart()
            part.total = totalParts.toLong()
            part.part = i.toLong()
            part.msgId = msgId
            part.msgType = msgType
            part.module = module
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
