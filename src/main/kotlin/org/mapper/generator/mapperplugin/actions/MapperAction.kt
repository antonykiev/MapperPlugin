package org.mapper.generator.mapperplugin.actions


import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.ui.Messages
import org.mapper.generator.mapperplugin.actions.ui.MapperDialog
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope
import org.mapper.generator.mapperplugin.logic.ClassMetadata

class MapperAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project!!
        val metadata = extractClassMetadata(project)

        MapperDialog(metadata!!).onShow { result ->
            Messages.showInfoMessage(
                /* message = */ GENERATION_RESULT + result,
                /* title = */ TITLE
            )
        }
    }

    private fun extractClassMetadata(project: Project): ClassMetadata? {
        val psiClass = JavaPsiFacade.getInstance(project)
            .findClass("org.mapper.generator.PersonResponse", GlobalSearchScope.allScope(project))

        return if (psiClass != null) {
            val properties = psiClass.fields.map { it.name to it.type.canonicalText }
            ClassMetadata(
                className = "org.mapper.generator.PersonResponse",
                projectBasePath = project.basePath.orEmpty(),
                properties = properties
            )
        } else {
            null
        }
    }

    fun loadUserResponseClass(project: Project, className: String): Any? {
        val module = ModuleManager.getInstance(project).modules.firstOrNull()!!
        val sdk = ModuleRootManager.getInstance(module).sdk
        val classLoader = sdk?.sdkType?.javaClass?.classLoader!!

        return try {
            val clazz = classLoader.loadClass(className)
            clazz.getDeclaredConstructor().newInstance()
        } catch (e: ClassNotFoundException) {
            println("Class not found: $className")
            null
        } catch (e: Exception) {
            println("Failed to load class: ${e.message}")
            null
        }
    }

    fun getModuleClassLoader(module: Module): ClassLoader? {
        val sdk = ModuleRootManager.getInstance(module).sdk
        return sdk?.sdkType?.javaClass?.classLoader
    }

    companion object Constant {
        private const val TITLE = "Mapper Generator"
        private const val GENERATION_RESULT = "Generation result: "
    }
}