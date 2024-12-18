package org.mapper.generator.mapperplugin.logic

import com.intellij.openapi.components.Service

@Service
class ClassLoaderService {
    var projectClassLoader: ClassLoader? = null
}
