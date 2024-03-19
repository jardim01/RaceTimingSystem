import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import communication.GateState
import domain.Gate
import domain.Gate.BLUE_GATE
import domain.Gate.YELLOW_GATE
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun Chrono(
    startTime: LocalDateTime?,
    onStart: (() -> Unit)?,
    onReset: (() -> Unit)?,
    gateState: GateState,
    gate1FinishTime: LocalDateTime?,
    gate2FinishTime: LocalDateTime?,
    onClearGate: (Gate) -> Unit,
    communicationError: Boolean,
    modifier: Modifier = Modifier,
) {
    var elapsedTime by remember(startTime) { mutableStateOf(0.seconds) }
    val gate1Duration =
        if (startTime == null || gate1FinishTime == null) {
            null
        } else {
            Duration.between(startTime, gate1FinishTime)
        }
    val gate2Duration =
        if (startTime == null || gate2FinishTime == null) {
            null
        } else {
            Duration.between(startTime, gate2FinishTime)
        }

    LaunchedEffect(startTime) {
        if (startTime != null) {
            while (true) {
                elapsedTime = Duration.between(startTime, LocalDateTime.now())
                delay(100)
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = elapsedTime.fmt(),
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Black,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { onStart?.invoke() },
                enabled = onStart != null,
            ) {
                Text(text = "Start")
            }
            var showDialog by remember { mutableStateOf(false) }
            if (showDialog && onReset != null) {
                ConfirmDialog(
                    body = {
                        Text(text = "Are you sure you want to reset the stopwatch?\nThis will also clear the gate times.")
                    },
                    onConfirm = {
                        onReset()
                        showDialog = false
                    },
                    onDismissRequest = { showDialog = false },
                )
            }
            Button(
                onClick = { showDialog = true },
                enabled = onReset != null,
                colors = dangerButtonColors(),
            ) {
                Text(text = "Reset")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Gate(
            gate = BLUE_GATE,
            breached = gateState.gate1,
            time = gate1Duration,
            elapsedTime = elapsedTime,
            error = communicationError,
            onClearTime = { onClearGate(BLUE_GATE) },
        )
        Gate(
            gate = YELLOW_GATE,
            breached = gateState.gate2,
            time = gate2Duration,
            elapsedTime = elapsedTime,
            error = communicationError,
            onClearTime = { onClearGate(YELLOW_GATE) },
        )
    }
}
