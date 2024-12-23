package org.mapper.generator.mapperplugin.buisness.generation

class GetClassNameFromFullNameUseCase {

    operator fun invoke(fullClassName: String): String {
        fullClassName.ifEmpty {
            return ""
        }
        return runCatching {
            fullClassName.substring(startIndex = fullClassName.lastIndexOf(".") + 1)
        }.getOrDefault("")
    }
}