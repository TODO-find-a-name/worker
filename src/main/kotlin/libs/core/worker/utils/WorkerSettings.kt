package libs.core.worker.utils

import dev.onvoid.webrtc.RTCConfiguration
import dev.onvoid.webrtc.RTCIceServer

class WorkerSettings(
    val brokerAddr: String,
    val organization: String,
    val loggingLvl: LoggerLvl = LoggerLvl.LOW,
    val p2pPayloadSizeBytes: Int = 10000,
    val timeoutCheckFrequencyMs: Long = 1000,
    val recruitmentTimeoutMs: Long = 5000,
    val p2pMsgTimeoutMs: Long = 60000
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
