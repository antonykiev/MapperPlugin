package org.mapper.generator.mapperplugin.buisness.generation

class GetClassNameFromFullNameUseCase {

    operator fun invoke(fullClassName: String): String {
        return fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
    }
}