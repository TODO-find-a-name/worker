package com.todo.todo.worker.socket.messages.abstractions

import com.google.gson.annotations.SerializedName
import com.todo.todo.worker.socket.messages.abstractions.DirectMsg
import com.todo.todo.worker.socket.messages.data.AgnosticRTCSessionDescription

open class SessionDescriptionMsg: DirectMsg() {
    @SerializedName("sessionDescription") var sessionDescription: AgnosticRTCSessionDescription? = null
}
