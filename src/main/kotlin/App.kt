import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import communication.Arduino

@Composable
@Preview
fun App() {
    var arduino by remember { mutableStateOf<Arduino?>(null) }

    DisposableEffect(arduino) {
        val curr = arduino
        onDispose {
            curr?.close()
        }
    }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Scaffold {
            val currArduino = arduino
            if (currArduino == null) {
                SelectSerialPortScreen(
                    onSelected = {
                        // FIXME: catch init error
                        arduino = Arduino(portDescriptor = it.systemPortPath).apply { init() }
                    },
                )
            } else {
                MainContentScreen(arduino = currArduino)
            }
        }
    }
}
