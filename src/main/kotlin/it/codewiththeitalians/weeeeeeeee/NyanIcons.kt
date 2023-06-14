package it.codewiththeitalians.weeeeeeeee

import com.intellij.openapi.util.IconLoader

interface NyanIcons {
    companion object {

        @JvmField
        val Pizza = IconLoader.getIcon("pizza-32.png", NyanIcons::class.java.classLoader)

        @JvmField
        val PizzaFlip = IconLoader.getIcon("pizza-flip-32.png", NyanIcons::class.java.classLoader)
    }
}
