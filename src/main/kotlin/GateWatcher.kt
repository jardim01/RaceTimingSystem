import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import com.fazecast.jSerialComm.SerialPortIOException
import communication.Arduino
import communication.GateState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime

@Composable
fun GateWatcher(
    arduino: Arduino,
    gateState: MutableState<GateState>,
    startTime: MutableState<LocalDateTime?>,
    gate1FinishTime: MutableState<LocalDateTime?>,
    gate2FinishTime: MutableState<LocalDateTime?>,
    communicationError: MutableState<Boolean>,
) {
    LaunchedEffect(arduino) {
        withContext(Dispatchers.IO) {
            while (true) {
                try {
                    val state = arduino.getState().also { gateState.value = it }
                    val now = LocalDateTime.now()
                    if (startTime.value != null) {
                        if (state.gate1 && gate1FinishTime.value == null) gate1FinishTime.value = now
                        if (state.gate2 && gate2FinishTime.value == null) gate2FinishTime.value = now
                    }
                } catch (e: SerialPortIOException) {
                    e.printStackTrace()
                    // port disconnected
                    communicationError.value = true
                    while (!arduino.reopen()) continue
                    communicationError.value = false
                }
            }
        }
    }
}
