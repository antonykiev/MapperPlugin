package org.mapper.generator.mapperplugin.presentation

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope
import org.mapper.generator.mapperplugin.buisness.parse.SettingsParseUseCase
import org.mapper.generator.mapperplugin.buisness.states.ClassMetadata
import org.mapper.generator.mapperplugin.data.GeneratorEngine
import org.mapper.generator.mapperplugin.presentation.ui.MapperDialog

class MapperAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project!!

        MapperDialog().onShow { result ->
            SettingsParseUseCase(
                settingsFilePath = project.basePath.orEmpty() + "/" + result.fileSettingsPath,
                project = project,
            ).settings()
                .map {
                    GeneratorEngine(it).run()
                }



            Messages.showInfoMessage(
                /* message = */ GENERATION_RESULT + result,
                /* title = */ TITLE
            )
        }
    }

    private fun extractClassMetadata(className: String, project: Project): ClassMetadata? {
        val psiClass = JavaPsiFacade.getInstance(project)
            .findClass(className, GlobalSearchScope.allScope(project))

        val projectPath = project.basePath.orEmpty()

        return if (psiClass != null) {
            val properties = psiClass.fields.map { it.name to it.type.canonicalText }
            ClassMetadata(
                className = className,
                properties = properties
            )
        } else {
            null
        }
    }

    companion object Constant {
        private const val TITLE = "Mapper Generator"
        private const val GENERATION_RESULT = "Generation result: "
    }
}