package org.mapper.generator.mapperplugin.logic

data class ClassMetadata(
    val className: String,
    val projectBasePath: String,
    val properties: List<Pair<String, String>>
)