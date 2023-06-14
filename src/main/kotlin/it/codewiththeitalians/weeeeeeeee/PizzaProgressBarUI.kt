package it.codewiththeitalians.weeeeeeeee

import com.intellij.openapi.util.ScalableIcon
import com.intellij.ui.Gray
import com.intellij.ui.JBColor
import com.intellij.ui.scale.JBUIScale
import com.intellij.util.ui.GraphicsUtil
import com.intellij.util.ui.UIUtil
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.LinearGradientPaint
import java.awt.geom.AffineTransform
import java.awt.geom.Area
import java.awt.geom.Rectangle2D
import java.awt.geom.RoundRectangle2D
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.SwingConstants
import javax.swing.plaf.basic.BasicProgressBarUI

internal class PizzaProgressBarUI : BasicProgressBarUI() {

    override fun getPreferredSize(c: JComponent?): Dimension =
        Dimension(super.getPreferredSize(c).width, JBUIScale.scale(20))

    @Volatile
    private var offset = 0

    @Volatile
    private var offset2 = 0

    @Volatile
    private var velocity = 1
    override fun paintIndeterminate(g2d: Graphics?, c: JComponent) {
        if (g2d !is Graphics2D) {
            return
        }
        val b = progressBar.insets // area for border
        val barRectWidth = progressBar.width - (b.right + b.left)
        val barRectHeight = progressBar.height - (b.top + b.bottom)
        if (barRectWidth <= 0 || barRectHeight <= 0) {
            return
        }
        g2d.color = JBColor(Gray._240.withAlpha(50), Gray._128.withAlpha(50))
        val w = c.width
        var h = c.preferredSize.height
        if (!isEven(c.height - h)) h++

        val basePizzaPaint = LinearGradientPaint(
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
        g2d.paint = basePizzaPaint
        if (c.isOpaque) {
            g2d.fillRect(0, (c.height - h) / 2, w, h)
        }
        g2d.color = JBColor(Gray._165.withAlpha(50), Gray._88.withAlpha(50))
        val config = GraphicsUtil.setupAAPainting(g2d)
        g2d.translate(0, (c.height - h) / 2)
        val x = -offset

        val old = g2d.paint
        g2d.paint = basePizzaPaint
        val R = JBUIScale.scale(8f)
        val R2 = JBUIScale.scale(9f)
        val containingRoundRect = Area(RoundRectangle2D.Float(1f, 1f, w - 2f, h - 2f, R, R))
        g2d.fill(containingRoundRect)
        g2d.paint = old
        offset = (offset + 1) % getPeriodLength()
        offset2 += velocity
        if (offset2 <= 2) {
            offset2 = 2
            velocity = 1
        } else if (offset2 >= w - JBUIScale.scale(15)) {
            offset2 = w - JBUIScale.scale(15)
            velocity = -1
        }
        val area = Area(Rectangle2D.Float(0f, 0f, w.toFloat(), h.toFloat()))
        area.subtract(Area(RoundRectangle2D.Float(1f, 1f, w - 2f, h - 2f, R, R)))
        g2d.paint = Gray._128
        if (c.isOpaque) {
            g2d.fill(area)
        }
        area.subtract(Area(RoundRectangle2D.Float(0f, 0f, w.toFloat(), h.toFloat(), R2, R2)))
        val parent = c.parent
        val background = if (parent != null) parent.background else UIUtil.getPanelBackground()
        g2d.paint = background
        if (c.isOpaque) {
            g2d.fill(area)
        }

        val scaledIcon: Icon = if (velocity > 0) NyanIcons.Pizza as ScalableIcon else (NyanIcons.PizzaFlip as ScalableIcon)
        scaledIcon.paintIcon(progressBar, g2d, offset2 - JBUIScale.scale(10), -JBUIScale.scale(6))
        g2d.draw(RoundRectangle2D.Float(1f, 1f, w - 2f - 1f, h - 2f - 1f, R, R))
        g2d.translate(0, -(c.height - h) / 2)

        if (progressBar.isStringPainted) {
            if (progressBar.orientation === SwingConstants.HORIZONTAL) {
                paintString(g2d, b.left, b.top, barRectWidth, barRectHeight, boxRect.x, boxRect.width)
            } else {
                paintString(g2d, b.left, b.top, barRectWidth, barRectHeight, boxRect.y, boxRect.height)
            }
        }
        config.restore()
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
        NyanIcons.Pizza.paintIcon(progressBar, g, amountFull - JBUIScale.scale(10), -JBUIScale.scale(6))
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
    }
}
