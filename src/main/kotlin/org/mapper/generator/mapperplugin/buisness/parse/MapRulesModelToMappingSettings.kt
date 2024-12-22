package org.mapper.generator.mapperplugin.buisness.parse

import com.intellij.openapi.project.Project
import org.mapper.generator.mapperplugin.buisness.parse.SettingsParseUseCase.ClassNameToClassMetadataUseCase
import org.mapper.generator.mapperplugin.buisness.states.Detail
import org.mapper.generator.mapperplugin.buisness.states.MappingRule
import org.mapper.generator.mapperplugin.buisness.states.MappingSettings
import org.mapper.generator.mapperplugin.data.states.DetailJson
import org.mapper.generator.mapperplugin.data.states.SimpleJsonRulesModel

class MapRulesModelToMappingSettings {

    private val classNameToClassMetadataUseCase = ClassNameToClassMetadataUseCase()
    private val getPropertyFromDetailsUseCase = GetPropertyFromDetailsUseCase()

    operator fun invoke(
        simpleJsonRulesModel: SimpleJsonRulesModel,
        project: Project,
    ): MappingSettings {
        return MappingSettings(
            mapperName = simpleJsonRulesModel.mapperName.orEmpty(),
            projectBasePath = project.basePath.orEmpty(),
            outputDir = simpleJsonRulesModel.outputDir,
            mappingRules = simpleJsonRulesModel.mappingRules.map { stringStringEntry: Map.Entry<String, DetailJson> ->

                val propertyMap = stringStringEntry.value.details.orEmpty()
                    .let { getPropertyFromDetailsUseCase(it) }

                MappingRule(
                    sourceClassMetaData = classNameToClassMetadataUseCase.classMetadata(
                        project = project,
                        className = stringStringEntry.key
                    ).getOrThrow(),
                    detail = Detail(
                        targetMetaData = classNameToClassMetadataUseCase.classMetadata(
                            project = project,
                            className = stringStringEntry.value.target
                        ).getOrThrow(),
                        propertyMap = propertyMap,
                    )
                )
            }
        )
    }
}