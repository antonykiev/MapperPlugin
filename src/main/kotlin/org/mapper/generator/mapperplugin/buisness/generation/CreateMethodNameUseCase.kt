package org.mapper.generator.mapperplugin.buisness.generation

class CreateMethodNameUseCase {

    operator fun invoke(
        subjectClassName: String,
        objectClassName: String
    ): String {
        return "map${subjectClassName}To${objectClassName}"
    }
}