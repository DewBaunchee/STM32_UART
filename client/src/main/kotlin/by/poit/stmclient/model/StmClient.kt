package by.poit.stmclient.model

import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPort.TIMEOUT_READ_BLOCKING
import com.fazecast.jSerialComm.SerialPort.TIMEOUT_WRITE_BLOCKING
import com.fazecast.jSerialComm.SerialPortIOException
import java.nio.ByteBuffer
import java.time.Instant

typealias DataHandler = (StmData) -> Unit

const val MAX_INTENSITY = 4095

class StmClient(
    private val port: SerialPort,
    private val dataHandler: DataHandler,
    private val closeHandler: () -> Unit
) : Thread() {

    init {
        start()
    }

    companion object {

        fun open(port: SerialPort, dataHandler: DataHandler, closeHandler: () -> Unit): StmClient? {
            if (port.isOpen) return null

            port.baudRate = 115200
            port.parity = SerialPort.NO_PARITY
            port.numDataBits = 8

            port.setComPortTimeouts(TIMEOUT_READ_BLOCKING or TIMEOUT_WRITE_BLOCKING, 0, 0)

            if (!port.openPort()) return null

            return StmClient(port, dataHandler, closeHandler)
        }
    }

    fun sendCommand(command: StmCommand) {
        try {
            port.outputStream.write(ByteArray(1) { command.value })
            port.outputStream.flush()
        } catch (e: SerialPortIOException) {
            // ignore
        }
    }

    override fun run() {
        val messageLength = 6
        val startTime = Instant.now().toEpochMilli()
        while (!interrupted()) {
            if (!port.isOpen) {
                interrupt()
                closeHandler()
                return
            }
            val message = port.inputStream.readNBytes(messageLength)

            (0..2).map { i ->
                ByteBuffer.wrap(
                    ByteArray(2) { message[2 * i + it] }
                        .reversedArray()
                        .plusLeft(4)
                ).int.toDouble()
            }.apply {
                dataHandler(
                    StmData(
                        Instant.now().toEpochMilli() - startTime,
                        180 * this[0] / MAX_INTENSITY,
                        100 * this[1] / MAX_INTENSITY,
                        this[2] * 3.3 * 100 / MAX_INTENSITY
                    )
                )
            }
        }
    }

    fun close() {
        port.closePort()
        interrupt()
        closeHandler()
    }

    private fun ByteArray.plusLeft(untilLength: Int): ByteArray {
        val additional = untilLength - size
        if (additional <= 0) return this
        return ByteArray(additional) { 0 }.plus(this)
    }
}