package com.todo.todo.worker.events.general

import com.todo.todo.worker.SharedRepository
import com.todo.todo.worker.events.Event

class RemoveRecruiterEvent(
    repository: SharedRepository, private val id: String
) : Event(repository) {

    override fun handleImpl() {
        println("removing recruiter")
        repository.removeRecruiter(id)
    }

}