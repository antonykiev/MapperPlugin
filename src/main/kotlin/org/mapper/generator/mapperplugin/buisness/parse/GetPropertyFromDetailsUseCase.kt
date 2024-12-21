package org.mapper.generator.mapperplugin.buisness.parse

class GetPropertyFromDetailsUseCase {

    operator fun invoke(details: List<String>): Map<String, String> {
        return details.map {
            val propertyName = getProperty(it)
            return@map propertyName to it
        }.toMap()
    }

    private fun getProperty(detail: String) : String {
        return detail.split("=").first().trim()
    }
}