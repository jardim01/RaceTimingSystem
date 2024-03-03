package composables.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@Composable
fun DropDownButton(
    text: @Composable () -> Unit,
    expanded: Boolean,
    onClick: () -> Unit,
    leadingIcon: @Composable (() -> Unit)?,
) {
    Button(onClick = onClick) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leadingIcon != null) leadingIcon()
            text()
            val vector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown
            Icon(imageVector = vector, contentDescription = null)
        }
    }
}
