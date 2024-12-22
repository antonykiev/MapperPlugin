package org.mapper.generator.mapperplugin.data.states

data class SimpleJsonRulesModel(
    val outputDir: String,
    val mapperName: String?,
    val mappingRules: Map<String, DetailJson>,
)
