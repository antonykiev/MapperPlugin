package org.mapper.generator.mapperplugin.buisness.states

data class Detail(
    val targetMetaData: ClassMetadata,// class info
    val propertyMap: Map<String, String> //property name to value
)
