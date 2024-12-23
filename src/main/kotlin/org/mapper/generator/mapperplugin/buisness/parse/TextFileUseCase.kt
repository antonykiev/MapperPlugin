package org.mapper.generator.mapperplugin.buisness.parse

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class TextFileUseCase {

    operator fun invoke(filePath: String): Result<String> {
        return runCatching {
            val path: Path = Paths.get(filePath)
            val lines: List<String> = Files.readAllLines(path)
            return@runCatching lines.joinToString("\n")
                .also { println(it) }
        }
    }
}