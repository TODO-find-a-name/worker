package com.todo.todo.worker.events.recruiter

import com.todo.todo.worker.SharedRepository
import com.todo.todo.worker.events.RecruiterEvent
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

class IncomingRecruiterMsgPartEvent(repository: SharedRepository, private val msg: ByteBuffer) : RecruiterEvent(repository) {

    override fun handleImpl() {
        println(StandardCharsets.UTF_8.decode(msg).toString())
    }
}