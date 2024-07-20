package libs.core.worker.utils

import dev.onvoid.webrtc.RTCConfiguration
import dev.onvoid.webrtc.RTCIceServer

val DEFAULT_LOGGING_LEVEL = LoggerLvl.MID
const val DEFAULT_MODULE_LOADING_TIMEOUT_MS = 300000L // 5 minutes
const val DEFAULT_P2P_PAYLOAD_SIZE_BYTES = 20000
const val DEFAULT_RECRUITMENT_TIMEOUT_MS = 5000L
const val DEFAULT_P2P_MSG_TIMEOUT_MS = 60000L
const val DEFAULT_MODULE_FORWARDING_TIMEOUT_SECONDS = 30
const val DEFAULT_LOAD_MODULES_MANUALLY = false
const val DEFAULT_NODE_MODULE_STARTUP_SCRIPT_PATH = "./modules/node_js_client_module/run_dev.sh"

class WorkerSettings(
    val brokerAddr: String,
    val organization: String,
    val loggingLvl: LoggerLvl = DEFAULT_LOGGING_LEVEL,
    val moduleLoadingTimeoutMs: Long = DEFAULT_MODULE_LOADING_TIMEOUT_MS,
    val p2pPayloadSizeBytes: Int = DEFAULT_P2P_PAYLOAD_SIZE_BYTES,
    val recruitmentTimeoutMs: Long = DEFAULT_RECRUITMENT_TIMEOUT_MS,
    val p2pMsgTimeoutMs: Long = DEFAULT_P2P_MSG_TIMEOUT_MS,
    val moduleForwardingTimeoutSeconds: Int = DEFAULT_MODULE_FORWARDING_TIMEOUT_SECONDS,
    val loadModulesManually: Boolean = DEFAULT_LOAD_MODULES_MANUALLY,
    val nodeModuleStartupScriptPath: String = DEFAULT_NODE_MODULE_STARTUP_SCRIPT_PATH
) {

    fun getRTCConfiguration(): RTCConfiguration {
        val rtcConfiguration = RTCConfiguration()
        rtcConfiguration.iceServers = listOf(
            createRTCIceServer(listOf("stun:openrelay.metered.ca:80")),
            createRTCIceServer(listOf("turn:openrelay.metered.ca:80"), "openrelayproject", "openrelayproject"),
            createRTCIceServer(listOf("turn:openrelay.metered.ca:443"), "openrelayproject", "openrelayproject"),
            createRTCIceServer(
                listOf("turn:openrelay.metered.ca:443?transport=tcp"),
                "openrelayproject",
                "openrelayproject"
            )
        )
        return rtcConfiguration
    }

    private fun createRTCIceServer(urls: List<String>, username: String? = null, password: String? = null): RTCIceServer {
        val rtcIceServer = RTCIceServer()
        rtcIceServer.urls = urls
        username?.let { rtcIceServer.username = username }
        password?.let { rtcIceServer.password = password }
        return rtcIceServer
    }

}
