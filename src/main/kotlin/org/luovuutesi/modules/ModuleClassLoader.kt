package org.luovuutesi.modules

import java.io.File
import java.net.URI
import java.net.URL
import java.net.URLClassLoader

class ModuleClassLoader(urls: Array<URL>, parent: ClassLoader) : URLClassLoader(urls, parent) {
    constructor(module: File, parent: ClassLoader) : this(module.let {
        if (it.isDirectory) {
            it.listFiles()?.map(File::toURI)?.map(URI::toURL)?.toTypedArray() ?: arrayOf()
        } else {
            arrayOf(it.toURI().toURL())
        }
    }, parent)

    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        var loaded = findLoadedClass(name)

        if (loaded == null) {
            val isShared = SHARED_PACKAGES.any(name::startsWith)

            loaded = if (isShared) {
                parent.loadClass(name)
            } else {
                super.loadClass(name, resolve)
            }
        }

        if (resolve) {
            resolveClass(loaded)
        }

        return loaded
    }

    companion object {
        private val SHARED_PACKAGES = listOf("org.luovuutesi.api")
    }
}