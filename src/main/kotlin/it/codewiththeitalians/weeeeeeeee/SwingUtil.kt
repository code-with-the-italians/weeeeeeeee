package it.codewiththeitalians.weeeeeeeee

import com.intellij.openapi.util.ScalableIcon
import java.awt.Insets

internal val Insets.horizontal
    get() = left + right

internal val Insets.vertical
    get() = top + bottom

internal fun ScalableIcon.resize(sizePx: Int) =
    if (sizePx != iconWidth) {
        scale(sizePx / iconWidth.toFloat())
    } else {
        this
    }
