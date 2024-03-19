package domain

import between
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class RaceTimes(val gate: Gate, val start: LocalDateTime, val finish: LocalDateTime) {
    val duration = Duration.between(start, finish)
}
