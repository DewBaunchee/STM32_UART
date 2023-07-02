package by.poit.stmclient.model

enum class StmRgbSource(val setCommand: StmCommand) {
    ROTATION(StmCommand.SET_RGB_ON_ROTATION),
    LIGHT(StmCommand.SET_RGB_ON_LIGHT),
    TEMP(StmCommand.SET_RGB_ON_TEMP)
}