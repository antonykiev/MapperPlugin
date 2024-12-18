package org.mapper.generator.mapperplugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope
import org.mapper.generator.mapperplugin.actions.ui.MapperDialog
import org.mapper.generator.mapperplugin.logic.data.ClassMetadata

class MapperAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project!!
        val metadata = extractClassMetadata(project)

        MapperDialog(metadata!!).onShow { result ->
            Messages.showInfoMessage(
                /* message = */ GENERATION_RESULT + result,
                /* title = */ TITLE
            )
        }
    }

    private fun extractClassMetadata(project: Project): ClassMetadata? {
        val psiClass = JavaPsiFacade.getInstance(project)
            .findClass("org.mapper.generator.PersonResponse", GlobalSearchScope.allScope(project))

        return if (psiClass != null) {
            val properties = psiClass.fields.map { it.name to it.type.canonicalText }
            ClassMetadata(
                className = "org.mapper.generator.PersonResponse",
                projectBasePath = project.basePath.orEmpty(),
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