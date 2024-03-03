package communication

import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortIOException
import kotlin.experimental.and

class Arduino(
    portDescriptor: String,
    private val baudRate: Int = 9600,
    private val dataBits: Int = 8,
    private val stopBits: Int = 1,
    private val parity: Int = 0,
) {
    private val port = SerialPort.getCommPort(portDescriptor)

    companion object {
        private const val PACKET_SIZE = 1
    }

    fun init() {
        requireNotStarted()
        port.baudRate = baudRate
        port.numDataBits = dataBits
        port.numStopBits = stopBits
        port.parity = parity
        if (!port.openPort()) throw IllegalStateException("Failed to open serial port '${port.systemPortName}'")
    }

    fun getState(): GateState {
        requireStarted()
        while (true) {
            val available = port.bytesAvailable()
            // FIXME: is this the best approach?
            if (available == -1) throw SerialPortIOException("Port disconnected")
            if (available != PACKET_SIZE) continue
            val buffer = ByteArray(PACKET_SIZE)
            port.readBytes(buffer, PACKET_SIZE)
            ack()

            // xxxxxxxAB
            // A -> gate1
            // B -> gate2
            val data = buffer[0]
            val gate1 = data and 0b00000010 != 0.toByte()
            val gate2 = data and 0b00000001 != 0.toByte()
            return GateState(gate1, gate2)
        }
    }

    fun close() {
        port.closePort()
    }

    fun reopen(): Boolean {
        port.closePort()
        return port.openPort()
    }

    private fun ack() {
        // send 1 random byte
        val count = port.writeBytes(byteArrayOf(0), 1)
        // FIXME
        if (count == -1) println("Failed to send ack")
        port.flushIOBuffers()
    }

    private fun requireStarted() {
        require(port.isOpen) { "Not started" }
    }

    private fun requireNotStarted() {
        require(!port.isOpen) { "Already started" }
    }
}
