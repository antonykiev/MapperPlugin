package org.mapper.generator.mapperplugin.actions.ui

import com.intellij.openapi.ui.DialogWrapper
import org.mapper.generator.mapperplugin.logic.ClassMetadata
import org.mapper.generator.mapperplugin.logic.Generator
import javax.swing.*

class MapperDialog(
    private val metadata: ClassMetadata
) : DialogWrapper(true) {

    private val inputField1 = JTextField()
    private val inputField2 = JTextField()
    private val message1 = JLabel(MESSAGE_FILE)
    private val message2 = JLabel(MESSAGE_FOLDER)

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
        panel.add(message2)
        panel.add(inputField2)
        return panel
    }

    fun onShow(action: (result: String) -> Unit)  {
        if (showAndGet()) {
            Generator(metadata).run()
            action(
//                result
                "Success"
            )
        }
    }

    companion object Constant {
        private const val TITLE = "Mapper Generator"
        private const val MESSAGE_FILE = "Set your file settings:"
        private const val MESSAGE_FOLDER = "Set your folder for generating code:"
    }
}