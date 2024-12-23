package org.mapper.generator.mapperplugin.buisness.parse

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class TextFileUseCase(
    private val filePath: String,
) {
    fun text(): Result<String> {
        return runCatching {
            val path: Path = Paths.get(filePath)
            val lines: List<String> = Files.readAllLines(path)
            return@runCatching lines.joinToString("\n")
                .also { println(it) }
        }
    }
}