package org.mapper.generator.mapperplugin.presentation.ui

import com.intellij.openapi.ui.DialogWrapper
import javax.swing.*

class MapperDialog(lastInput: String) : DialogWrapper(true) {

    private val inputField1 = JTextField(lastInput)
    private val message1 = JLabel(MESSAGE_FILE)

    init {
        title = TITLE
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.add(message1)
        panel.add(inputField1)
        panel.add(Box.createVerticalStrut(10))
        return panel
    }

    fun onShow(action: (result: InputResult) -> Unit)  {
        if (showAndGet()) {
            action(
                InputResult(
                    fileSettingsPath = inputField1.text,
                )
            )
        }
    }

    data class InputResult(
        val fileSettingsPath: String,
    )

    companion object Constant {
        private const val TITLE = "Mapper Generator"
        private const val MESSAGE_FILE = "Set your file settings:"
    }
}