package org.mapper.generator.mapperplugin.presentation.ui

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import org.mapper.generator.mapperplugin.presentation.ui.MapperDialog.Constant.GENERATE_IN_FILE
import org.mapper.generator.mapperplugin.presentation.ui.MapperDialog.Constant.GENERATE_IN_FOLDER

class MapperDialogController(
    private val onUpdateState: (MapperDialogState) -> Unit,
    private val event: AnActionEvent
) {

    private var sourceClassFieldSize = 0
    private var targetClassFieldSize = 0
    private var targetFileFieldSize = 0

    private var state = MapperDialogState(
        generateButtonEnabled = false,
        generateStrategy = GenerateStrategy.FILE,
        targetFileField = event.getData(CommonDataKeys.PSI_FILE)?.name.orEmpty(),
        generateInLabel = GENERATE_IN_FILE
    )
        private set(value) {
            field = value
            onUpdateState(value)
        }

    fun onUpdateSourceClassField(fieldSize: Int) {
        sourceClassFieldSize = fieldSize
        validateGenerateButtonState()
    }


    fun onUpdateTargetClassField(fieldSize: Int) {
        targetClassFieldSize = fieldSize
        validateGenerateButtonState()
    }


    fun onUpdateTargetFileFieldSize(value: String) {
        state = state.copy(targetFileField = value)
        targetFileFieldSize = value.length
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