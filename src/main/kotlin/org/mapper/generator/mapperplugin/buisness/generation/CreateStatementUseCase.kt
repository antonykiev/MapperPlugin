package org.mapper.generator.mapperplugin.buisness.generation

import org.mapper.generator.mapperplugin.buisness.states.Detail

class CreateStatementUseCase {

    operator fun invoke(
        sourceProperties: List<String>,
        detail: Detail
    ): String {
        return  "\n" + sourceProperties
            .map { sourceProperties ->

                val targetProperty = detail.targetMetaData.properties
                    .map { it.first }
                    .find { it == sourceProperties }
                    .orEmpty()

                return@map if (detail.propertyMap.containsKey(sourceProperties)) {
                     detail.propertyMap[targetProperty] + ",\n"
                } else {
                    createStatementLine(
                        sourceProperty = sourceProperties,
                        targetProperty = targetProperty
                    )
                }
            }.joinToString(separator = "")
    }

    private fun createStatementLine(
        sourceProperty: String,
        targetProperty: String,
    ): String {
        if (sourceProperty.isEmpty()) return "$targetProperty = ,\n"
        return "$targetProperty = source.$sourceProperty,\n"
    }
}