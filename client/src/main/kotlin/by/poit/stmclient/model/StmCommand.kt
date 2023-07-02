package by.poit.stmclient.model

enum class StmCommand(val value: Byte) {
    NEXT_VALUE(1),
    SET_MODE_MANUAL(2),
    SET_MODE_AUTO(3),
    SET_RGB_ON_ROTATION(4),
    SET_RGB_ON_LIGHT(5),
    SET_RGB_ON_TEMP(6)
}