package org.mapper.generator.mapperplugin.buisness

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

class NotificationUseCase(
    private val project: Project,
    private val groupName: String,
    private val content: String,
    private val type: NotificationType
) {

    operator fun invoke() {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(groupName)
            .createNotification(content, type)
            .notify(project)
    }
}