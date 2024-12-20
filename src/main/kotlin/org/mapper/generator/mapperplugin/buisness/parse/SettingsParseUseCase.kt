package org.mapper.generator.mapperplugin.buisness.parse

import com.google.gson.Gson
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope
import org.mapper.generator.mapperplugin.buisness.states.ClassMetadata
import org.mapper.generator.mapperplugin.buisness.states.MappingRule
import org.mapper.generator.mapperplugin.buisness.states.MappingSettings
import org.mapper.generator.mapperplugin.data.states.MappingRuleJsonModel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class SettingsParseUseCase(
    private val settingsFilePath: String,
    private val project: Project,
) {
    private val classNameToClassMetadataUseCase = ClassNameToClassMetadataUseCase(project)

    fun settings(): Result<MappingSettings> {
        val fileText: Result<String> = TextFileUseCase(settingsFilePath).text()
        return fileText.map {
            Gson().fromJson(it, MappingRuleJsonModel::class.java)
        }
            .map {
                MappingSettings (
                    projectBasePath = project.basePath.orEmpty(),
                    outputDir = it.outputDir,
                            mappingRules = it.mappingRules.map { stringStringEntry ->
                                MappingRule.Simple (
                                    sourceClassMetaData = classNameToClassMetadataUseCase.classMetadata(stringStringEntry.key).getOrThrow(),
                                    targetMetaData = classNameToClassMetadataUseCase.classMetadata(stringStringEntry.value).getOrThrow(),
                                )
                            }
                )
            }
    }

    class ClassNameToClassMetadataUseCase(
        private val project: Project,
    ) {
        private val psiFacade = JavaPsiFacade.getInstance(project)

        fun classMetadata(className: String): Result<ClassMetadata> {
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