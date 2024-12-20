package org.mapper.generator.mapperplugin.buisness.parse

import com.intellij.execution.processTools.mapFlat
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope
import org.mapper.generator.mapperplugin.buisness.states.ClassMetadata
import org.mapper.generator.mapperplugin.buisness.states.MappingSettings
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class SettingsParseUseCase(
    private val settingsFilePath: String,
    private val project: Project,
) {
    private val parseStringToRuleModelUseCase = ParseStringToRuleModelUseCase()
    private val mapRulesModelToMappingSettings = MapRulesModelToMappingSettings()

    fun settings(): Result<MappingSettings> {
        return TextFileUseCase(settingsFilePath).text()
            .mapFlat(parseStringToRuleModelUseCase::invoke)
            .map { mapRulesModelToMappingSettings(it, project) }
    }

    class ClassNameToClassMetadataUseCase {

        fun classMetadata(
            project: Project,
            className: String
        ): Result<ClassMetadata> {
            val psiFacade = JavaPsiFacade.getInstance(project)
            val psiClass = psiFacade.findClass(className, GlobalSearchScope.allScope(project))
                ?: return Result.failure(Exception("Can not init PSI CLASS ${project.name} Project name - $project, className - $className"))
            return Result.success(
                value = ClassMetadata(
                    className = className,
                    properties = psiClass.fields.map { it.name to it.type.canonicalText }
                )
            )
        }
    }

    class TextFileUseCase(
        private val filePath: String,
    ) {
        fun text(): Result<String> {
            return runCatching {
                val path: Path = Paths.get(filePath)
                val lines: List<String> = Files.readAllLines(path)
                return@runCatching lines.joinToString("\n")
            }
        }
    }
}