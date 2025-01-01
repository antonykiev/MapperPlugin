package org.mapper.generator.mapperplugin.presentation.ui

data class MapperDialogState(
    val generateButtonEnabled: Boolean,
    val generateStrategy: GenerateStrategy,
    val targetFileField: String,
    val generateInLabel: String,
)

enum class GenerateStrategy {
    FILE,
    FOLDER
}
