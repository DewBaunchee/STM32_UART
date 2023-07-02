package by.poit.stmclient.app

import by.poit.stmclient.view.MainView
import tornadofx.launch
import tornadofx.App

class StmClientApp : App(MainView::class)

fun main(args: Array<String>) {
    launch<StmClientApp>(args)
}

