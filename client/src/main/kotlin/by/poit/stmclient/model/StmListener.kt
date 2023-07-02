package by.poit.stmclient.model

import com.fazecast.jSerialComm.SerialPort
import kotlin.concurrent.fixedRateTimer


class StmListener {

    fun connect(comPort: String, dataHandler: DataHandler, closeHandler: () -> Unit): StmClient? {
        return SerialPort.getCommPorts()
            .find { it.systemPortName == comPort }
            ?.let { StmClient.open(it, dataHandler, closeHandler) }
    }

    fun getFreePorts(): List<String> {
        return SerialPort.getCommPorts().map { it.systemPortName }
    }

    fun listen(action: (List<String>) -> Unit) {
        fixedRateTimer("comCheck", false, 0, 100) { action(getFreePorts()) }
    }
}