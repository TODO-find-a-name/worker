package com.todo.todo.worker.utils

class Lock {

    @Synchronized
    fun execute(function: () -> Unit){
        function()
    }

}