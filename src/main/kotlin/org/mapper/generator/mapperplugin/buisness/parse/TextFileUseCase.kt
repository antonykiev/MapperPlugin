package org.mapper.generator.mapperplugin.buisness.parse

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.nio.charset.StandardCharsets

class TextFileUseCase {

    operator fun invoke(
        filePath: String
    ): Result<String> {
        return runCatching {
            val virtualFile = createVirtualFile(filePath)
            return@runCatching readFileContentWithUnsavedChanges(virtualFile).also { println(it) }
        }
    }

    private fun createVirtualFile(path: String): VirtualFile {
        val ioFile = File(path)
        val localFileSystem = LocalFileSystem.getInstance()
        localFileSystem.refreshAndFindFileByIoFile(ioFile)
        localFileSystem.refresh(false)
        return localFileSystem.findFileByIoFile(ioFile) ?: throw Exception("File not found")
    }

    private fun readFileContentWithUnsavedChanges(virtualFile: VirtualFile): String {
        val fileDocumentManager = FileDocumentManager.getInstance()
        val document = fileDocumentManager.getDocument(virtualFile)
        return if (document != null && fileDocumentManager.isFileModified(virtualFile)) {
            document.text
        } else {
            virtualFile.contentsToByteArray().toString(StandardCharsets.UTF_8)
        }
    }
}