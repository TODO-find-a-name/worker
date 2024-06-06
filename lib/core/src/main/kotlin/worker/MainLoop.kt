package com.todo.todo.worker

class MainLoop {
    companion object {

        fun start(repository: SharedRepository){
            val generalQueue = repository.eventQueues.general
            val socketQueue = repository.eventQueues.socket
            val recruiterQueue = repository.eventQueues.recruiter
            val recruiterEventsPerCycle = repository.settings.recruiterEventsPerCycle
            var recruiterEventsIndex: Int

            while (repository.isRunning){
                while (generalQueue.isNotEmpty()){
                    generalQueue.removeFirst().handleImpl()
                }
                if(repository.isRunning){
                    while (socketQueue.isNotEmpty()){
                        socketQueue.removeFirst().handleImpl()
                    }
                    recruiterEventsIndex = 0
                    while(recruiterEventsIndex++ < recruiterEventsPerCycle && recruiterQueue.isNotEmpty()) {
                        recruiterQueue.removeFirst().handleImpl()
                    }
                }
            }
        }

    }
}