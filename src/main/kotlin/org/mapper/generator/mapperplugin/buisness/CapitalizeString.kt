package org.mapper.generator.mapperplugin.buisness

class CapitalizeString(
    private val string: String
) {

    operator fun invoke(): String {
        return string.replaceFirstChar { it.uppercase() }
    }

}