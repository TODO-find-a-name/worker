package com.todo.todo.worker.socket.messages

import com.google.gson.annotations.SerializedName
import java.util.Optional

data class TeamProposalMsg(
    @SerializedName("organization") var organization : String? = null,
    @SerializedName("ignore") var ignore: ArrayList<String> = arrayListOf(),
    @SerializedName("from") var from: String? = null
) {
    fun toChecked(): Optional<TeamProposalMsgChecked>{
        if(organization == null || from == null){
            return Optional.empty()
        }
        return Optional.of(TeamProposalMsgChecked(organization!!, ignore, from!!))
    }
}

data class TeamProposalMsgChecked(
    val organization : String,
    val ignore: ArrayList<String>,
    val from: String
)
