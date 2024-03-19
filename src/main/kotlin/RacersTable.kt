import RacerTableHeader.DURATION
import RacerTableHeader.FINISH_TIME
import RacerTableHeader.GATE
import RacerTableHeader.ID
import RacerTableHeader.NAME
import RacerTableHeader.START_TIME
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize.Max
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import domain.Gate
import domain.Racer

private enum class RacerTableHeader(val label: String) {
    ID("ID"),
    NAME("Name"),
    START_TIME("Start Time"),
    FINISH_TIME("Finish Time"),
    DURATION("Duration"),
    GATE("Gate"),
}

@Composable
fun RacersTable(
    racers: List<Racer>,
    onAddRacer: (Racer) -> Unit,
    allowAssigningTimes: Boolean,
    onAssignGateTime: (Gate, Int) -> Unit,
    onClearRacerTime: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sortBy = remember { mutableStateOf(ID) }
    val sortDescending = remember { mutableStateOf(false) }

    val sortedRacers =
        remember(racers, sortBy.value, sortDescending.value) {
            racers
                .sortedBy(descending = sortDescending.value) { it.id }
                .sortedWith(
                    compareBy(
                        descending = sortDescending.value,
                        comparator = if (sortDescending.value) nullsFirst() else nullsLast(),
                    ) {
                        @Suppress("UNCHECKED_CAST")
                        when (sortBy.value) {
                            ID -> it.id
                            NAME -> it.name
                            START_TIME -> it.times?.start
                            FINISH_TIME -> it.times?.finish
                            DURATION -> it.times?.duration
                            GATE -> it.times?.gate
                        } as Comparable<Any>?
                    },
                )
        }
    var showNewRacerDialog by remember { mutableStateOf(false) }
    Column(
        modifier = modifier.width(Max),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (showNewRacerDialog) {
            NewRacerDialog(
                onDismissRequest = { showNewRacerDialog = false },
                onAddRacer = {
                    showNewRacerDialog = false
                    onAddRacer(it)
                },
            )
        }

        val rowModifier = Modifier.fillMaxWidth()
        val rowArrangement = Arrangement.spacedBy(0.dp)

        var idWidth by remember(racers, sortBy.value, sortDescending.value) { mutableStateOf(0) }
        var nameWidth by remember(racers, sortBy.value, sortDescending.value) { mutableStateOf(0) }
        var gateWidth by remember(racers, sortBy.value, sortDescending.value) { mutableStateOf(0) }
        var startTimeWidth by remember(racers, sortBy.value, sortDescending.value) { mutableStateOf(0) }
        var finishTimeWidth by remember(racers, sortBy.value, sortDescending.value) { mutableStateOf(0) }
        var durationWidth by remember(racers, sortBy.value, sortDescending.value) { mutableStateOf(0) }

        val columnModifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
        val idModifier =
            columnModifier
                .onGloballyPositioned { idWidth = maxOf(idWidth, it.size.width) }
                .defaultMinSize(minWidth = idWidth.dp)
        val nameModifier =
            columnModifier
                .onGloballyPositioned { nameWidth = maxOf(nameWidth, it.size.width) }
                .defaultMinSize(minWidth = nameWidth.dp)
        val gateModifier =
            columnModifier
                .onGloballyPositioned { gateWidth = maxOf(gateWidth, it.size.width) }
                .defaultMinSize(minWidth = gateWidth.dp)
        val startTimeModifier =
            columnModifier
                .onGloballyPositioned { startTimeWidth = maxOf(startTimeWidth, it.size.width) }
                .defaultMinSize(minWidth = startTimeWidth.dp)
        val finishTimeModifier =
            columnModifier
                .onGloballyPositioned { finishTimeWidth = maxOf(finishTimeWidth, it.size.width) }
                .defaultMinSize(minWidth = finishTimeWidth.dp)
        val durationModifier =
            columnModifier
                .onGloballyPositioned { durationWidth = maxOf(durationWidth, it.size.width) }
                .defaultMinSize(minWidth = durationWidth.dp)

        Row(
            modifier = rowModifier.background(MaterialTheme.colorScheme.surfaceVariant),
            horizontalArrangement = rowArrangement,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RacerHeaderText(
                header = ID,
                sortBy = sortBy,
                sortDescending = sortDescending,
                modifier = idModifier,
            )
            RacerHeaderText(
                header = NAME,
                sortBy = sortBy,
                sortDescending = sortDescending,
                modifier = nameModifier,
            )
            RacerHeaderText(
                header = START_TIME,
                sortBy = sortBy,
                sortDescending = sortDescending,
                modifier = startTimeModifier,
            )
            RacerHeaderText(
                header = FINISH_TIME,
                sortBy = sortBy,
                sortDescending = sortDescending,
                modifier = finishTimeModifier,
            )
            RacerHeaderText(
                header = DURATION,
                sortBy = sortBy,
                sortDescending = sortDescending,
                modifier = durationModifier,
            )
            RacerHeaderText(
                header = GATE,
                sortBy = sortBy,
                sortDescending = sortDescending,
                modifier = gateModifier,
            )
        }
        sortedRacers.forEachIndexed { idx, racer ->
            val background =
                if (idx % 2 == 0) {
                    MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                } else {
                    MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                }
            Row(
                modifier = rowModifier.background(background),
                horizontalArrangement = rowArrangement,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = racer.id.toString(), modifier = idModifier)
                Text(text = racer.name, modifier = nameModifier)
                Text(text = racer.times?.start?.fmt() ?: "--", modifier = startTimeModifier)
                Text(text = racer.times?.finish?.fmt() ?: "--", modifier = finishTimeModifier)
                Text(text = racer.times?.duration?.fmt() ?: "--", modifier = durationModifier)
                Text(
                    text = racer.times?.gate?.title ?: "--",
                    modifier = gateModifier,
                    color = racer.times?.gate?.color ?: Color.Unspecified,
                )
                var showDialog by remember { mutableStateOf(false) }
                if (showDialog) {
                    ConfirmDialog(
                        body = { Text(text = "Are you sure you want to clear the times for ${racer.name}?") },
                        onConfirm = {
                            onClearRacerTime(racer.id)
                            showDialog = false
                        },
                        onDismissRequest = { showDialog = false },
                    )
                }
                FilledIconButton(
                    onClick = { showDialog = true },
                    enabled = racer.times != null,
                    colors = dangerIconButtonColors(),
                ) {
                    Icon(imageVector = Icons.Default.TimerOff, contentDescription = null)
                }
                Gate.entries.forEach { gate ->
                    FilledTonalIconButton(
                        onClick = { onAssignGateTime(gate, racer.id) },
                        enabled = allowAssigningTimes && racer.times == null,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(contentColor = gate.color),
                    ) {
                        Icon(imageVector = Icons.Default.Timer, contentDescription = null)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { showNewRacerDialog = true }) {
            Text(text = "Add Racer")
        }
    }
}

@Composable
private fun RacerHeaderText(
    header: RacerTableHeader,
    sortBy: MutableState<RacerTableHeader>,
    sortDescending: MutableState<Boolean>,
    modifier: Modifier = Modifier,
) {
    Text(
        text =
            getHeaderLabel(
                label = header.label,
                header = header,
                sortBy = sortBy.value,
                sortDescending = sortDescending.value,
            ),
        modifier =
            Modifier
                .clickable {
                    if (sortBy.value == header) {
                        sortDescending.value = !sortDescending.value
                    } else {
                        sortBy.value = header
                        sortDescending.value = false
                    }
                }.then(modifier),
        fontWeight = FontWeight.Bold,
    )
}

private fun getHeaderLabel(
    label: String,
    header: RacerTableHeader,
    sortBy: RacerTableHeader,
    sortDescending: Boolean,
): String {
    if (sortBy != header) return label
    val suffix = if (sortDescending) " ↓" else " ↑"
    return "$label$suffix"
}
