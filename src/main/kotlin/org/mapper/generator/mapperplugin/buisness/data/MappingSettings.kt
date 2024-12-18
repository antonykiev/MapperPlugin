package org.mapper.generator.mapperplugin.buisness.data

data class MappingSettings(
    val outputDir: String,
    val mappingRules: List<MappingRule>
)
