package org.mapper.generator.mapperplugin.buisness.generation

import com.squareup.kotlinpoet.FileSpec
import org.mapper.generator.mapperplugin.buisness.states.MappingSettings

class SuccessMessageUseCase {
    operator fun invoke(
        fileSpec: FileSpec,
    ): String {
        return buildString {
            append("SUCCESS \n")
            append("generated class in path: ${fileSpec.relativePath}")
        }
    }
}