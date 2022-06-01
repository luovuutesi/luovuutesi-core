package org.luovuutesi.modules

import org.luovuutesi.api.Module
import java.io.File
import java.util.ServiceLoader

class ModuleManager(private val modulesDir: File) {
    init {
        if (!modulesDir.exists()) {
            modulesDir.mkdirs()
        }

        if (!modulesDir.isDirectory) {
            throw InvalidModulesPathException("Modules path does not name a directory!")
        }
    }

    constructor(modulesPath: String) : this(File(modulesPath))

    private val modules = mutableMapOf<String, Module>()

    fun loadModules() {
        val files = modulesDir.listFiles() ?: arrayOf()
        for (module in files) {
            loadModule(module)
        }
    }

    private fun loadModule(location: File) {
        val moduleClassLoader = ModuleClassLoader(location, javaClass.classLoader)
        val currentClassLoader = Thread.currentThread().contextClassLoader

        try {
            Thread.currentThread().contextClassLoader = moduleClassLoader
            for (module in ServiceLoader.load(
                Module::class.java,
                moduleClassLoader
            )) {
                installModule(module)
            }
        } finally {
            Thread.currentThread().contextClassLoader = currentClassLoader
        }
    }

    private fun installModule(module: Module) {
        if (modules.containsKey(module.moduleId)) {
            throw DuplicateModuleIdException("Duplicate Module ID: '${module.moduleId}'!")
        }

        modules[module.moduleId] = module
    }

    val allModules: List<Module> get() = modules.values.toList()
}