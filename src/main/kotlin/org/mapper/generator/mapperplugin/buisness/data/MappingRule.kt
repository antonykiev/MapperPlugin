package org.mapper.generator.mapperplugin.buisness.data

sealed interface MappingRule {

    data class Simple(
        val classSubject: String,
        val classObject: String,
    ): MappingRule

}