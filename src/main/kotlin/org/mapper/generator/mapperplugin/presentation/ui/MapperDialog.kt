package org.mapper.generator.mapperplugin.presentation.ui

import com.intellij.collaboration.ui.util.name
import com.intellij.icons.AllIcons
import com.intellij.ide.BrowserUtil
import com.intellij.ide.util.TreeClassChooserFactory
import com.intellij.ide.util.TreeFileChooserFactory
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.impl.ActionButton
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.JBColor
import com.intellij.ui.components.ActionLink
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.base.psi.kotlinFqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.Document


class MapperDialog(
    private val project: Project,
    event: AnActionEvent
) : DialogWrapper(project) {

    private val sourceClassField = JBTextField().apply {
        text = event.getData(CommonDataKeys.PSI_FILE)?.findDescendantOfType<KtClass>()?.kotlinFqName?.asString() ?: ""
    }
    private val targetClassField = JBTextField()
    private val targetFileField = JBTextField().apply {
        text = event.getData(CommonDataKeys.PSI_FILE)?.name ?: ""
    }
    private val extensionFunctionRadio = JRadioButton(EXTENSION_FUNCTION, true)
    private val globalFunctionRadio = JRadioButton(GLOBAL_FUNCTION)

    init {
        title = GENERATE_MAPPING_FUNCTION
        init()
    }

    override fun createCenterPanel(): JComponent {
        val mainPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
        }

        val sourcePanel = JPanel(BorderLayout()).apply {
            add(JBLabel(FROM, SwingConstants.LEFT), BorderLayout.WEST)
            add(sourceClassField, BorderLayout.CENTER)
            add(createSelectClassButton(sourceClassField, SELECT_SOURCE_CLASS), BorderLayout.EAST)
        }

        val targetPanel = JPanel(BorderLayout()).apply {
            add(JBLabel(TO, SwingConstants.LEFT), BorderLayout.WEST)
            add(targetClassField, BorderLayout.CENTER)
            add(createSelectClassButton(targetClassField, SELECT_TARGET_CLASS), BorderLayout.EAST)
        }

        val targetFilePanel = JPanel(BorderLayout()).apply {
            add(JBLabel(GENERATE_IN, SwingConstants.LEFT), BorderLayout.WEST)
            add(targetFileField, BorderLayout.CENTER)
            add(createSelectFileButton(targetFileField, SELECT_MAPPER_FUNCTION_FILE), BorderLayout.EAST)
        }

        val functionTypePanel = JPanel().apply {
            border = BorderFactory.createTitledBorder(FUNCTION_TYPE)
                .apply { titleColor = JBColor(Color.BLACK, Color.WHITE) }
            add(extensionFunctionRadio)
            add(globalFunctionRadio)
        }

        ButtonGroup().apply {
            add(extensionFunctionRadio)
            add(globalFunctionRadio)
        }

        val myAction: AnAction = object : AnAction(OPEN_SETTINGS_ACTION) {
            override fun actionPerformed(e: AnActionEvent) {
                openSettingsPanel()
            }
        }
        val presentation = Presentation(CUSTOMIZE_SETTINGS).apply {
            setIcon(AllIcons.General.Settings)
        }
        val actionButton = ActionButton(myAction, presentation, ActionPlaces.UNKNOWN, Dimension(40, 40)).marginRight(20)

        val settingsRatingRow = JPanel(BorderLayout()).apply {
            add(actionButton, BorderLayout.WEST)
            add(JBLabel(LIKE_THIS, SwingConstants.CENTER), BorderLayout.CENTER)
            add(ActionLink(MY_GITHUB_URL) {
                BrowserUtil.browse(MY_GITHUB_URL)
            }, BorderLayout.EAST)
            marginTop(8)
        }

        sourceClassField.document.onUpdate(::updateOkButtonState)
        targetClassField.document.onUpdate(::updateOkButtonState)
        targetFileField.document.onUpdate(::updateOkButtonState)

        // Add all components to the main panel
        mainPanel.add(sourcePanel)
        mainPanel.add(targetPanel)
        mainPanel.add(targetFilePanel)
        mainPanel.add(Box.createVerticalStrut(8))
        mainPanel.add(functionTypePanel)
        mainPanel.add(settingsRatingRow)

        okAction.isEnabled = false
        okAction.name = GENERATE

        return mainPanel
    }

    private fun createSelectClassButton(classField: JBTextField, dialogTitle: String): JButton {
        return JButton("...").apply {
            addActionListener {
                val classChooser = TreeClassChooserFactory.getInstance(project)
                    .createAllProjectScopeChooser(dialogTitle) // todo: Search for kotlin data classes only
                classChooser.showDialog()
                val selectedClass = classChooser.selected
                classField.text = selectedClass?.qualifiedName ?: ""
            }
        }
    }

    private fun createSelectFileButton(fileField: JBTextField, dialogTitle: String): JButton {
        return JButton("...").apply {
            addActionListener {
                val fileChooser = TreeFileChooserFactory.getInstance(project).createFileChooser(
                    dialogTitle, null,
                    KotlinFileType.INSTANCE, null,
                )
                fileChooser.showDialog()
                val selectedFile = fileChooser.selectedFile
                fileField.text = selectedFile?.name ?: ""
            }
        }
    }

    fun getSelectedClasses(): Pair<String?, String?> {
        return Pair(sourceClassField.text, targetClassField.text)
    }

    fun getSelectedFileName(): String? {
        return targetFileField.text
    }

    fun isExtensionFunctionSelected(): Boolean {
        return extensionFunctionRadio.isSelected
    }

    fun openSettingsPanel() {
//        ShowSettingsUtil.getInstance().showSettingsDialog(
//            null,  // Pass the current project if needed
//            AppSettingsConfigurable::class.java
//        )
    }

    private fun updateOkButtonState(e: DocumentEvent?) {
        val allFieldsFilled =
            sourceClassField.text.isNotEmpty() && targetClassField.text.isNotEmpty() && targetFileField.text.isNotEmpty()
        okAction.isEnabled = allFieldsFilled
    }

    companion object Constant {
        private const val MY_GITHUB_URL = "https://github.com/antonykiev/MapperPlugin"
        private const val GENERATE_MAPPING_FUNCTION = "Generate Mapping Function"
        private const val EXTENSION_FUNCTION = "Extension Function"
        private const val GLOBAL_FUNCTION = "Global Function"
        private const val SELECT_SOURCE_CLASS = "Select Source Class"
        private const val SELECT_TARGET_CLASS = "Select Target Class"
        private const val FROM = "From : "
        private const val GENERATE_IN = "Generate in :   "
        private const val SELECT_MAPPER_FUNCTION_FILE = "Select Mapper Function File"
        private const val FUNCTION_TYPE = "Function Type"
        private const val TO = "To :     "
        private const val OPEN_SETTINGS_ACTION = "Open Settings Action"
        private const val CUSTOMIZE_SETTINGS = "Customize Settings"
        private const val LIKE_THIS = "Like this version? Please star here: "
        private const val GENERATE = "Generate"
    }
}

fun Document.onUpdate(action: (DocumentEvent?) -> Unit) {
    addDocumentListener(object : DocumentListener {
        override fun insertUpdate(e: DocumentEvent?) = action(e)
        override fun removeUpdate(e: DocumentEvent?) = action(e)
        override fun changedUpdate(e: DocumentEvent?) = action(e)
    })
}