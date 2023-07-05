package it.codewiththeitalians.weeeeeeeee

import com.intellij.ui.JBColor
import com.intellij.ui.NewUI
import com.intellij.ui.scale.JBUIScale
import com.intellij.ui.scale.JBUIScale.scale
import com.intellij.util.ui.GraphicsUtil
import java.awt.BasicStroke
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Insets
import java.awt.Shape
import java.awt.geom.RoundRectangle2D
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.plaf.basic.BasicProgressBarUI
import kotlin.math.roundToInt
import kotlin.random.Random

@Suppress("MagicNumber")
internal class PizzaProgressBarUI : BasicProgressBarUI() {

    private val italianPaints = listOf(
        JBColor(FLAG_RED_LIGHT, FLAG_RED_DARK),
        JBColor(FLAG_WHITE_LIGHT, FLAG_WHITE_DARK),
        JBColor(FLAG_GREEN_LIGHT, FLAG_GREEN_DARK)
    )

    private val oldUiBorderColor = JBColor(0xCCCCCC, 0x555555)
    private val newUiBorderColor = JBColor(0xCCCCCC, 0x111111)
    private val borderColor
        get() = if (NewUI.isEnabled()) newUiBorderColor else oldUiBorderColor

    private var iconOffsetX = 0
    private var isGoingRight = true

    private var icon: Icon = PizzaIcons.PizzaGoingRight.resize(iconSize)

    private val iconSize
        get() = scale(20)


    private var isIndeterminate: Boolean = false
        set(value) {
            if (field != value) resetState()
            field = value
        }


    override fun getPreferredSize(c: JComponent): Dimension =
        Dimension(super.getPreferredSize(c).width, iconSize + c.insets.vertical)

    override fun paintIndeterminate(g2d: Graphics, c: JComponent) {
        isIndeterminate = true
        if (g2d !is Graphics2D) {
            return
        }

        GraphicsUtil.setupAAPainting(g2d)

        val insets = progressBar.insets
        val barWidth = progressBar.width - insets.horizontal
        val barHeight = progressBar.height - insets.vertical
        if (barWidth <= 0 || barHeight <= 0) {
            return
        }

        doDraw(insets, barWidth, barHeight, c, g2d)

        g2d.drawPizzaIcon(
            barY = insets.top,
            barHeight = barHeight,
            xOffset = iconOffsetX,
            isJittery = true
        )
    }

    override fun paintDeterminate(g2d: Graphics, c: JComponent) {
        isIndeterminate = false
        if (g2d !is Graphics2D) {
            return
        }

        GraphicsUtil.setupAAPainting(g2d)

        val insets = progressBar.insets
        val progress = progressBar.value.toFloat() / progressBar.maximum
        val barWidth = ((progressBar.width - insets.horizontal) * progress + iconSize * progress).roundToInt()

        val barHeight = progressBar.height - insets.vertical
        if (barWidth <= 0 || barHeight <= 0) {
            return
        }

        doDraw(insets, barWidth, barHeight, c, g2d)

        g2d.drawPizzaIcon(
            barY = insets.top,
            barHeight = barHeight,
            xOffset = insets.left + barWidth,
            isJittery = false
        )
    }

    private fun doDraw(
        insets: Insets,
        barWidth: Int,
        barHeight: Int,
        c: JComponent,
        g2d: Graphics2D,
    ) {
        val componentWidth = c.width
        val componentHeight = c.height

        updateIcon(barWidth)

        val barArc = scale(8f)
        val barShape = RoundRectangle2D.Float(1f, 1f, componentWidth - 2f, componentHeight - 2f, barArc, barArc)

        g2d.drawItalianFlag(insets, barHeight, barWidth, barShape)

        g2d.stroke = BasicStroke(scale(2f))
        g2d.color = borderColor
        g2d.draw(barShape)
    }

    private fun updateIcon(barWidth: Int) {
        val tick = JBUIScale.scale(1)
        if (isGoingRight) iconOffsetX += tick else iconOffsetX -= tick

        if (iconOffsetX > barWidth) {
            icon = PizzaIcons.PizzaGoingLeft.resize(iconSize)
            isGoingRight = false
        }
        if (iconOffsetX < -iconSize) {
            icon = PizzaIcons.PizzaGoingRight.resize(iconSize)
            isGoingRight = true
        }
    }

    private fun Graphics2D.drawItalianFlag(
        insets: Insets,
        barHeight: Int,
        barWidth: Int,
        barShape: Shape,
    ) {
        val oldClip = clip
        clip(barShape)

        val stripeHeight = (barHeight.toFloat() / italianPaints.size).roundToInt()
        for ((index, paint) in italianPaints.withIndex()) {
            this.paint = paint
            fillRect(insets.left, insets.top + stripeHeight * index, barWidth, stripeHeight)
        }

        clip(oldClip)
    }

    private fun Graphics2D.drawPizzaIcon(barY: Int, barHeight: Int, xOffset: Int, isJittery: Boolean) {
        val yOffset = barY + barHeight / 2 - iconSize / 2
        val jitterX = if (isJittery) Random.nextInt(4) - 2 else 0
        val jitterY = if (isJittery) Random.nextInt(4) - 2 else 0

        icon.paintIcon(
            progressBar,
            this,
            xOffset - iconSize / 2 + jitterX,
            yOffset + jitterY
        )
    }

    override fun getBoxLength(availableLength: Int, otherDimension: Int): Int = availableLength

    private fun resetState() {
        iconOffsetX = 0
        isGoingRight = true
        icon = PizzaIcons.PizzaGoingRight.resize(iconSize)
    }

    companion object {

        private const val FLAG_RED_LIGHT = 0xFF0000
        private const val FLAG_WHITE_LIGHT = 0xFFFFFF
        private const val FLAG_GREEN_LIGHT = 0x00DD00

        private const val FLAG_RED_DARK = 0xA80000
        private const val FLAG_WHITE_DARK = 0xDDDDDD
        private const val FLAG_GREEN_DARK = 0x008800
    }
}
