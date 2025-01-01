package org.mapper.generator.mapperplugin.presentation.ui

import com.intellij.collaboration.ui.util.name
import com.intellij.ide.util.TreeClassChooserFactory
import com.intellij.ide.util.TreeFileChooserFactory
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.base.psi.kotlinFqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.mapper.generator.mapperplugin.data.states.MappingSettings
import java.awt.BorderLayout
import java.awt.Color
import java.awt.event.ItemEvent
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.Document


class MapperDialog(
    private val project: Project,
    private val event: AnActionEvent
) : DialogWrapper(project) {

    private val controller = MapperDialogController(::updateState)

    private val sourceClassField = JBTextField()
    private val targetClassField = JBTextField()
    private val targetFileField = JBTextField()
    private val extensionFunctionRadio = JRadioButton(EXTENSION_FUNCTION, true)
    private val globalFunctionRadio = JRadioButton(GLOBAL_FUNCTION)
    private val classRadio = JRadioButton(SEPARATE_CLASS)

    private val generateInLabel = JBLabel(GENERATE_IN_FILE, SwingConstants.LEFT)
    private val createSelectFileButton = JButton(ELLIPSIS)

    init {
        title = GENERATE_MAPPING_FUNCTION
        init()
    }

    override fun createCenterPanel(): JComponent {
        sourceClassField.document.onUpdate {
            controller.onUpdateSourceClassField(sourceClassField.text.length)
        }
        targetClassField.document.onUpdate {
            controller.onUpdateTargetClassField(targetClassField.text.length)
        }
        targetFileField.document.onUpdate {
            controller.onUpdateTargetFileFieldSize(targetFileField.text.length)
        }

        sourceClassField.text = event.getData(CommonDataKeys.PSI_FILE)
            ?.findDescendantOfType<KtClass>()
            ?.kotlinFqName
            ?.asString()
            .orEmpty()

        targetFileField.text = event.getData(CommonDataKeys.PSI_FILE)?.name.orEmpty()


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
            add(generateInLabel, BorderLayout.WEST)
            add(targetFileField, BorderLayout.CENTER)
            add(createSelectFileButton, BorderLayout.EAST)
        }

        classRadio.addItemListener { e ->
            if (e.stateChange == ItemEvent.SELECTED) {
                targetFileField.text = ""
                generateInLabel.text = GENERATE_IN_FOLDER
            } else {
                generateInLabel.text = GENERATE_IN_FILE
                targetFileField.text = event.getData(CommonDataKeys.PSI_FILE)?.name.orEmpty()
            }

            controller.onClassRadioUpdated(e.stateChange == ItemEvent.SELECTED)
        }

        val functionTypePanel = JPanel().apply {
            border = BorderFactory.createTitledBorder(FUNCTION_TYPE)
                .apply { titleColor = JBColor(Color.BLACK, Color.WHITE) }
            add(extensionFunctionRadio)
            add(globalFunctionRadio)
            add(classRadio)
        }

        ButtonGroup().apply {
            add(extensionFunctionRadio)
            add(globalFunctionRadio)
            add(classRadio)
        }

        mainPanel.add(sourcePanel)
        mainPanel.add(targetPanel)
        mainPanel.add(targetFilePanel)
        mainPanel.add(Box.createVerticalStrut(8))
        mainPanel.add(functionTypePanel)

        okAction.isEnabled = false
        okAction.name = GENERATE

        return mainPanel
    }

    fun mappingSetting(): MappingSettings {
        return MappingSettings(
            sourceClassField.text,
            targetClassField.text,
            targetFileField.text,
            extensionFunctionRadio.isSelected
        )
    }

    private fun updateState(new: MapperDialogState) {
        okAction.isEnabled = new.generateButtonEnabled

        createSelectFileButton.actionListeners.forEach {
            createSelectFileButton.removeActionListener(it)
        }
        createSelectFileButton.addActionListener {
            when (new.generateStrategy) {
                GenerateStrategy.FILE -> showFileChooser(targetFileField, SELECT_MAPPER_FUNCTION_FILE)
                GenerateStrategy.FOLDER -> showFolderChooser(targetFileField, SELECT_MAPPER_FUNCTION_FILE)
            }
        }
    }

    private fun createSelectClassButton(
        classField: JBTextField,
        dialogTitle: String
    ): JButton {
        return JButton(ELLIPSIS).apply {
            addActionListener {
                val classChooser = TreeClassChooserFactory.getInstance(project)
                    .createAllProjectScopeChooser(dialogTitle)
                classChooser.showDialog()
                val selectedClass = classChooser.selected
                classField.text = selectedClass?.qualifiedName.orEmpty()
            }
        }
    }

    private fun showFolderChooser(fileField: JBTextField, dialogTitle: String) {
        val descriptor = FileChooserDescriptor(
            /* chooseFiles = */ false,
            /* chooseFolders = */ true,
            /* chooseJars = */ false,
            /* chooseJarsAsFiles = */ false,
            /* chooseJarContents = */ false,
            /* chooseMultiple = */ false
        )
        descriptor.title = SELECT_FOLDER
        descriptor.description = SELECT_FOLDER_DESCRIPTION

        val selectedFolder: VirtualFile? = FileChooser.chooseFile(descriptor, project, null)
        fileField.text = selectedFolder?.path.orEmpty()
    }

    private fun showFileChooser(fileField: JBTextField, dialogTitle: String) {
        val fileChooser = TreeFileChooserFactory.getInstance(project).createFileChooser(
            dialogTitle, null,
            KotlinFileType.INSTANCE, null,
        )
        fileChooser.showDialog()
        val selectedFile = fileChooser.selectedFile
        fileField.text = selectedFile?.name.orEmpty()
    }

    private fun Document.onUpdate(action: (DocumentEvent?) -> Unit) {
        addDocumentListener(
            /* listener = */ object : DocumentListener {
                override fun insertUpdate(e: DocumentEvent?) = action(e)
                override fun removeUpdate(e: DocumentEvent?) = action(e)
                override fun changedUpdate(e: DocumentEvent?) = action(e)
            }
        )
    }

    companion object Constant {
        private const val GENERATE_MAPPING_FUNCTION = "Generate Mapping Function"
        private const val EXTENSION_FUNCTION = "Extension Function"
        private const val GLOBAL_FUNCTION = "Global Function"
        private const val SEPARATE_CLASS = "Separate class"
        private const val SELECT_SOURCE_CLASS = "Select Source Class"
        private const val SELECT_TARGET_CLASS = "Select Target Class"
        private const val FROM = "From : "
        private const val GENERATE_IN_FOLDER = "Generate in folder:   "
        private const val GENERATE_IN_FILE = "Generate in file:   "
        private const val SELECT_MAPPER_FUNCTION_FILE = "Select Mapper Function File"
        private const val FUNCTION_TYPE = "Function Type"
        private const val TO = "To :     "
        private const val GENERATE = "Generate"
        private const val ELLIPSIS = "..."
        private const val SELECT_FOLDER = "Select Folder"
        private const val SELECT_FOLDER_DESCRIPTION = "Please select a folder."
    }
}
