package com.todo.todo.worker.socket.messages

import com.google.gson.annotations.SerializedName

data class TeamProposalMsg(
    @SerializedName("organization") var organization : String? = null,
    @SerializedName("ignore") var ignore: ArrayList<String> = arrayListOf(),
    @SerializedName("from") var from: String? = null
)
