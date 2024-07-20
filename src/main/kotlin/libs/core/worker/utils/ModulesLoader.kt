package libs.core.worker.utils

import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import libs.common.messages.LocalPeerMsg
import libs.common.messages.LocalPeerMsgParsable
import libs.common.module.CRITICAL_ERROR_CHANNEL
import libs.common.module.WorkerModule
import libs.common.module.WorkerModulePack
import libs.core.worker.Repository
import libs.core.worker.events.RemoveRecruiterEvent
import libs.core.worker.events.recruiter.messages.SendPeerMsgToRecruiterEvent
import java.util.*
import java.util.concurrent.CompletableFuture

const val INIT_CHANNEL: String = "init"
const val ERROR_CHANNEL_SUFFIX: String = "_ERROR"

class ModulesLoader {

    private var moduleLoaderGuard: Optional<ModuleLoaderGuard> = Optional.empty()

    fun loadModules(modulePacks: List<WorkerModulePack>, repository: Repository): Map<String, WorkerModule> {
        val server = createAndStartSocketIoServer(repository)
        return modulePacks.associateBy({ it.id() }, { loadModule(it, repository, server) })
    }

    private fun createAndStartSocketIoServer(repository: Repository): SocketIOServer {
        val config = Configuration()
        config.hostname = "localhost"
        config.port = 8081 //TODO env variable?

        val server = SocketIOServer(config)
        server.addEventListener(INIT_CHANNEL, String::class.java){ client, data, _ ->
            moduleLoaderGuard.get().complete(data, client)
        }
        Runtime.getRuntime().addShutdownHook(Thread {
            server.stop()
            repository.logger.log(LoggerLvl.LOW, "Local Socket server stopped")
        })
        server.start()
        repository.logger.log(LoggerLvl.LOW, "Local Socket server started")
        return server
    }

    private fun loadModule(pack: WorkerModulePack, repository: Repository, server: SocketIOServer): WorkerModule {
        moduleLoaderGuard = Optional.of(ModuleLoaderGuard(pack.id(), repository))

        registerMainChannelListener(pack, repository, server)
        registerErrorChannelListener(pack, repository, server)
        registerDisconnectionShutdownListener(pack, repository, server)
        startModule(repository, pack)
        /*
            it will get completed in the INIT_CHANNEL listener...
            or everything will be shut down if there is an error or a timeout for the module start
         */
        val client = moduleLoaderGuard.get().waitForCompletion()

        val module = buildWorkerModule(pack, client, repository)
        repository.logger.log(LoggerLvl.LOW, "Module ${pack.id()} loaded")
        return module
    }

    private fun addSocketListenerForPeerMsg(
        server: SocketIOServer,
        channel: String,
        repository: Repository,
        onParsedMsg: (msg: LocalPeerMsg) -> Unit,
    ) {
        server.addEventListener(channel, String::class.java){ _, data, _ ->
            repository.parser.fromJson(data, LocalPeerMsgParsable::class.java).ifPresent { parsed ->
                parsed.toChecked().ifPresent {
                    onParsedMsg(it)
                }
            }
        }
    }

    private fun registerMainChannelListener(pack: WorkerModulePack, repository: Repository, server: SocketIOServer){
        val moduleMainChannel = pack.id();
        addSocketListenerForPeerMsg(server, moduleMainChannel, repository){
                msg -> SendPeerMsgToRecruiterEvent(repository, msg.recruiterId, pack.id(), msg).handle()
        }
    }

    private fun registerErrorChannelListener(pack: WorkerModulePack, repository: Repository, server: SocketIOServer){
        val moduleErrorChannel = pack.id() + ERROR_CHANNEL_SUFFIX;
        addSocketListenerForPeerMsg(server, moduleErrorChannel, repository){ msg ->
            if(msg.msgType == CRITICAL_ERROR_CHANNEL){
                RemoveRecruiterEvent(
                    repository,
                    msg.recruiterId,
                    "Module ${pack.id()} sent a critical error for job ${msg.jobId}: ${msg.payload}"
                ).handle()
            }
        }
    }

    private fun registerDisconnectionShutdownListener(pack: WorkerModulePack, repository: Repository, server: SocketIOServer){
        server.addDisconnectListener{
            ExtremeSolution.shutdown(repository.logger, ShutdownCode.MODULE_DISCONNECTED, "Connection to module " + pack.id() + " lost, shutting down")
        }
    }

    private fun startModule(repository: Repository, pack: WorkerModulePack) {
        if(repository.settings.loadModulesManually){
            repository.logger.log(LoggerLvl.LOW, "Waiting manual module ${pack.id()} loading")
        } else {
            pack.startModule(repository)
        }
    }

    private fun buildWorkerModule(pack: WorkerModulePack, client: SocketIOClient, repository: Repository): WorkerModule {
        return pack.builder()
            .socketClient(client)
            .settings(repository.settings)
            .jsonParser(repository.parser)
            .logger(repository.logger)
            .viewCallbacks(repository.viewCallbacks)
            .build()
    }

}

private class ModuleLoaderGuard(val moduleId: String, val repository: Repository){

    private val future: CompletableFuture<SocketIOClient> = CompletableFuture()
    private val timer = Timer()

    init {
        timer.schedule(
            object : TimerTask() {
                override fun run() {
                    ExtremeSolution.shutdown(
                        repository.logger,
                        ShutdownCode.MODULE_LOADING_TIMEOUT,
                        "Module $moduleId exceeded load timeout"
                    )
                }
            },
            repository.settings.moduleLoadingTimeoutMs
        )
    }

    fun complete(moduleId: String, client: SocketIOClient) {
        timer.cancel()
        if(moduleId == this.moduleId){
            future.complete(client)
        } else {
            ExtremeSolution.shutdown(
                repository.logger,
                ShutdownCode.WRONG_MODULE_LOADING_RESPONSE,
                "Expected module " + this.moduleId + " to be initialized but got a message on the init channel from " + moduleId
            )
        }
    }

    fun waitForCompletion(): SocketIOClient {
        return future.get()
    }

}