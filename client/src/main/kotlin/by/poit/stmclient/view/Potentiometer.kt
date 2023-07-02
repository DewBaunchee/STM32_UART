package by.poit.stmclient.view

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import java.nio.file.Files
import java.nio.file.Path

class Potentiometer : ImageView() {

    init {
        image = Image(Files.newInputStream(Path.of("assets/potentiometer.png")))
    }

    fun setAngle(angle: Double) {
        rotate = angle
    }
}