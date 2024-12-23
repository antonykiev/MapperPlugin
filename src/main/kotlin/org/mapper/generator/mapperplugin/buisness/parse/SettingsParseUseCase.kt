package org.mapper.generator.mapperplugin.buisness.parse

import com.intellij.execution.processTools.mapFlat
import com.intellij.openapi.project.Project
import org.mapper.generator.mapperplugin.buisness.states.MappingSettings
import java.io.File

class SettingsParseUseCase(
    private val settingsFilePath: String,
    private val project: Project,
) {
    private val parseStringToRuleModelUseCase = ParseStringToRuleModelUseCase()
    private val mapRulesModelToMappingSettings = MapRulesModelToMappingSettings()
    private val textFileUseCase = TextFileUseCase()

    fun settings(): Result<MappingSettings> {
        return validateSettingsFile(settingsFilePath)
            .mapFlat { textFileUseCase.invoke(settingsFilePath) }
            .mapFlat { parseStringToRuleModelUseCase.invoke(it) }
            .mapFlat {
                mapRulesModelToMappingSettings.invoke(
                    simpleJsonRulesModel = it,
                    project = project
                )
            }
    }

    private fun validateSettingsFile(settingsFilePath: String): Result<Unit> {
        return runCatching {
            if (settingsFilePath.isEmpty()) throw Exception("File path is empty")
            val settingFile = File(settingsFilePath)
            if (!settingFile.exists() || !settingFile.isFile) throw Exception("File not found")
        }
    }
}