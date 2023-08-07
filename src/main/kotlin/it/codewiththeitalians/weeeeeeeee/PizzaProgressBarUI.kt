package it.codewiththeitalians.weeeeeeeee

import com.intellij.ui.JBColor
import com.intellij.ui.NewUI
import com.intellij.ui.scale.JBUIScale.scale
import com.intellij.util.Icons
import com.intellij.util.ui.GraphicsUtil
import com.intellij.util.ui.JBInsets
import java.awt.BasicStroke
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.geom.Path2D
import java.awt.geom.RoundRectangle2D
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.plaf.basic.BasicProgressBarUI
import kotlin.math.roundToInt
import kotlin.random.Random

@Suppress("MagicNumber", "TooManyFunctions")
internal class PizzaProgressBarUI : BasicProgressBarUI() {

    private val redPaint = JBColor(FLAG_RED_LIGHT, FLAG_RED_DARK)
    private val whitePaint = JBColor(FLAG_WHITE_LIGHT, FLAG_WHITE_DARK)
    private val greenPaint = JBColor(FLAG_GREEN_LIGHT, FLAG_GREEN_DARK)

    private val oldUiBorderColor = JBColor(0xCCCCCC, 0x555555)
    private val newUiBorderColor = JBColor(0xCCCCCC, 0x111111)

    private val borderColor
        get() = if (NewUI.isEnabled()) newUiBorderColor else oldUiBorderColor

    private var iconOffsetX = 0

    private var icon: Icon = PizzaIcons.PizzaGoingRight

    private var iconSize = icon.iconHeight
        get() = icon.iconHeight
        set(value) {
            if (field != value) {
                field = value
                updateIcon()
            }
        }

