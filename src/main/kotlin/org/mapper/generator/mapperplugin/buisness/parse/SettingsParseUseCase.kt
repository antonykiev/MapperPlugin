package org.mapper.generator.mapperplugin.buisness.parse

import com.intellij.execution.processTools.mapFlat
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope
import org.mapper.generator.mapperplugin.buisness.states.ClassMetadata
import org.mapper.generator.mapperplugin.buisness.states.MappingSettings
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class SettingsParseUseCase(
    private val settingsFilePath: String,
    private val project: Project,
) {
    private val parseStringToRuleModelUseCase = ParseStringToRuleModelUseCase()
    private val mapRulesModelToMappingSettings = MapRulesModelToMappingSettings()
    private val textFileUseCase = TextFileUseCase()

    fun settings(): Result<MappingSettings> {
        return validateSettingsFile(settingsFilePath)
            .mapFlat { textFileUseCase.text(settingsFilePath) }
            .mapFlat(parseStringToRuleModelUseCase::invoke)
            .mapFlat { mapRulesModelToMappingSettings(it, project) }
    }

    private fun validateSettingsFile(settingsFilePath: String): Result<Unit> {
        return runCatching {
            if (settingsFilePath.isEmpty()) throw Exception("File path is empty")
            val settingFile = File(settingsFilePath)
            if (!settingFile.exists() || !settingFile.isFile) throw Exception("File not found")
        }
    }
}