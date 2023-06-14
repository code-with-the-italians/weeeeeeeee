package it.codewiththeitalians.weeeeeeeee

import com.intellij.ui.JBColor
import com.intellij.ui.scale.JBUIScale
import com.intellij.ui.util.preferredHeight
import com.intellij.ui.util.preferredWidth
import com.intellij.util.ui.GraphicsUtil
import com.intellij.util.ui.UIUtil
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Insets
import java.awt.LinearGradientPaint
import java.awt.geom.AffineTransform
import java.awt.geom.RoundRectangle2D
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.SwingConstants
import javax.swing.plaf.basic.BasicProgressBarUI
import kotlin.math.roundToInt
import kotlin.random.Random

internal class PizzaProgressBarUI : BasicProgressBarUI() {

    private val italianPaints = listOf(
        JBColor(FLAG_RED_LIGHT, FLAG_RED_DARK),
        JBColor(FLAG_WHITE_LIGHT, FLAG_WHITE_DARK),
        JBColor(FLAG_GREEN_LIGHT, FLAG_GREEN_DARK)
    )

    private var iconOffsetX = 0
    private var isGoingRight = true

    private var icon: Icon = PizzaIcons.PizzaRight.resize(iconSize)

    private val iconSize
        get() = JBUIScale.scale(20)

    override fun getPreferredSize(c: JComponent): Dimension =
        Dimension(super.getPreferredSize(c).width, iconSize + c.insets.vertical)

    override fun paintIndeterminate(g2d: Graphics, c: JComponent) {
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

        val componentWidth = c.preferredWidth
        val componentHeight = c.preferredHeight

        updateIcon(barWidth)

        g2d.drawItalianFlag(componentWidth, componentHeight, insets, barHeight, barWidth)
        g2d.drawPizzaIcon(insets.top, barHeight)
    }

    private fun updateIcon(barWidth: Int) {
        val tick = JBUIScale.scale(1)
        if (isGoingRight) iconOffsetX += tick else iconOffsetX -= tick

        if (iconOffsetX > barWidth) {
            icon = PizzaIcons.PizzaLeft.resize(iconSize)
            isGoingRight = false
        }
        if (iconOffsetX < -iconSize) {
            icon = PizzaIcons.PizzaRight.resize(iconSize)
            isGoingRight = true
        }
    }

    private fun Graphics2D.drawItalianFlag(
        componentWidth: Int,
        componentHeight: Int,
        insets: Insets,
        barHeight: Int,
        barWidth: Int
    ) {
        val barArc = JBUIScale.scale(8f)
        val barShape = RoundRectangle2D.Float(1f, 1f, componentWidth - 2f, componentHeight - 2f, barArc, barArc)
        val oldClip = clip
        clip(barShape)

        val stripeHeight = (barHeight.toFloat() / italianPaints.size).roundToInt()
        for ((index, paint) in italianPaints.withIndex()) {
            this.paint = paint
            fillRect(insets.left, insets.top + stripeHeight * index, barWidth, stripeHeight)
        }

        clip(oldClip)
    }

    private fun Graphics2D.drawPizzaIcon(barY: Int, barHeight: Int) {
        val yOffset = barY + barHeight / 2 - iconSize / 2
        val jitterX = Random.nextInt(4) - 2
        val jitterY = Random.nextInt(4) - 2
        icon.paintIcon(progressBar, this, iconOffsetX - iconSize / 2 + jitterX, yOffset + jitterY)
    }

