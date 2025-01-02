package org.mapper.generator.mapperplugin.presentation

import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.mapper.generator.mapperplugin.buisness.NotificationUseCase
import org.mapper.generator.mapperplugin.data.MapperEngine
import org.mapper.generator.mapperplugin.presentation.ui.MapperDialog

class PopUpMenuAction : AnAction() {


    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val dialog = MapperDialog(project, event)

        val notificationUseCase = NotificationUseCase(
            project = project,
            groupName = "MapCraft",
            content = "Mapping function generated",
            type = NotificationType.INFORMATION
        )
        if (!dialog.showAndGet()) return

        MapperEngine(
            project = project,
            settings = dialog.mappingSetting()
        ).run().fold(
            onSuccess = {
                notificationUseCase.invoke()
            },
            onFailure = {

            }
        )
    }
}