package com.todo.todo.worker.events.recruiter.negotiation

import com.todo.todo.worker.SharedRepository
import com.todo.todo.worker.events.Event

class CreateAnswerNegotiationEvent(repository: SharedRepository, val recruiterId: String) : Event(repository) {

    override fun handleImpl() {
        repository.recruiters[recruiterId]?.createAnswer()
    }

}