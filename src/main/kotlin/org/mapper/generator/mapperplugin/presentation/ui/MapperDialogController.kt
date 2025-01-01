package org.mapper.generator.mapperplugin.presentation.ui

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import org.mapper.generator.mapperplugin.presentation.ui.MapperDialog.Constant.GENERATE_IN_FILE
import org.mapper.generator.mapperplugin.presentation.ui.MapperDialog.Constant.GENERATE_IN_FOLDER
import kotlin.properties.Delegates

class MapperDialogController(
    private val onUpdateState: (MapperDialogState) -> Unit,
    private val event: AnActionEvent
) {

    private var sourceClassFieldSize = 0
    private var targetClassFieldSize = 0
    private var targetFileFieldSize = 0

    private var state by Delegates.observable(
        initialValue = MapperDialogState(
            generateButtonEnabled = false,
            generateStrategy = GenerateStrategy.FILE,
            targetFileField = event.getData(CommonDataKeys.PSI_FILE)?.name.orEmpty(),
            generateInLabel = GENERATE_IN_FILE
        )
    ) { _, _, new ->
        onUpdateState(new)
    }

    fun onUpdateSourceClassField(fieldSize: Int) {
        sourceClassFieldSize = fieldSize
        validateGenerateButtonState()
    }


    fun onUpdateTargetClassField(fieldSize: Int) {
        targetClassFieldSize = fieldSize
        validateGenerateButtonState()
    }


    fun onUpdateTargetFileFieldSize(fieldSize: Int) {
        targetFileFieldSize = fieldSize
        validateGenerateButtonState()
    }

    fun onClassRadioUpdated(isSelected: Boolean) {
        state = state.copy(
            generateStrategy = if (isSelected) GenerateStrategy.FOLDER else GenerateStrategy.FILE,
            targetFileField = if (isSelected) "" else event.getData(CommonDataKeys.PSI_FILE)?.name.orEmpty(),
            generateInLabel = if (isSelected) GENERATE_IN_FOLDER else GENERATE_IN_FILE
        )
    }

    private fun validateGenerateButtonState() {
        state = if (sourceClassFieldSize > 0 && targetClassFieldSize > 0 && targetFileFieldSize > 0) {
            state.copy(generateButtonEnabled = true)
        } else {
            state.copy(generateButtonEnabled = false)
        }
    }

}