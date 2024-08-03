package libs.core.worker

import com.corundumstudio.socketio.SocketIOClient
import libs.core.worker.gui.GuiMsg
import libs.core.worker.utils.JsonParser
import java.util.*

const val GUI_MSG_CHANNEL = "GUI_MSG"

class GuiSocket {

    private var client: Optional<SocketIOClient> = Optional.empty()

    fun setClient(client: SocketIOClient) {
        this.client = Optional.of(client);
    }

    fun send(msg: GuiMsg, parser: JsonParser) {
        if(client.isPresent) {
            client.get().sendEvent(GUI_MSG_CHANNEL, parser.toJson(msg))
        }
    }
}