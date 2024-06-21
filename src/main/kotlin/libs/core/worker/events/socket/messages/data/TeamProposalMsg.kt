package libs.core.worker.events.socket.messages.data

import com.google.gson.annotations.SerializedName
import java.util.Optional

data class TeamProposalMsg(
    val organization : String,
    val ignore: ArrayList<String>,
    val from: String,
    val module: String
)

class TeamProposalMsgParsable(
    @SerializedName("organization") var organization : String? = null,
    @SerializedName("ignore") var ignore: ArrayList<String> = arrayListOf(),
    @SerializedName("from") var from: String? = null,
    @SerializedName("module") var module: String? = null
) {
    fun toChecked(): Optional<TeamProposalMsg>{
        if(organization == null || from == null || module == null){
            return Optional.empty()
        }
        return Optional.of(TeamProposalMsg(organization!!, ignore, from!!, module!!))
    }
}
