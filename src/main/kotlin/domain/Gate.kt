package domain

import GATE1_COLOR
import GATE2_COLOR
import androidx.compose.ui.graphics.Color

enum class Gate(val title: String, val color: Color) {
    GATE_1("Gate 1", GATE1_COLOR),
    GATE_2("Gate 2", GATE2_COLOR),
}
