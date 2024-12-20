package org.mapper.generator.mapperplugin.buisness.states

sealed interface MappingRule {

    data class Simple(
        val sourceClassMetaData: ClassMetadata,
        val targetMetaData: ClassMetadata,
    ): MappingRule

}