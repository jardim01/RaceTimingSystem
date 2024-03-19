package domain

import androidx.compose.ui.graphics.Color

enum class Gate(val number: Int, val title: String, val color: Color) {
    BLUE_GATE(number = 1, title = "Blue", color = Color(0xFF1E90FF)),
    YELLOW_GATE(number = 2, title = "Yellow", color = Color(0xFFFFC800)),
}
