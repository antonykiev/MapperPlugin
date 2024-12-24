package org.mapper.generator.mapperplugin.buisness.generation

class GetPackageFromFullNameUseClass {

    operator fun invoke(fullClassName: String, default: String): String {
        fullClassName.ifEmpty {
            return ""
        }
        if (!fullClassName.contains("."))
            return default
        return runCatching {
            fullClassName.substring(
                startIndex = 0,
                endIndex = fullClassName.lastIndexOf(".")
            )
        }.getOrDefault(default)

    }
}