package com.todo.todo.worker

import com.todo.todo.worker.recruiter.Recruiter
import com.todo.todo.worker.socket.Socket
import com.todo.todo.ViewCallbacks
import com.todo.todo.worker.utils.EventQueues
import com.todo.todo.worker.utils.JsonParser
import com.todo.todo.worker.utils.WorkerSettings
import kotlinx.coroutines.Deferred
import java.util.*

class SharedRepository(val settings: WorkerSettings, val viewCallbacks: ViewCallbacks) {

    var isRunning: Boolean = false
    var loop: Optional<Deferred<Any>> = Optional.empty()
    val socket = Socket(this)
    val parser = JsonParser()
    val eventQueues = EventQueues()
    val recruiters: MutableMap<String, Recruiter> = mutableMapOf()

}