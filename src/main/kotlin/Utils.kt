import kotlinx.datetime.Clock.System
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalDateTime.Companion
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration
import kotlin.time.toKotlinDuration
import java.time.Duration as JDuration

fun Duration.Companion.between(
    startInclusive: LocalDateTime,
    endExclusive: LocalDateTime,
): Duration {
    return JDuration.between(startInclusive.toJavaLocalDateTime(), endExclusive.toJavaLocalDateTime())
        .toKotlinDuration()
}

fun Duration.fmt(): String {
    val minutes = inWholeMinutes
    val seconds = inWholeSeconds - minutes * 60
    val milliseconds = inWholeMilliseconds - minutes * 60 * 1000 - seconds * 1000
    return String.format("%02d:%02d.%03d", minutes, seconds, milliseconds)
}

fun LocalDateTime.fmt(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    return formatter.format(this.toJavaLocalDateTime())
}

fun Companion.now() = System.now().toLocalDateTime(TimeZone.UTC)

fun <T, R : Comparable<R>> Iterable<T>.sortedBy(
    descending: Boolean,
    selector: (T) -> R?,
): List<T> =
    if (descending) {
        sortedByDescending(selector)
    } else {
        sortedBy(selector)
    }

fun <T, K> compareBy(
    descending: Boolean,
    comparator: Comparator<K>,
    selector: (T) -> K,
): Comparator<T> =
    if (descending) {
        compareByDescending(comparator, selector)
    } else {
        compareBy(comparator, selector)
    }
