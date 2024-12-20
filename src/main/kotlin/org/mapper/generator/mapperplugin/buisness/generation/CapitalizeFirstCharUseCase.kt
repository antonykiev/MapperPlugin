package org.mapper.generator.mapperplugin.buisness.generation

class CapitalizeFirstCharUseCase {

    operator fun invoke(input: String): String {
        return input.replaceFirstChar { it.uppercase() }
    }
}