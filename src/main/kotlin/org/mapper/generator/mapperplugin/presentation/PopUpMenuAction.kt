package org.mapper.generator.mapperplugin.presentation

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.mapper.generator.mapperplugin.data.GeneratorEngine
import org.mapper.generator.mapperplugin.presentation.ui.MapperDialog

class PopUpMenuAction: AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val dialog = MapperDialog(project, event)

        if (dialog.showAndGet()) {
            GeneratorEngine(
                project = project,
                settings = dialog.mappingSetting()
            ).run()
        }
    }
}