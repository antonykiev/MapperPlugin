package org.mapper.generator.mapperplugin.buisness.generation

class GetPackageUseClass {

    operator fun invoke(fullClassName: String): String {
        return fullClassName.substring(0, fullClassName.lastIndexOf("."))
    }
}