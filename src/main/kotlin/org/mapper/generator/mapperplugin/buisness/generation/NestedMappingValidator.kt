package org.mapper.generator.mapperplugin.buisness.generation

import org.mapper.generator.mapperplugin.buisness.states.MappingRule

class NestedMappingValidator {

    operator fun invoke(
        mappingRules: List<MappingRule>
    ) {
        val explicitMapping: List<TypeComparison> = mappingRules
            .map {
                TypeComparison(
                    source = it.sourceClassMetaData.className,
                    target = it.detail.targetMetaData.className
                )
            }
        val nestedMapping/*: List<TypeComparison>*/ = mappingRules
            .map {
                val sourceClassProperties = it.sourceClassMetaData.properties.toMap()

                val targetClassProperties = it.detail.targetMetaData.properties.toMap()
                val detailedProperties = it.detail.propertyMap
                val validTargetClassProperties = targetClassProperties.filterNot { (key, _) ->
                    detailedProperties.containsKey(key)
                }
                checkFieldCorrespondence(
                    sourceClassName = it.sourceClassMetaData.className,
                    targetClassName = it.detail.targetMetaData.className,
                    sourceClassProperties = sourceClassProperties,
                    targetClassProperties = validTargetClassProperties
                )
            }
    }

    private fun checkFieldCorrespondence(
        sourceClassName: String,
        targetClassName: String,
        sourceClassProperties: Map<String, String>,
        targetClassProperties: Map<String, String>
    ) {
        targetClassProperties.forEach { (key, value) ->
            if (sourceClassProperties.containsKey(key).not()) {
                throw Exception("Set correspondence between $targetClassName.$key and $sourceClassName in your mapping rules")
            }
            if (sourceClassProperties[key] != value) {
                throw Exception("Type mismatch $targetClassName.$key type is - $value and $sourceClassName type is - ${sourceClassProperties[key]}")
            }
        }
    }

    private data class TypeComparison(
        val source: String,
        val target: String
    )
}