package org.mapper.generator.mapperplugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import org.mapper.generator.mapperplugin.actions.ui.MapperDialog

class MapperAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        MapperDialog().onShow { result ->
            Messages.showInfoMessage(
                /* message = */ GENERATION_RESULT + result,
                /* title = */ TITLE
            )
        }
    }

    companion object Constant {
        private const val TITLE = "Mapper Generator"
        private const val GENERATION_RESULT = "Generation result: "
    }
}