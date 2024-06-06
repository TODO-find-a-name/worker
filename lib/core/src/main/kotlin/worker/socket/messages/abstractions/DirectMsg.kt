package com.todo.todo.worker.socket.messages.abstractions

import com.google.gson.annotations.SerializedName

open class DirectMsg {
    @SerializedName("from") var from: String? = null
    @SerializedName("to") var to: String? = null
}