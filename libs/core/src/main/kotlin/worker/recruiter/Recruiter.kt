package com.todo.todo.worker.recruiter

import com.todo.todo.worker.SharedRepository

class Recruiter(id: String, repository: SharedRepository) {

    val peer: Peer = Peer(id, repository)

    fun disconnect(){
        peer.disconnect()
        // TODO
    }

}
