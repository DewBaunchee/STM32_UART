package by.poit.stmclient.view

import by.poit.stmclient.model.*
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tornadofx.*
import java.text.DecimalFormat

val defaultBorderColor = c("#AAA")

class MainView : View("STM CLIENT") {

    override fun onDock() {
        super.onDock()
        primaryStage.isMaximized = true
    }

    private val stmData = FXCollections.observableArrayList<StmData>()
    private val rotation = Series<Number, Number>().apply {
        name = "Rotation"
        data.bind(stmData) { XYChart.Data(it.time, it.rotation) }
    }
    private val light = Series<Number, Number>().apply {
        name = "Light"
        data.bind(stmData) { XYChart.Data(it.time, it.light) }
    }
    private val temp = Series<Number, Number>().apply {
        name = "Temperature"
        data.bind(stmData) { XYChart.Data(it.time, it.temp) }
    }

    private val comPorts = FXCollections.observableArrayList<String>()
    private val listener = StmListener()
    private val settings = ClientSettings()
    private var client: StmClient? = null

    private lateinit var comPort: ComboBox<String>
    private lateinit var connect: Button
    private lateinit var disconnect: Button
    private lateinit var nextValue: Button

    private lateinit var potentiometer: Potentiometer

    init {
        listener.listen {
            val ports = listener.getFreePorts()
            if (ports.any { !comPorts.contains(it) }) {
                Platform.runLater { comPorts.setAll(ports) }
            }
        }
    }

    override val root = vbox {
        style { fontSize = 18.px }
        hbox {
            paddingAll = 10.0
            style {
                borderColor += box(defaultBorderColor)
                borderWidth += box(0.px, 0.px, 1.px, 0.px)
            }
            settings()

            hbox {
                spacing = 10.0
                paddingAll = 10
                alignment = Pos.CENTER

                style {
                    borderColor += box(defaultBorderColor)
                    borderWidth += box(0.px, 0.px, 0.px, 1.px)
                }

                potentiometer = Potentiometer()
                potentiometer.maxHeight(20.0)
                potentiometer.maxWidth(20.0)
                add(potentiometer)
            }

            control()
        }
        info()
    }

    private fun createClient() {
        client = listener.connect(
            comPort.selectionModel.selectedItem,
            { data ->
                Platform.runLater {
                    stmData.add(data)
                    if (stmData.size > 100) stmData.removeFirst()
                    potentiometer.setAngle(data.rotation)
                }
            }
        ) {
            Platform.runLater {
                client = null
                nextValue.isDisable = true
                connect.isDisable = false
                disconnect.isDisable = true
            }
        }?.also {
            nextValue.isDisable = false
            connect.isDisable = true
            disconnect.isDisable = false
        }?.also { client ->
            settings.apply {
                mode.listenValue { _, _, new ->
                    client.sendCommand(new.setCommand)
                }
                rgbSource.listenValue { _, _, new ->
                    client.sendCommand(new.setCommand)
                }
            }
        }
    }

    private fun HBox.settings() {
        hbox {
            spacing = 10.0
            alignment = Pos.CENTER_LEFT

            hbox {
                spacing = 10.0
                paddingAll = 10
                alignment = Pos.CENTER_LEFT
                togglegroup {
                    radiobutton("Manual") {
                        onMouseClicked = EventHandler {
                            settings.mode.set(StmMode.MANUAL)
                        }
                    }
                    radiobutton("Auto") {
                        onMouseClicked = EventHandler {
                            settings.mode.set(StmMode.AUTO)
                        }
                        fire()
                    }
                }
            }

            hbox {
                spacing = 10.0
                paddingAll = 10
                alignment = Pos.CENTER_LEFT
                togglegroup {
                    style {
                        borderColor += box(defaultBorderColor)
                        borderWidth += box(0.px, 0.px, 0.px, 1.px)
                    }
                    radiobutton("Display Rotation") {
                        onMouseClicked = EventHandler {
                            settings.rgbSource.set(StmRgbSource.ROTATION)
                        }
                    }
                    radiobutton("Display Light") {
                        onMouseClicked = EventHandler {
                            settings.rgbSource.set(StmRgbSource.LIGHT)
                        }
                        fire()
                    }
                    radiobutton("Display Temperature") {
                        onMouseClicked = EventHandler {
                            settings.rgbSource.set(StmRgbSource.TEMP)
                        }
                    }
                }
            }
        }
    }

    private fun HBox.control() {
        hbox {
            spacing = 10.0
            alignment = Pos.CENTER_RIGHT
            hgrow = Priority.ALWAYS

            style {
                borderColor += box(defaultBorderColor)
                borderWidth += box(0.px, 0.px, 0.px, 1.px)
            }

            nextValue = button("Next") {
                isDisable = true
                onAction = EventHandler { client?.sendCommand(StmCommand.NEXT_VALUE) }
            }
            connect = button("Connect") {
                onAction = EventHandler {
                    if (comPort.selectionModel.isEmpty) return@EventHandler

                    createClient()
                }
            }
            disconnect = button("Disconnect") {
                isDisable = true
                onAction = EventHandler { client?.close() }
            }
            button("Clear") { onAction = EventHandler { stmData.clear() } }
            comPort = combobox {
                style { cursor = Cursor.HAND }
                items = comPorts
                items.addListener(ListChangeListener {
                    if (!it.next()) return@ListChangeListener
                    if (it.list.size == it.addedSize) {
                        Platform.runLater {
                            selectionModel.select(0)
                        }
                    }
                })
            }

            shortcut("Enter") {
                if (client == null) {
                    connect.fire()
                    return@shortcut
                }
                nextValue.fire()
            }
        }
    }

    private fun VBox.info() {
        hbox {
            vgrow = Priority.ALWAYS
            paddingAll = 10

            tableview(stmData) {
                prefWidth = 500.0
                columnResizePolicy = SmartResize.POLICY

                val format = DecimalFormat("0.##")

                readonlyColumn("Time", StmData::time).remainingWidth()
                readonlyColumn("Rotation", StmData::rotation)
                    .remainingWidth()
                    .cellFormat {
                        text = format.format(it)
                    }
                readonlyColumn("Light", StmData::light)
                    .remainingWidth()
                    .cellFormat {
                        text = format.format(it)
                    }
                readonlyColumn("Temperature", StmData::temp)
                    .remainingWidth()
                    .cellFormat {
                        text = format.format(it)
                    }

                items.addListener(ListChangeListener { next ->
                    scrollTo(next.list.size - 1)
                })
            }

            vbox {
                hgrow = Priority.ALWAYS
                chart(rotation, "Rotation", "Degrees")
                chart(light, "Light", "Percent")
                chart(temp, "Temperature", "Degrees Celsius")
            }
        }
    }

    private fun VBox.chart(series: Series<Number, Number>, title: String, yAxisLabel: String) {
        linechart(
            title,
            NumberAxis().apply {
                isForceZeroInRange = false
            },
            NumberAxis()
        ) {
            vgrow = Priority.ALWAYS
            data.add(series)
            animated = false
            isLegendVisible = false

            yAxis.label = yAxisLabel
        }
    }
}

private fun <T> ObservableValue<T>.listenValue(listener: ChangeListener<T>) {
    listener.changed(this, null, value)
    addListener(listener)
}