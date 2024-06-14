package com.todo.todo.worker.events.general

import com.todo.todo.worker.SharedRepository
import com.todo.todo.worker.events.GeneralEvent

class RemoveRecruiterEvent(
    repository: SharedRepository, private val id: String
) : GeneralEvent(repository) {

    override fun handleImpl() {
        repository.removeRecruiter(id)
    }

}