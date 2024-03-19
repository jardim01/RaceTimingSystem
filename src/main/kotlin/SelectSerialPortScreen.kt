import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize.Max
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fazecast.jSerialComm.SerialPort

@Composable
fun SelectSerialPortScreen(onSelected: (SerialPort) -> Unit) {
    var serialPort by remember { mutableStateOf<SerialPort?>(null) }
    var serialPorts by remember { mutableStateOf(SerialPort.getCommPorts()) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Serial Port",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
            )
            if (serialPorts.isEmpty()) {
                Text(text = "No serial ports found", style = MaterialTheme.typography.labelMedium)
            } else {
                Column(modifier = Modifier.width(Max).selectableGroup()) {
                    serialPorts.forEach {
                        val selected = serialPort == it
                        val onClick = { serialPort = it }
                        LabeledRadioButton(
                            selected = selected,
                            onClick = onClick,
                            label = { Text(text = it.portDescription) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledTonalIconButton(
                    onClick = {
                        serialPort = null
                        serialPorts = SerialPort.getCommPorts()
                    },
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                }
                Button(
                    onClick = {
                        val curr = serialPort
                        if (curr != null) onSelected(curr)
                    },
                    enabled = serialPort != null,
                ) {
                    Text(text = "Connect")
                }
            }
        }
    }
}
