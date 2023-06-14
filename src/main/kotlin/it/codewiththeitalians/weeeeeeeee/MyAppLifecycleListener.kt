package it.codewiththeitalians.weeeeeeeee

import com.intellij.ide.AppLifecycleListener
import com.intellij.openapi.components.service

class MyAppLifecycleListener : AppLifecycleListener {

    override fun appFrameCreated(commandLineArgs: MutableList<String>) {
        service<PizzaService>()
    }
}
