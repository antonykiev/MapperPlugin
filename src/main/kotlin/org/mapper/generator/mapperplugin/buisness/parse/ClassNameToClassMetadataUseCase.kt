package org.mapper.generator.mapperplugin.buisness.parse

import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope
import org.mapper.generator.mapperplugin.buisness.states.ClassMetadata

class ClassNameToClassMetadataUseCase {

    fun classMetadata(
        project: Project,
        className: String
    ): Result<ClassMetadata> {
        return runCatching {
            val psiFacade = JavaPsiFacade.getInstance(project)
            val psiClass = psiFacade.findClass(className, GlobalSearchScope.allScope(project))
                ?: throw Exception("Can not init class - $className check it please")
            ClassMetadata(
                className = className,
                properties = psiClass.fields.map { it.name to it.type.canonicalText }
            )
        }
    }
}