package com.todo.todo.worker.socket.messages.abstractions

class SocketMsgType {
    companion object {
        const val TEAM_PROPOSAL = "0"
        const val TEAM_APPLICATION = "1"
        const val INTERVIEW_PROPOSAL = "2"
        const val INTERVIEW_ACCEPTANCE = "3"
        const val TEAM_DETAILS = "4"
    }
}