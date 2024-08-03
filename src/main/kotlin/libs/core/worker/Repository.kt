package libs.core.worker

import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import libs.common.module.WorkerModule
import libs.common.module.WorkerModulePack
import libs.core.worker.gui.RecruiterDisconnectedGuiMsg
import libs.core.worker.utils.ExtremeSolution
import libs.core.worker.utils.JsonParser
import libs.core.worker.utils.Lock
import libs.core.worker.utils.Logger
import libs.core.worker.utils.LoggerLvl
import libs.core.worker.utils.ModulesLoader
import libs.core.worker.utils.ShutdownCode
import libs.core.worker.utils.WorkerSettings
import java.util.*
import java.util.concurrent.CompletableFuture

const val GUI_INIT_CHANNEL: String = "GUI_INIT"

class Repository(val settings: WorkerSettings, modulePacks: List<WorkerModulePack>) {

    var isRunning: Boolean = false

    val logger: Logger = Logger(settings)
    val lock: Lock = Lock(logger)
    val parser = JsonParser()
    val modules: Map<String, WorkerModule>
    val recruiters: MutableMap<String, Recruiter> = mutableMapOf()
    val socket = Socket(this)
    val guiSocket = GuiSocket()

    private var guiLoaderGuard: Optional<GuiLoaderGuard> = Optional.empty()
    private val socketServer: SocketIOServer

    init {
        val config = Configuration()
        config.hostname = "0.0.0.0"
        config.port = 8081 //TODO env variable?
        config.origin = "*"

        socketServer = SocketIOServer(config)
        Runtime.getRuntime().addShutdownHook(Thread {
            socketServer.stop()
            logger.log(LoggerLvl.LOW, "Local Socket server stopped")
        })
        socketServer.start()
        logger.log(LoggerLvl.LOW, "Local Socket server started")
        if(settings.isGuiEnabled){
            loadGui()
        }
        modules = ModulesLoader().loadModules(modulePacks, socketServer, this)
    }

    private fun loadGui(){
        logger.log(LoggerLvl.LOW, "Waiting for GUI to connect")
        val guard = GuiLoaderGuard(this)
        guiLoaderGuard = Optional.of(guard)
        socketServer.addEventListener(GUI_INIT_CHANNEL, String::class.java){ client, _, _ ->
            guiLoaderGuard.ifPresent {
                it.complete(client)
                guiLoaderGuard = Optional.empty()
                // TODO rimuovere listener
            }
        }
        guiSocket.setClient(guard.waitForCompletion())
        logger.log(LoggerLvl.LOW, "GUI connected")
    }

    fun removeRecruiter(id: String, log: String) {
        logger.error("Removing Recruiter $id: $log")
        recruiters.remove(id)?.disconnect()
        guiSocket.send(RecruiterDisconnectedGuiMsg(id), parser)
    }

    fun removeRecruiter(recruiter: Recruiter, log: String){
        val id = recruiter.recruiterId
        logger.error("Removing Recruiter ${id}: $log")
        recruiters.remove(recruiter.recruiterId)
        recruiter.disconnect()
        guiSocket.send(RecruiterDisconnectedGuiMsg(id), parser)
    }

}

private class GuiLoaderGuard(val repository: Repository){

    private val future: CompletableFuture<SocketIOClient> = CompletableFuture()
    private val timer = Timer()

    init {
        timer.schedule(
            object : TimerTask() {
                override fun run() {
                    ExtremeSolution.shutdown(
                        repository.logger,
                        ShutdownCode.GUI_LOADING_TIMEOUT,
                        "Gui exceeded load timeout"
                    )
                }
            },
            100000000000000 //TODO
        )
    }

    fun complete(client: SocketIOClient) {
        timer.cancel()
        future.complete(client)
    }

    fun waitForCompletion(): SocketIOClient {
        return future.get()
    }

}