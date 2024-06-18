package com.todo.todo.worker.events

import com.todo.todo.worker.SharedRepository

class RemoveRecruiterEvent(
    repository: SharedRepository, private val id: String
) : Event(repository) {

    override fun handleImpl() {
        println("removing recruiter")
        repository.removeRecruiter(id)
    }

}