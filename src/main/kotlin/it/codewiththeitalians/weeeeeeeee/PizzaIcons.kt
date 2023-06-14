package it.codewiththeitalians.weeeeeeeee

import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.ScalableIcon

interface PizzaIcons {
    companion object {

        @JvmField
        val PizzaLeft = IconLoader.getIcon("pizza-32.png", PizzaIcons::class.java.classLoader) as ScalableIcon

        @JvmField
        val PizzaRight = IconLoader.getIcon("pizza-flip-32.png", PizzaIcons::class.java.classLoader) as ScalableIcon
    }
}
