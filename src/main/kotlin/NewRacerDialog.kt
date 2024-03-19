import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import domain.Racer

@Composable
fun NewRacerDialog(
    onDismissRequest: () -> Unit,
    onAddRacer: (Racer) -> Unit,
) {
    var id by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    val validId = id.toIntOrNull()
    val validName = if (name.trim().length < 3) null else name.trim()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Add Racer") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = id,
                    onValueChange = { id = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text(text = "ID") },
                    singleLine = true,
                    isError = validId == null,
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = "Name") },
                    singleLine = true,
                    isError = validName == null,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (validId != null && validName != null) {
                        val racer = Racer(id = validId, name = validName, times = null)
                        onAddRacer(racer)
                        id = ""
                        name = ""
                    }
                },
                enabled = validId != null && validName != null,
            ) {
                Text(text = "Add")
            }
        },
        dismissButton = {
            FilledTonalButton(
                onClick = onDismissRequest,
            ) {
                Text(text = "Cancel")
            }
        },
    )
}
