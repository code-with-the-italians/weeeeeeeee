package it.codewiththeitalians.weeeeeeeee

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBLabel
import com.intellij.ui.scale.JBUIScale.scale
import com.intellij.util.Alarm
import com.intellij.util.ui.components.BorderLayoutPanel
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JProgressBar

class PizzaTimeAction : AnAction(), DumbAware {

    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun actionPerformed(e: AnActionEvent) {
        PizzaDialogWrapper(e.project).show()
    }

    @Suppress("MagicNumber")
    private class PizzaDialogWrapper(project: Project?) : DialogWrapper(project) {

        private val pbList: MutableList<JProgressBar> = ArrayList()
        private val winList: MutableList<JComponent> = ArrayList()
        private val alarm = Alarm(disposable)
        private var isRaceDone = false

        init {
            init()
            buttonMap
        }

        override fun createCenterPanel(): JComponent {
            val panel = JPanel()
            panel.setLayout(BoxLayout(panel, BoxLayout.Y_AXIS))

            panel.add(createPanel(indeterminate = false, modeless = false))
            panel.add(createPanel(indeterminate = false, modeless = true))
            panel.add(createPanel(indeterminate = true, modeless = false))
            panel.add(createPanel(indeterminate = true, modeless = true))

            for ((index, pb) in pbList.withIndex()) {
                if (!pb.isIndeterminate) {
                    val request: Runnable = object : Runnable {
                        override fun run() {
                            if (pb.value < pb.maximum) {
                                pb.value++
                                alarm.addRequest(this, 100)
                            } else if (!isRaceDone) {
                                isRaceDone = true
                                winList[index].isVisible = true
                            }
                        }
                    }
                    alarm.addRequest(request, 200, ModalityState.any())
                }
            }
            return panel
        }

        private fun createPanel(indeterminate: Boolean, modeless: Boolean): JComponent {
            val text = if (indeterminate) "indeterminate" else "determinate"
            val label = JLabel(text)
            val progress = JProgressBar(0, pickMaximum())
            progress.setIndeterminate(indeterminate)
            progress.setValue(0)
            if (modeless) {
                progress.putClientProperty("ProgressBar.stripeWidth", 2)
            }
            pbList.add(progress)

            val victory = JBLabel("🏆")
            victory.isVisible = false
            winList.add(victory)

            val panel = JPanel()
            panel.setLayout(BoxLayout(panel, BoxLayout.Y_AXIS))
            panel.add(label)

            val row = BorderLayoutPanel(scale(6), 0).apply {
                addToCenter(progress)
                addToRight(victory)
            }
            panel.add(row)
            panel.add(Box.createVerticalStrut(5))
            return panel
        }

        private fun pickMaximum() = (80..110).random()

        override fun createActions() = arrayOf(okAction)
    }
}
