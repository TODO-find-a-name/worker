package app

import libs.core.worker.Worker

fun main() {
    Worker(
        WorkerSettingsByEnvVariables.create(),
        LoadModulesByEnvVariables.load(),
        TerminalViewCallbacks(false)
    ).connect()
}
