package org.luovuutesi.core

import org.luovuutesi.modules.ModuleManager
import org.luovuutesi.api.Module

fun main() {
    val moduleManager = ModuleManager("modules")

    moduleManager.loadModules()

    val modules = moduleManager.allModules

    modules.forEach(Module::start)

    modules.forEach(Module::stop)
}