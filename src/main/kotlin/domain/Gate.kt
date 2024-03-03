package domain

import androidx.compose.ui.graphics.Color

enum class Gate(val number: Int, val title: String, val color: Color) {
    GATE_1(1, "Gate 1", Color.Blue),
    GATE_2(2, "Gate 2", Color.Yellow),
}
