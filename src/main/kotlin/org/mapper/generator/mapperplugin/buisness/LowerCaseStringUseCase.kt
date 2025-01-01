package org.mapper.generator.mapperplugin.buisness

class LowerCaseStringUseCase(
    private val string: String
) {

    operator fun invoke(): String {
        if (string.isEmpty())
            return string
        return string[0].lowercaseChar() + string.substring(1)
    }
}