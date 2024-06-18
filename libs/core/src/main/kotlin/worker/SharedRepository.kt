package com.todo.todo.worker

import com.todo.todo.worker.recruiter.Recruiter
import com.todo.todo.worker.socket.Socket
import com.todo.todo.ViewCallbacks
import com.todo.todo.worker.events.RemoveRecruiterEvent
import com.todo.todo.worker.events.recruiter.SendPeerMsgToRecruiterEvent
import com.todo.todo.worker.utils.JsonParser
import com.todo.todo.worker.utils.Lock
import com.todo.todo.worker.utils.Logger
import com.todo.todo.worker.utils.WorkerSettings
import module.WorkerModule
import module.WorkerModulePack

class SharedRepository(
    val settings: WorkerSettings, modulePacks: List<WorkerModulePack>, val viewCallbacks: ViewCallbacks
) {

    var isRunning: Boolean = false

    val lock: Lock = Lock()
    val modules = createModules(modulePacks)
    val logger: Logger = Logger(settings)
    val socket = Socket(this)
    val parser = JsonParser()
    val recruiters: MutableMap<String, Recruiter> = mutableMapOf()

    private fun createModules(modulePacks: List<WorkerModulePack>): Map<String, WorkerModule> {
        return modulePacks.associateBy(
            {pack -> pack.id()},
            {pack -> pack.builder()
                .sendPeerMsg{ recruiterId, msg ->
                    SendPeerMsgToRecruiterEvent(this, recruiterId, msg).handle()
                }
                .onCriticalError{ recruiterId ->
                    RemoveRecruiterEvent(this, recruiterId).handle()
                }
                .build()
            }
        )
    }

    fun removeRecruiter(id: String) {
        recruiters.remove(id)?.let {
            it.disconnect()
            it.timeoutTimer.cancel()
            it.pendingMessages.forEach{ pair -> pair.value.cancelTimeout() }
            it.pendingMessages.clear()
        }
    }

}