    private var isGoingRight: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                updateIcon()
            }
        }

    private var isIndeterminate: Boolean = false
        set(value) {
            if (field != value) resetState()
            field = value
        }

    private val resizeListener = object : ComponentAdapter() {
        override fun componentResized(e: ComponentEvent?) {
            updateIcon()
            progressBar.invalidate()
        }
    }

    private val borderInsets = JBInsets.create(1, 1)

    init {
        updateIcon()
    }

    private fun updateIcon() {
        val maxSize = progressBar?.let { it.height - it.insets.vertical - borderInsets.vertical }
            ?: Int.MAX_VALUE

        val sizePx = iconSize.coerceAtMost(maxSize)
        icon = when {
            !isIndeterminate -> PizzaIcons.PizzaGoingRight.resize(sizePx)
            isGoingRight -> PizzaIcons.PizzaGoingRight.resize(sizePx)
            else -> PizzaIcons.PizzaGoingLeft.resize(sizePx)
        }
    }

    override fun installListeners() {
        super.installListeners()
        progressBar.addComponentListener(resizeListener)
    }

    override fun uninstallListeners() {
        progressBar.removeComponentListener(resizeListener)
        super.uninstallListeners()
    }

    override fun getPreferredSize(c: JComponent): Dimension {
        val verticalInsets = c.insets.vertical
        val borderThickness = borderInsets.top
        return Dimension(
            super.getPreferredSize(c).width,
            getPreferredStripeHeight() + verticalInsets + borderThickness
        )
    }

    private fun getPreferredStripeHeight(): Int {
        val ho = progressBar.getClientProperty("ProgressBar.stripeWidth")
        return ho?.toString()?.toIntOrNull()?.let { scale(it.coerceAtLeast(12)) }
            ?: defaultIconSize
    }

    override fun paintIndeterminate(g2d: Graphics, c: JComponent) {
        isIndeterminate = true
        if (g2d !is Graphics2D) {
            return
        }

        GraphicsUtil.setupAAPainting(g2d)

        val insets = progressBar.insets
        val availableWidth = (progressBar.width - insets.horizontal).toFloat()
        val availableHeight = (progressBar.height - insets.vertical).toFloat()

        val borderThickness = borderInsets.top.toFloat()
        val barWidth = availableWidth - 2 * borderThickness
        val barHeight = availableHeight - 2 * borderThickness

        if (availableWidth <= 0 || availableHeight <= 0) {
            return
        }

        val x = insets.top.toFloat()
        val y = insets.left.toFloat()

        doAnimationTick(barWidth.toInt())

        g2d.drawItalianFlag(
            x = x + borderThickness,
            y = y + borderThickness,
            barWidth = barWidth,
            barHeight = barHeight
        )

        g2d.drawBorder(
            x = x,
            y = y,
            boundsWidth = availableWidth - borderThickness,
            boundsHeight = availableHeight - borderThickness,
            borderThickness = borderThickness,
        )

        g2d.drawPizzaIcon(
            barY = y + borderThickness,
            barHeight = barHeight,
            xOffset = x + iconOffsetX.toFloat(),
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
        val availableWidth = (progressBar.width - insets.horizontal).toFloat()
        val availableHeight = (progressBar.height - insets.vertical).toFloat()

        val progress = progressBar.value.toFloat() / progressBar.maximum
        val borderThickness = borderInsets.top.toFloat()
        val barWidth = ((availableWidth - 2 * borderThickness) * progress + iconSize * progress)
        val barHeight = availableHeight - 2 * borderThickness

        if (availableWidth <= 0 || availableHeight <= 0) {
            return
        }

        val x = insets.top.toFloat()
        val y = insets.left.toFloat()

        doAnimationTick(barWidth.toInt())

        g2d.drawItalianFlag(
            x = x + borderThickness,
            y = y + borderThickness,
            barWidth = (barWidth - iconSize / 2f).coerceIn(0f..(availableWidth - 2 * borderThickness)),
            barHeight = barHeight
        )

        g2d.drawBorder(
            x = x + borderThickness / 2f,
            y = y + borderThickness / 2f,
            boundsWidth = availableWidth - borderThickness,
            boundsHeight = availableHeight - borderThickness,
            borderThickness = borderThickness,
        )

        if (availableHeight >= iconSize) {
            g2d.drawPizzaIcon(
                barY = y + borderThickness,
                barHeight = barHeight,
                xOffset = x + barWidth - iconSize / 2f,
                isJittery = false
            )
        }
    }

    private fun doAnimationTick(barWidth: Int) {
        val tick = scale(1)
        if (isGoingRight) iconOffsetX += tick else iconOffsetX -= tick

        when {
            !isIndeterminate -> isGoingRight = true
            iconOffsetX > barWidth + iconSize / 2 -> isGoingRight = false
            iconOffsetX < -iconSize -> isGoingRight = true
        }
    }

    private fun Graphics2D.drawItalianFlag(
        x: Float,
        y: Float,
        barWidth: Float,
        barHeight: Float
    ) {
        if (barWidth < 1f) return

        val barArc = scale(ARC_SIZE) / 2f
        val stripeHeight = barHeight / 3f

        val redStripePath = Path2D.Float().apply {
            moveTo(x, y + barArc)
            quadTo(x, y, x + barArc, y)
            lineTo(x + barWidth - barArc, y)
            quadTo(x + barWidth, y, x + barWidth, y + barArc)
            lineTo(x + barWidth, y + stripeHeight)
            lineTo(x, y + stripeHeight)
            closePath()
        }
        paint = redPaint
        fill(redStripePath)

        // Can't use paintRect for this as we need more precision
        paint = whitePaint
        val whiteStripePath = Path2D.Float().apply {
            moveTo(x, y + stripeHeight)
            lineTo(x + barWidth, y + stripeHeight)
            lineTo(x + barWidth, y + stripeHeight * 2)
            lineTo(x, y + stripeHeight * 2)
            closePath()
        }
        fill(whiteStripePath)

        val greenStripePath = Path2D.Float().apply {
            moveTo(x, y + stripeHeight * 2)
            lineTo(x + barWidth, y + stripeHeight * 2)
            lineTo(x + barWidth, y + barHeight - barArc)
            quadTo(x + barWidth, y + barHeight, x + barWidth - barArc, y + barHeight)
            lineTo(x + barArc, y + barHeight)
            quadTo(x, y + barHeight, x, y + barHeight - barArc)
            closePath()
        }
        paint = greenPaint
        fill(greenStripePath)
    }

    private fun Graphics2D.drawBorder(
        x: Float,
        y: Float,
        boundsWidth: Float,
        boundsHeight: Float,
        borderThickness: Float
    ) {
        val borderArc = scale(ARC_SIZE)
        val borderShape = RoundRectangle2D.Float(
            /* x = */ x,
            /* y = */ y,
            /* w = */ boundsWidth,
            /* h = */ boundsHeight,
            /* arcw = */ borderArc,
            /* arch = */ borderArc
        )
        stroke = BasicStroke(borderThickness)
        color = borderColor
        draw(borderShape)
    }


    private fun Graphics2D.drawPizzaIcon(barY: Float, barHeight: Float, xOffset: Float, isJittery: Boolean) {
        val yOffset = barY + barHeight / 2f - icon.iconHeight / 2f
        val maxJitter = if (barHeight > 1f) barHeight / 5f else Float.MAX_VALUE
        val jitterSize = (iconSize / 8f).coerceIn(1f..maxJitter)
        val jitterScale = 2f * jitterSize
        val jitterX = if (isJittery) Random.nextFloat() * jitterScale - jitterSize else 0f
        val jitterY = if (isJittery) Random.nextFloat() * jitterScale - jitterSize else 0f

        icon.paintIcon(
            progressBar,
            this,
            (xOffset - (iconSize / 2f) + jitterX).roundToInt(),
            (yOffset + jitterY).roundToInt()
        )
    }

    override fun getBoxLength(availableLength: Int, otherDimension: Int): Int = availableLength

    private fun resetState() {
        iconOffsetX = 0
        isGoingRight = true
    }

    companion object {

        private const val FLAG_RED_LIGHT = 0xFF0000
        private const val FLAG_WHITE_LIGHT = 0xFFFFFF
        private const val FLAG_GREEN_LIGHT = 0x00DD00

        private const val FLAG_RED_DARK = 0xA80000
        private const val FLAG_WHITE_DARK = 0xDDDDDD
        private const val FLAG_GREEN_DARK = 0x008800

        private val defaultIconSize
            get() = scale(20)

        private const val ARC_SIZE = 8f
    }
}
