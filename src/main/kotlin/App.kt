import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import communication.Arduino
import theme.RaceTimingSystemTheme

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

    val isSystemInDarkTheme = isSystemInDarkTheme()
    var darkTheme by remember { mutableStateOf(isSystemInDarkTheme) }
    RaceTimingSystemTheme(darkTheme = darkTheme) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { darkTheme = !darkTheme },
                ) {
                    val icon = if (darkTheme) Icons.Default.LightMode else Icons.Default.DarkMode
                    Icon(imageVector = icon, contentDescription = null)
                }
            },
        ) {
            val currArduino = arduino
            if (currArduino == null) {
                SelectSerialPortScreen(
                    onSelected = {
                        // FIXME: catch init error
                        arduino = Arduino(portDescriptor = it.systemPortPath).apply { init() }
                    },
                )
            } else {
                MainScreen(arduino = currArduino)
            }
        }
    }
}
