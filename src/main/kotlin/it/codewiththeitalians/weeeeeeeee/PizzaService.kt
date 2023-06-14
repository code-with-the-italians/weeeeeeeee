package it.codewiththeitalians.weeeeeeeee

import com.intellij.application.subscribe
import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import javax.swing.UIManager

@Service
class PizzaService : Disposable, LafManagerListener {

    init {
        LafManagerListener.TOPIC.subscribe(this, this)
        updateProgressBarUi()
    }

    private fun updateProgressBarUi() {
        UIManager.put("ProgressBarUI", PizzaProgressBarUiInstaller::class.java.name)
        UIManager.getDefaults()[PizzaProgressBarUiInstaller::class.java.name] = PizzaProgressBarUiInstaller::class.java
    }

    override fun dispose() {
        // TODO restore default ProgressBarUI
    }

    override fun lookAndFeelChanged(source: LafManager) {
        updateProgressBarUi()
    }
}
