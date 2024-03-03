import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fazecast.jSerialComm.SerialPortIOException
import communication.Arduino
import communication.GateState
import domain.Gate
import domain.Gate.GATE_1
import domain.Gate.GATE_2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toKotlinDuration

@Composable
fun MainContentScreen(arduino: Arduino) {
    var startTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var elapsedTime by remember(startTime) { mutableStateOf(0.seconds) }
    var communicationError by remember { mutableStateOf(false) }
    var gateState by remember { mutableStateOf(GateState(gate1 = false, gate2 = false)) }
    var gate1Time by remember { mutableStateOf<Duration?>(null) }
    var gate2Time by remember { mutableStateOf<Duration?>(null) }

    LaunchedEffect(arduino) {
        withContext(Dispatchers.IO) {
            while (true) {
                try {
                    gateState = arduino.waitStateChange(gateState)
                    val now = LocalDateTime.now()
                    if (startTime != null) {
                        if (gateState.gate1 && gate1Time == null) {
                            gate1Time =
                                java.time.Duration.between(startTime, now).toKotlinDuration()
                        }
                        if (gateState.gate2 && gate2Time == null) {
                            gate2Time =
                                java.time.Duration.between(startTime, now).toKotlinDuration()
                        }
                    }
                } catch (e: SerialPortIOException) {
                    // port disconnected
                    communicationError = true
                    while (!arduino.tryReopen()) continue
                    communicationError = false
                }
            }
        }
    }

    LaunchedEffect(startTime) {
        val t0 = startTime
        if (t0 != null) {
            while (true) {
                elapsedTime = java.time.Duration.between(t0, LocalDateTime.now()).toKotlinDuration()
                delay(100)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = elapsedTime.fmt(),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Black,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    if (!gateState.gate1 && !gateState.gate2) {
                        gate1Time = null
                        gate2Time = null
                        startTime = LocalDateTime.now()
                    }
                }, enabled = startTime == null && !gateState.gate1 && !gateState.gate2) {
                    Text(text = "Start")
                }
                // FIXME: confirm reset
                Button(onClick = { startTime = null }, enabled = startTime != null, colors = dangerButtonColors()) {
                    Text(text = "Reset")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // FIXME: confirm clear?
            Gate(
                gate = GATE_1,
                breached = gateState.gate1,
                time = gate1Time,
                elapsedTime = elapsedTime,
                error = communicationError,
                onClearTime = { gate1Time = null },
            )
            // FIXME: confirm clear?
            Gate(
                gate = GATE_2,
                breached = gateState.gate2,
                time = gate2Time,
                elapsedTime = elapsedTime,
                error = communicationError,
                onClearTime = { gate2Time = null },
            )
        }
    }
}

@Composable
fun dangerButtonColors() =
    ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
    )

@Composable
private fun Gate(
    gate: Gate,
    breached: Boolean,
    time: Duration?,
    elapsedTime: Duration,
    error: Boolean,
    onClearTime: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        GateView(
            breached = breached,
            color = if (error) Color.Red else gate.color,
        ) {
            Row {
                Text(text = "${gate.title}: ", fontWeight = FontWeight.Bold)
                Text(text = (time ?: elapsedTime).fmt())
            }
        }
        // FIXME: confirm clear?
        Button(
            onClick = onClearTime,
            enabled = time != null && !breached,
            colors = dangerButtonColors(),
        ) {
            Text(text = "Clear")
        }
    }
}

private fun Duration.fmt(): String {
    val minutes = inWholeMinutes
    val seconds = inWholeSeconds - minutes * 60
    val milliseconds = inWholeMilliseconds - minutes * 60 * 1000 - seconds * 1000
    return String.format("%02d:%02d.%03d", minutes, seconds, milliseconds)
}
