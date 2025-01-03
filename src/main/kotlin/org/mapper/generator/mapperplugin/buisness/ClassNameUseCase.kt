package org.mapper.generator.mapperplugin.buisness

import com.intellij.psi.PsiClass

class ClassNameUseCase(
    private val sourcePsiClass: PsiClass,
    private val targetPsiClass: PsiClass,
) {

    operator fun invoke(): String {
        return "${CapitalizeString(sourcePsiClass.name.orEmpty()).invoke()}" +
                "To" +
                "${CapitalizeString(targetPsiClass.name.orEmpty()).invoke()}" +
                "Mapper"
    }
}