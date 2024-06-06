package com.todo.todo.worker.utils

import com.todo.todo.worker.events.GeneralEvent
import com.todo.todo.worker.events.RecruiterEvent
import com.todo.todo.worker.events.SocketEvent

class EventQueues {

    val general: MutableList<GeneralEvent> = mutableListOf()
    val socket: MutableList<SocketEvent> = mutableListOf()
    val recruiter: MutableList<RecruiterEvent> = mutableListOf()

}