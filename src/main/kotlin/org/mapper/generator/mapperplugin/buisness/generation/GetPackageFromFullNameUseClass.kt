package org.mapper.generator.mapperplugin.buisness.generation

class GetPackageFromFullNameUseClass {

    operator fun invoke(fullClassName: String): String {
        fullClassName.ifEmpty {
            return ""
        }
        if (!fullClassName.contains("."))
            return fullClassName
        return runCatching {
            fullClassName.substring(
                startIndex = 0,
                endIndex = fullClassName.lastIndexOf(".")
            )
        }.getOrDefault("")

    }
}