package it.codewiththeitalians.weeeeeeeee

import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.ScalableIcon

object PizzaIcons {

    @JvmField
    val PizzaGoingLeft = IconLoader.getIcon("pizza-32.png", PizzaIcons::class.java.classLoader) as ScalableIcon

    @JvmField
    val PizzaGoingRight = IconLoader.getIcon("pizza-flip-32.png", PizzaIcons::class.java.classLoader) as ScalableIcon
}
