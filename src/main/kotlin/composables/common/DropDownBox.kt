package composables.common

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun <T> DropDownBox(
    label: String,
    leadingIcon: (@Composable () -> Unit)? = null,
    options: List<T>,
    optionLabel: (T) -> String,
    selectedOption: T?,
    onOptionClick: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        DropDownButton(
            text = { Text(text = label) },
            expanded = expanded,
            onClick = { expanded = true },
            leadingIcon = leadingIcon,
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = optionLabel(option)) },
                    onClick = {
                        onOptionClick(option)
                        expanded = false
                    },
                    leadingIcon = {
                        val vector =
                            if (selectedOption == option) {
                                Icons.Default.RadioButtonChecked
                            } else {
                                Icons.Default.RadioButtonUnchecked
                            }
                        Icon(imageVector = vector, contentDescription = null)
                    },
                )
            }
        }
    }
}
