package org.mapper.generator.mapperplugin.buisness.states

data class MappingSettings(
    val mapperName: String,
    val projectBasePath: String,
    val outputDir: String,
    val mappingRules: List<MappingRule>,
)
