package libs.core.worker.utils

import libs.core.worker.events.Event
import java.util.*

fun Timer.scheduleEvent(event: Event, delay: Long) {
    this.schedule(
        object : TimerTask() {
            override fun run() {
                event.handle()
            }
        },
        delay
    )
}