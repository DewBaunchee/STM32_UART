package by.poit.stmclient.view

import by.poit.stmclient.model.StmMode
import by.poit.stmclient.model.StmRgbSource
import javafx.beans.property.SimpleObjectProperty

class ClientSettings {
    val mode = SimpleObjectProperty(StmMode.AUTO)
    val rgbSource = SimpleObjectProperty(StmRgbSource.LIGHT)
}