package it.codewiththeitalians.weeeeeeeee

import com.intellij.application.subscribe
import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import javax.swing.UIManager

class NyanService: Disposable, LafManagerListener {

    init {
        LafManagerListener.TOPIC.subscribe(this, this)
        updateProgressBarUi()
    }

    private fun updateProgressBarUi() {
        UIManager.put("ProgressBarUI", NyanProgressBarUi::class.java.name)
        UIManager.getDefaults()[NyanProgressBarUi::class.java.name] = NyanProgressBarUi::class.java
    }

    override fun dispose() {
        // fuck it
    }

    override fun lookAndFeelChanged(source: LafManager) {
        updateProgressBarUi()
    }
}
