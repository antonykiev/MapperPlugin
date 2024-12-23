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
        project: Project,
        filePath: String
    ): Result<String> {
        return runCatching {
            val virtualFile = createVirtualFile(filePath)
            return@runCatching readFileContentWithUnsavedChanges(virtualFile).also { println(it) }
                ?: throw Exception("File is empty")
        }
    }

    private fun createVirtualFile(path: String): VirtualFile? {
        val ioFile = File(path)

        // Refresh the local file system to make the new file visible to the VFS
        val localFileSystem = LocalFileSystem.getInstance()
        localFileSystem.refreshAndFindFileByIoFile(ioFile)
        localFileSystem.refresh(false)

        // Find the VirtualFile in the VFS
        return localFileSystem.findFileByIoFile(ioFile)
    }

    private fun readFileContentWithUnsavedChanges(virtualFile: VirtualFile?): String? {
        virtualFile ?: return null
        val fileDocumentManager = FileDocumentManager.getInstance()
        val document = fileDocumentManager.getDocument(virtualFile)
        return if (document != null && fileDocumentManager.isFileModified(virtualFile)) {
            document.text // Use unsaved changes
        } else {
            virtualFile.contentsToByteArray().toString(StandardCharsets.UTF_8) // Use VFS content
        }
    }
}