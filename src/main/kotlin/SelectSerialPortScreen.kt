import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
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
                Column(modifier = Modifier.selectableGroup()) {
                    serialPorts.forEach {
                        val selected = serialPort == it
                        val onClick = { serialPort = it }
                        Row(
                            modifier = Modifier
                                .semantics { role = Role.RadioButton }
                                .selectable(selected = selected, onClick = onClick, role = Role.RadioButton),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = selected,
                                onClick = onClick,
                            )
                            Text(text = it.portDescription)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledIconButton(
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
