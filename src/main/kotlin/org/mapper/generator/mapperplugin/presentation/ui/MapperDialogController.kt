package org.mapper.generator.mapperplugin.presentation.ui

import kotlin.properties.Delegates

class MapperDialogController(onUpdateState: (MapperDialogState) -> Unit) {

    private var sourceClassFieldSize = 0
    private var targetClassFieldSize = 0
    private var targetFileFieldSize = 0

    private var state by Delegates.observable(
        initialValue = MapperDialogState(
            generateButtonEnabled = false,
            generateStrategy = GenerateStrategy.FILE
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

    fun onClassRadioUpdated(b: Boolean) {
        state = state.copy(generateStrategy = if (b) GenerateStrategy.FOLDER else GenerateStrategy.FILE)
    }

    private fun validateGenerateButtonState() {
        state = if (sourceClassFieldSize > 0 && targetClassFieldSize > 0 && targetFileFieldSize > 0) {
            state.copy(generateButtonEnabled = true)
        } else {
            state.copy(generateButtonEnabled = false)
        }
    }

}