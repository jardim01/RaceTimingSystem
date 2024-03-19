import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import communication.Arduino
import communication.GateState
import domain.Gate
import domain.Gate.BLUE_GATE
import domain.Gate.YELLOW_GATE
import domain.RaceTimes
import domain.Racer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import net.harawata.appdirs.AppDirsFactory
import kotlin.io.path.Path
import kotlin.io.path.createFile
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.io.path.moveTo
import kotlin.io.path.outputStream
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private val appDataPath = Path(AppDirsFactory.getInstance().getUserDataDir("RaceTimingSystem", null, null, false))
private val backupsPath = appDataPath.resolve("backups")
private val racersFilePath = appDataPath.resolve("racers.json")
private val startSoundResourcePath = "sounds/start.wav"

@Composable
fun MainScreen(arduino: Arduino) {
    val coroutineScope = rememberCoroutineScope()

    var racers by remember { mutableStateOf(loadRacers()) }
    var starting by remember { mutableStateOf(false) }
    val startTime = remember { mutableStateOf<LocalDateTime?>(null) }
    val gateState = remember { mutableStateOf(GateState(gate1 = false, gate2 = false)) }
    val gate1FinishTime = remember { mutableStateOf<LocalDateTime?>(null) }
    val gate2FinishTime = remember { mutableStateOf<LocalDateTime?>(null) }
    var delayError by remember { mutableStateOf(false) }
    val communicationError = remember { mutableStateOf(false) }

    LaunchedEffect(racers) {
        saveRacers(racers)
    }

    LaunchedEffect(arduino) {
        while (true) {
            delayError = Duration.between(arduino.lastPacketReceivedAt, LocalDateTime.now()) > 500.milliseconds
            delay(100.milliseconds)
        }
    }

    GateWatcher(
        arduino = arduino,
        gateState = gateState,
        startTime = startTime,
        gate1FinishTime = gate1FinishTime,
        gate2FinishTime = gate2FinishTime,
        communicationError = communicationError,
    )

    fun start() {
        if (!starting && startTime.value == null && !gateState.value.gate1 && !gateState.value.gate2) {
            coroutineScope.launch {
                starting = true
                playSound(startSoundResourcePath)
                startTime.value = LocalDateTime.now()
                gate1FinishTime.value = null
                gate2FinishTime.value = null
                starting = false
            }
        }
    }

    fun reset() {
        startTime.value = null
        gate1FinishTime.value = null
        gate2FinishTime.value = null
    }

    fun clearGate(gate: Gate) {
        when (gate) {
            BLUE_GATE -> gate1FinishTime.value = null
            YELLOW_GATE -> gate2FinishTime.value = null
        }
    }

    fun addRacer(racer: Racer) {
        racers = (racers + racer).distinctBy { it.id }
    }

    fun assignGateTime(
        gate: Gate,
        racerId: Int,
    ) {
        val t0 = startTime.value
        val t1 =
            when (gate) {
                BLUE_GATE -> gate1FinishTime.value
                YELLOW_GATE -> gate2FinishTime.value
            }
        if (t0 == null || t1 == null) return
        val raceTimes = RaceTimes(gate = gate, start = t0, finish = t1)
        // Cannot assign the same result to multiple racers
        if (racers.none { it.times == raceTimes }) {
            racers =
                racers.map {
                    if (it.id != racerId) it else it.copy(times = raceTimes)
                }
        }
    }

    fun clearTime(racerId: Int) {
        racers =
            racers.map {
                if (it.id != racerId) it else it.copy(times = null)
            }
    }

    val enableStart =
        !starting && startTime.value == null && !gateState.value.gate1 && !gateState.value.gate2

    Column(
        modifier = Modifier.fillMaxSize().padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        Chrono(
            startTime = startTime.value,
            onReset = if (startTime.value == null) null else ::reset,
            onStart = if (!enableStart) null else ::start,
            gateState = gateState.value,
            gate1FinishTime = gate1FinishTime.value,
            gate2FinishTime = gate2FinishTime.value,
            onClearGate = ::clearGate,
            communicationError = communicationError.value || delayError,
            modifier = Modifier.fillMaxWidth(),
        )
        RacersTable(
            racers = racers,
            onAddRacer = ::addRacer,
            allowAssigningTimes = gate1FinishTime.value != null && gate2FinishTime.value != null,
            onAssignGateTime = ::assignGateTime,
            onClearRacerTime = ::clearTime,
            modifier = Modifier.verticalScroll(state = rememberScrollState()).padding(horizontal = 32.dp),
        )
    }
}

@OptIn(ExperimentalSerializationApi::class)
fun loadRacers(): List<Racer> {
    try {
        return Json.decodeFromStream(racersFilePath.inputStream())
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return emptyList()
}

@OptIn(ExperimentalSerializationApi::class)
fun saveRacers(racers: List<Racer>) {
    if (!racersFilePath.exists()) {
        racersFilePath.createParentDirectories()
        racersFilePath.createFile()
    }
    try {
        val backupPath = backupsPath.resolve(System.nanoTime().toString() + "${System.currentTimeMillis()}.json")
        backupPath.createParentDirectories()
        racersFilePath.moveTo(backupPath)
        Json.encodeToStream(racers, racersFilePath.outputStream())
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
