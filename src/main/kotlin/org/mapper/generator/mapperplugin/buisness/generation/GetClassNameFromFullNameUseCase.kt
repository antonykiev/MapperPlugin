package org.mapper.generator.mapperplugin.buisness.generation

class GetClassNameFromFullNameUseCase {

    operator fun invoke(fullClassName: String): String {
        return fullClassName
            .substring(startIndex = fullClassName.lastIndexOf(".") + 1)
    }
}