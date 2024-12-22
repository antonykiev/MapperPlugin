package org.mapper.generator.mapperplugin.buisness.generation

import com.squareup.kotlinpoet.FileSpec
import org.mapper.generator.mapperplugin.buisness.states.MappingSettings

class SuccessMessageUseCase {
    operator fun invoke(
        fileSpec: FileSpec,
        mappingSettings: MappingSettings
    ): String {
        return buildString {
            append("SUCCESS \n")
            append("generated mapper "+ fileSpec.packageName + "." + fileSpec.name + "\n")
            append("in path: ${mappingSettings.projectBasePath + mappingSettings.outputDir}")
            append("${fileSpec.packageName.replace(".", "/")}/${fileSpec.name}.kt")
        }
    }
}