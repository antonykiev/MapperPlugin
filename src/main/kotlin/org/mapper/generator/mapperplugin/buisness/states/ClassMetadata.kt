package org.mapper.generator.mapperplugin.buisness.states

data class ClassMetadata(
    val className: String,
    val properties: List<Pair<String, String>>
)