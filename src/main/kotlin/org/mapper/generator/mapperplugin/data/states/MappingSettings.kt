package org.mapper.generator.mapperplugin.data.states

data class MappingSettings(
    val sourceClassName: String,
    val targetClassName: String,
    val selectedFileName: String,
    val isExtensionFunc: Boolean,
)
