import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.fazecast.jSerialComm.SerialPort
import composables.common.DropDownBox

@Composable
fun SelectSerialPortScreen(onSelected: (SerialPort) -> Unit) {
    var serialPort by remember { mutableStateOf<SerialPort?>(null) }
    var serialPorts by remember { mutableStateOf(SerialPort.getCommPorts()) }
    Column {
        DropDownBox(
            label = serialPort?.descriptivePortName ?: "Select serial port",
            options = serialPorts.toList(),
            optionLabel = { it.descriptivePortName },
            selectedOption = serialPort,
            onOptionClick = { serialPort = it },
        )
        Row {
            Button(onClick = {
                serialPort = null
                serialPorts = SerialPort.getCommPorts()
            }) {
                Text(text = "Refresh")
            }
            Button(onClick = {
                val curr = serialPort
                if (curr != null) onSelected(curr)
            }, enabled = serialPort != null) {
                Text(text = "OK")
            }
        }
    }
}
