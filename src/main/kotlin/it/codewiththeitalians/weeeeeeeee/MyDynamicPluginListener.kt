package it.codewiththeitalians.weeeeeeeee

import com.intellij.ide.plugins.DynamicPluginListener
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.openapi.components.service
import com.intellij.openapi.extensions.PluginId

class MyDynamicPluginListener : DynamicPluginListener {

    override fun pluginLoaded(pluginDescriptor: IdeaPluginDescriptor) {
        if (pluginDescriptor.pluginId == PluginId.getId("it.codewiththeitalians.weeeeeeeee")) {
            service<PizzaService>()
        }
    }
}
