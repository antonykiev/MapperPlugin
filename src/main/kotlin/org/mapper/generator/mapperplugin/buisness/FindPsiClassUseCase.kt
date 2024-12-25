package org.mapper.generator.mapperplugin.buisness

import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.search.GlobalSearchScope

class FindPsiClassUseCase(
    private val project: Project
) {
    private val psiFacade = JavaPsiFacade.getInstance(project)

    operator fun invoke(className: String): PsiClass? {
        return psiFacade.findClass(className, GlobalSearchScope.allScope(project))
    }
}