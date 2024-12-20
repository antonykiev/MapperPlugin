package org.mapper.generator.mapperplugin.buisness.generation

class CreateStatementUseCase {

    operator fun invoke(
        sourceProperties: List<String>,
        targetProperties: List<String>
    ): String {
        return  "\n" + sourceProperties
            .map { sourceProperties ->
                val targetProperty = targetProperties.find { it == sourceProperties }.orEmpty()
                return@map createStatementLine(
                    sourceProperty = sourceProperties,
                    targetProperty = targetProperty
                )
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