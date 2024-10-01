package libs.core.worker.events.socket.messages.data

import com.google.gson.annotations.SerializedName
import java.util.*

data class TeamProposalMsg(
    val sessionToken: String,
    val organization : String,
    val ignore: ArrayList<String>,
    val from: String,
    val module: String
)

class TeamProposalMsgParsable(
    @SerializedName("sessionToken") var sessionToken: String? = null,
    @SerializedName("organization") var organization : String? = null,
    @SerializedName("ignore") var ignore: ArrayList<String> = arrayListOf(),
    @SerializedName("from") var from: String? = null,
    @SerializedName("module") var module: String? = null
) {
    fun toChecked(): Optional<TeamProposalMsg>{
        if(sessionToken == null || organization == null || from == null || module == null){
            return Optional.empty()
        }
        return Optional.of(TeamProposalMsg(sessionToken!!, organization!!, ignore, from!!, module!!))
    }
}
