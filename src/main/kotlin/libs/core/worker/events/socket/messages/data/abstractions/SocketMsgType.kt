package libs.core.worker.events.socket.messages.data.abstractions

class SocketMsgType {
    companion object {
        const val TEAM_PROPOSAL = "0"
        const val TEAM_APPLICATION = "1"
        const val INTERVIEW_PROPOSAL = "2"
        const val INTERVIEW_ACCEPTANCE = "3"
        const val TEAM_DETAILS = "4"

        const val TEAM_PROPOSAL_NAME = "TEAM_PROPOSAL"
        const val TEAM_APPLICATION_NAME = "TEAM_APPLICATION"
        const val INTERVIEW_PROPOSAL_NAME = "INTERVIEW_PROPOSAL"
        const val INTERVIEW_ACCEPTANCE_NAME = "INTERVIEW_ACCEPTANCE"
        const val TEAM_DETAILS_NAME = "TEAM_DETAILS"

        fun toHumanReadableMsgType(msgType: String): String {
            return when(msgType){
                TEAM_PROPOSAL -> TEAM_PROPOSAL_NAME
                TEAM_APPLICATION -> TEAM_APPLICATION_NAME
                INTERVIEW_PROPOSAL -> INTERVIEW_PROPOSAL_NAME
                INTERVIEW_ACCEPTANCE -> INTERVIEW_ACCEPTANCE_NAME
                TEAM_DETAILS -> TEAM_DETAILS_NAME
                else -> "UNRECOGNIZED MSG"
            }
        }
    }
}