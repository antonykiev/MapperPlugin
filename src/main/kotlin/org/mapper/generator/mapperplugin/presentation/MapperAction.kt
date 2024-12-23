package org.mapper.generator.mapperplugin.presentation

import com.intellij.execution.processTools.mapFlat
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import org.mapper.generator.mapperplugin.buisness.parse.SettingsParseUseCase
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
                .mapFlat {
                    GeneratorEngine(it).run()
                }.fold(
                    onSuccess = {
                        Messages.showInfoMessage(
                            /* message = */ GENERATION_RESULT + it,
                            /* title = */ TITLE
                        )
                    },
                    onFailure = {
                        Messages.showErrorDialog(
                            /* message = */ "FAILED " + it.message.orEmpty(),
                            /* title = */ TITLE
                        )
                    }
                )
        }
    }

    companion object Constant {
        private const val TITLE = "Mapper Generator"
        private const val GENERATION_RESULT = "Generation result: "
    }
}