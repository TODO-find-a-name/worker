package libs.common.module

import com.corundumstudio.socketio.SocketIOClient
import libs.core.worker.GuiSocket
import libs.core.worker.utils.JsonParser
import libs.core.worker.utils.Logger
import libs.core.worker.utils.WorkerSettings

class WorkerModuleBuilder(private val id: String) {

    private var socketClient: SocketIOClient? = null
    private var settings: WorkerSettings? = null
    private var jsonParser: JsonParser? = null
    private var logger: Logger? = null
    private var guiSocket: GuiSocket? = null

    fun socketClient(client: SocketIOClient): WorkerModuleBuilder {
        this.socketClient = client
        return this
    }

    fun settings(settings: WorkerSettings): WorkerModuleBuilder {
        this.settings = settings
        return this
    }

    fun jsonParser(parser: JsonParser): WorkerModuleBuilder {
        this.jsonParser = parser
        return this
    }

    fun guiSocket(guiSocket: GuiSocket): WorkerModuleBuilder {
        this.guiSocket = guiSocket
        return this
    }

    fun logger(logger: Logger): WorkerModuleBuilder {
        this.logger = logger
        return this
    }

    fun build(): WorkerModule {
        return WorkerModule(
            id, socketClient!!, jsonParser!!, logger!!, settings!!, guiSocket!!
        )
    }
}