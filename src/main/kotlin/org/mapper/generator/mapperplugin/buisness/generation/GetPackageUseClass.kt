package org.mapper.generator.mapperplugin.buisness.generation

class GetPackageUseClass {

    operator fun invoke(fullClassName: String): String {
        return fullClassName.substring(
            startIndex = 0,
            endIndex = fullClassName.lastIndexOf(".")
        )
    }
}