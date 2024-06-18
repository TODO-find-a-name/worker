package libs.core.worker.utils

class Lock {

    @Synchronized
    fun execute(function: () -> Unit){
        function()
    }

}