package libs.core.worker.gui

const val START_JOB_GUI_MSG = "START_JOB"
const val STOP_JOB_GUI_MSG = "STOP_JOB"
const val RECRUITER_CONNECTED_GUI_MSG = "RECRUITER_CONNECTED"
const val RECRUITER_DISCONNECTED_GUI_MSG = "RECRUITER_DISCONNECTED"
const val BROKER_CONNECTED_GUI_MSG = "BROKER_CONNECTED"
const val BROKER_DISCONNECTED_GUI_MSG = "BROKER_DISCONNECTED"
const val BROKER_CONNECTION_ERROR_GUI_MSG = "BROKER_CONNECTION_ERROR"
const val MODULE_LOADED_GUI_MSG = "MODULE_LOADED"

open class GuiMsg(val type: String, val payload: Payload)
interface Payload

private class EmptyPayload : Payload
private class ModulePayload(val moduleId: String): Payload
private open class RecruiterPayload(val recruiterId: String) : Payload
private class JobPayload(recruiterId: String, val jobId: String) : RecruiterPayload(recruiterId)

class StartJobGuiMsg(recruiterId: String, jobId: String) : GuiMsg(START_JOB_GUI_MSG, JobPayload(recruiterId, jobId))
class StopJobGuiMsg(recruiterId: String, jobId: String) : GuiMsg(STOP_JOB_GUI_MSG, JobPayload(recruiterId, jobId))
class RecruiterConnectedGuiMsg(recruiterId: String): GuiMsg(RECRUITER_CONNECTED_GUI_MSG, RecruiterPayload(recruiterId))
class RecruiterDisconnectedGuiMsg(recruiterId: String): GuiMsg(RECRUITER_DISCONNECTED_GUI_MSG, RecruiterPayload(recruiterId))
class BrokerConnectedGuiMsg : GuiMsg(BROKER_CONNECTED_GUI_MSG, EmptyPayload())
class BrokerDisconnectedGuiMsg : GuiMsg(BROKER_DISCONNECTED_GUI_MSG, EmptyPayload())
class BrokerConnectionErrorGuiMsg: GuiMsg(BROKER_CONNECTION_ERROR_GUI_MSG, EmptyPayload())
class ModuleLoadedGuiMsg(moduleId: String): GuiMsg(MODULE_LOADED_GUI_MSG, ModulePayload(moduleId))