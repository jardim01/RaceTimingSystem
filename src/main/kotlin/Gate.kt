import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import domain.Gate
import kotlin.time.Duration

@Composable
fun Gate(
    gate: Gate,
    breached: Boolean,
    time: Duration?,
    elapsedTime: Duration,
    error: Boolean,
    onClearTime: () -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        ConfirmDialog(
            body = { Text(text = "Are you sure you want to clear this gate's time?") },
            onConfirm = {
                onClearTime()
                showDialog = false
            },
            onDismissRequest = { showDialog = false },
        )
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        GateView(
            breached = breached,
            color = if (error) Color.Red else gate.color,
        ) {
            Text(
                text = (time ?: elapsedTime).fmt(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        FilledIconButton(
            onClick = { showDialog = true },
            enabled = time != null && !breached,
            colors = dangerIconButtonColors(),
        ) {
            Icon(imageVector = Icons.Default.Clear, contentDescription = null)
        }
    }
}
