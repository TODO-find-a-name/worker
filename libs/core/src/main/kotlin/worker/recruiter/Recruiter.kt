package com.todo.todo.worker.recruiter

import com.todo.todo.worker.SharedRepository

class Recruiter(id: String, repository: SharedRepository) {

    val peer: Peer = Peer(id, repository)
    val pendingMessages: MutableMap<String, PendingMsg> = mutableMapOf()

    fun disconnect(){
        peer.disconnect()
        // TODO
    }

}
