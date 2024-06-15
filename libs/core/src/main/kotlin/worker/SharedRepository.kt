package com.todo.todo.worker

import com.todo.todo.worker.recruiter.Recruiter
import com.todo.todo.worker.socket.Socket
import com.todo.todo.ViewCallbacks
import com.todo.todo.worker.events.general.RemoveRecruiterEvent
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

    val lock: Lock = Lock()

    var isRunning: Boolean = false
    val modules: Map<String, WorkerModule> = createModules(modulePacks)
    val logger: Logger = Logger(settings)
    val socket = Socket(this)
    val parser = JsonParser()
    val recruiters: MutableMap<String, Recruiter> = mutableMapOf()

    private fun createModules(modulePacks: List<WorkerModulePack>): Map<String, WorkerModule> {
        return modulePacks.associateBy(
            {pack -> pack.id()},
            {pack -> pack.builder()
                .sendPeerMsg{ recruiterId, msg ->
                    SendPeerMsgToRecruiterEvent(this, recruiterId, msg).handleImpl()
                }
                .onCriticalError{ recruiterId ->
                    RemoveRecruiterEvent(this, recruiterId).handleImpl()
                }
                .build()
            }
        )
    }

    fun removeRecruiter(id: String) {
        recruiters.remove(id)?.let { removeRecruiter(it)}
    }

    fun removeRecruiter(recruiter: Recruiter){
        recruiter.disconnect()
        // TODO everything that needs to be done
    }

}