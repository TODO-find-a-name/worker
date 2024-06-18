package libs.core

interface ViewCallbacks {

    fun onBrokerConnectionEstablished()
    fun onBrokerConnectionError()
    fun onBrokerDisconnection()
    fun onRecruiterConnected(id: String)
    fun onRecruiterDisconnected(id: String)
    fun onJobStarted(recruiterId: String, jobId: String)
    fun onJobEnded(recruiterId: String, jobId: String)

}