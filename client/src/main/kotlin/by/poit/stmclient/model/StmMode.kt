package by.poit.stmclient.model

enum class StmMode(val setCommand: StmCommand) {
    MANUAL(StmCommand.SET_MODE_MANUAL),
    AUTO(StmCommand.SET_MODE_AUTO)
}