package org.mapper.generator.mapperplugin.data

import com.intellij.ide.util.PropertiesComponent

object Storage {

    private val properties = PropertiesComponent.getInstance()

    private val keyEmptyException = IllegalArgumentException("Key must not be empty")

    fun saveKeyValue(key: String, value: String) {
        if (key.isEmpty()) throw keyEmptyException
        properties.setValue(key, value)
    }

    fun getKeyValue(key: String): Result<String> {
        if (key.isEmpty()) throw keyEmptyException
        return runCatching { properties.getValue(key)!! }
    }
}