    override fun paintDeterminate(g: Graphics, c: JComponent) {
        if (g !is Graphics2D) {
            return
        }
        if (progressBar.orientation !== SwingConstants.HORIZONTAL || !c.componentOrientation.isLeftToRight) {
            super.paintDeterminate(g, c)
            return
        }
        val config = GraphicsUtil.setupAAPainting(g)
        val b = progressBar.insets // area for border
        val w = progressBar.width
        var h = progressBar.preferredSize.height
        if (!isEven(c.height - h)) h++
        val barRectWidth = w - (b.right + b.left)
        val barRectHeight = h - (b.top + b.bottom)
        if (barRectWidth <= 0 || barRectHeight <= 0) {
            return
        }
        val amountFull = getAmountFull(b, barRectWidth, barRectHeight)
        val parent = c.parent
        val background = if (parent != null) parent.background else UIUtil.getPanelBackground()
        g.setColor(background)
        if (c.isOpaque) {
            g.fillRect(0, 0, w, h)
        }
        val R = JBUIScale.scale(8f)
        val R2 = JBUIScale.scale(9f)
        val off = JBUIScale.scale(1f)
        g.translate(0, (c.height - h) / 2)
        g.color = progressBar.foreground
        g.fill(RoundRectangle2D.Float(0f, 0f, w - off, h - off, R2, R2))
        g.color = background
        g.fill(RoundRectangle2D.Float(off, off, w - 2f * off - off, h - 2f * off - off, R, R))
        g.paint = LinearGradientPaint(
            0f,
            JBUIScale.scale(2).toFloat(),
            0f,
            (h - JBUIScale.scale(6)).toFloat(),
            floatArrayOf(
                ONE_OVER_THREE * 1,
                ONE_OVER_THREE * 2,
                ONE_OVER_THREE * 3,
            ),
            arrayOf(Color(0xA80000), Color.WHITE, Color(0x008800)),
        )
        PizzaIcons.PizzaLeft.paintIcon(progressBar, g, amountFull - JBUIScale.scale(10), -JBUIScale.scale(6))
        g.fill(RoundRectangle2D.Float(2f * off, 2f * off, amountFull - JBUIScale.scale(5f), h - JBUIScale.scale(5f), JBUIScale.scale(7f), JBUIScale.scale(7f)))
        g.translate(0, -(c.height - h) / 2)

        // Deal with possible text painting
        if (progressBar.isStringPainted) {
            paintString(
                g,
                b.left,
                b.top,
                barRectWidth,
                barRectHeight,
                amountFull,
                b,
            )
        }
        config.restore()
    }

    private fun paintString(g: Graphics, x: Int, y: Int, w: Int, h: Int, fillStart: Int, amountFull: Int) {
        if (g !is Graphics2D) {
            return
        }
        val g2 = g
        val progressString = progressBar.string
        g2.font = progressBar.font
        var renderLocation = getStringPlacement(
            g2,
            progressString,
            x,
            y,
            w,
            h,
        )
        val oldClip = g2.clipBounds
        if (progressBar.orientation === SwingConstants.HORIZONTAL) {
            g2.color = selectionBackground
            g2.color = selectionForeground
            g2.clipRect(fillStart, y, amountFull, h)
        } else { // VERTICAL
            g2.color = selectionBackground
            val rotate = AffineTransform.getRotateInstance(Math.PI / 2)
            g2.font = progressBar.font.deriveFont(rotate)
            renderLocation = getStringPlacement(
                g2,
                progressString,
                x,
                y,
                w,
                h,
            )
            g2.color = selectionForeground
            g2.clipRect(x, fillStart, w, amountFull)
        }
        g2.clip = oldClip
    }

    override fun getBoxLength(availableLength: Int, otherDimension: Int): Int = availableLength

    private fun getPeriodLength(): Int = JBUIScale.scale(16)

    private fun isEven(value: Int): Boolean = value % 2 == 0

    companion object {

        private const val ONE_OVER_THREE = 1f / 3

        private const val FLAG_RED_LIGHT = 0xFF0000
        private const val FLAG_WHITE_LIGHT = 0xFFFFFF
        private const val FLAG_GREEN_LIGHT = 0x00DD00

        private const val FLAG_RED_DARK = 0xA80000
        private const val FLAG_WHITE_DARK = 0xDDDDDD
        private const val FLAG_GREEN_DARK = 0x008800
    }
}
