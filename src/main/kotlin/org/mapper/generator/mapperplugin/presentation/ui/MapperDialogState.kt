package org.mapper.generator.mapperplugin.presentation.ui

data class MapperDialogState(
    val generateButtonEnabled: Boolean,
    val generateStrategy: GenerateStrategy
)

enum class GenerateStrategy {
    FILE,
    FOLDER
}
