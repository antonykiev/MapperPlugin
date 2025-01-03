package org.mapper.generator.mapperplugin.data

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiType
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.util.PsiUtil
import org.jetbrains.kotlin.asJava.classes.KtLightClassForSourceDeclaration
import org.jetbrains.kotlin.idea.base.psi.imports.addImport
import org.jetbrains.kotlin.idea.base.psi.kotlinFqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.mapper.generator.mapperplugin.buisness.*
import org.mapper.generator.mapperplugin.data.states.GenerationStrategy
import org.mapper.generator.mapperplugin.data.states.MappingSettings
import java.io.File

class MapperEngine(
    private val project: Project,
    private val settings: MappingSettings
) {

    private val findPsiClassUseCase = FindPsiClassUseCase(project)
    private val findKtFileUseCase = FindKtFileUseCase(project)

    private val stringBuilder = StringBuilder()

    fun run(): Result<Unit> {
        stringBuilder.clear()
        val sourceClass = findPsiClassUseCase.invoke(settings.sourceClassName)
            ?: return Result.failure(Exception("Source class must be set"))
        val targetClass = findPsiClassUseCase(settings.targetClassName)
            ?: return Result.failure(Exception("Target class must be set"))

        val prefix = if (settings.generationStrategy == GenerationStrategy.EXTENSION_FUNCTION)
            "this"
        else
            LowerCaseStringUseCase(sourceClass.name.orEmpty()).invoke()

        when (settings.generationStrategy) {
            GenerationStrategy.GLOBAL_FUNCTION -> {
                stringBuilder.append("fun ${sourceClass.name.decapitalizeStr()}To${targetClass.name.capitalizeStr()}(${LowerCaseStringUseCase(sourceClass.name.orEmpty()).invoke()}: ${sourceClass.name}): ${targetClass.name} { return ${targetClass.name}(")
                build(
                    project = project,
                    sourceClass = targetClass,
                    targetClass = sourceClass,
                    parentChainName = "",
                    stringBuilder = stringBuilder,
                    prefix = prefix
                )
                stringBuilder.append(")}")
            }
            GenerationStrategy.EXTENSION_FUNCTION -> {
                stringBuilder.append("fun ${sourceClass.name}.to${targetClass.name.capitalizeStr()}(): ${targetClass.name} { return ${targetClass.name}(")
                build(
                    project = project,
                    sourceClass = targetClass,
                    targetClass = sourceClass,
                    parentChainName = "",
                    stringBuilder = stringBuilder,
                    prefix = prefix
                )
                stringBuilder.append(")}")
            }
            GenerationStrategy.OBJECT -> {
                val className = ClassNameUseCase(sourceClass, targetClass).invoke()
                stringBuilder.append("class $className {")
                stringBuilder.append("fun map(${LowerCaseStringUseCase(sourceClass.name.orEmpty()).invoke()}: ${sourceClass.name}): ${targetClass.name} { return ${targetClass.name}(")
                build(
                    project = project,
                    sourceClass = targetClass,
                    targetClass = sourceClass,
                    parentChainName = "",
                    stringBuilder = stringBuilder,
                    prefix = prefix
                )
                stringBuilder.append(")}")
                stringBuilder.append("}")
            }
        }

        when (settings.generationStrategy) {
            GenerationStrategy.GLOBAL_FUNCTION,
            GenerationStrategy.EXTENSION_FUNCTION -> {
                addGeneratedFunction(
                    project = project,
                    settings = settings,
                    sourceClass = sourceClass,
                    targetClass = targetClass,
                    stringBuilder = stringBuilder
                )
            }
            GenerationStrategy.OBJECT -> {
                addGeneratedClass(
                    project = project,
                    sourceClass = sourceClass,
                    targetClass = targetClass,
                    settings = settings,
                    stringBuilder = stringBuilder
                )
            }
        }

        return Result.success(Unit)
    }

    private fun addGeneratedFunction(
        project: Project,
        settings: MappingSettings,
        sourceClass: PsiClass,
        targetClass: PsiClass,
        stringBuilder: StringBuilder
    ) {
        val targetFile = findKtFileUseCase.invoke(settings.selectedFileName)
            ?: (sourceClass as KtLightClassForSourceDeclaration).kotlinOrigin.containingKtFile

        WriteCommandAction.runWriteCommandAction(project) {
            val psiFactory = KtPsiFactory(project)

            targetFile.findDescendantOfType<KtFunction> {
                it.name?.contains("to${targetClass.name}") ?: false && it.receiverTypeReference?.text == sourceClass.name
            }?.apply {
                delete()
            }
            val newFunction = psiFactory.createFunction(stringBuilder.toString())
            targetFile.add(newFunction)
        }
    }

    private fun addGeneratedClass(
        project: Project,
        sourceClass: PsiClass,
        targetClass:PsiClass,
        settings: MappingSettings,
        stringBuilder: StringBuilder
    ) {

        WriteCommandAction.runWriteCommandAction(project) {
            val psiFactory = KtPsiFactory(project)

            val newClass: KtClass = psiFactory.createClass(stringBuilder.toString())
            val element = CodeStyleManager.getInstance(project).reformat(newClass)

            val className = ClassNameUseCase(sourceClass, targetClass).invoke() + ".kt"
            val file = File(settings.selectedFileName, className)
            file.writeText(element.text)
        }
    }

    private fun build(
        project: Project,
        sourceClass: PsiClass,
        targetClass: PsiClass,
        parentChainName: String,
        stringBuilder: StringBuilder,
        prefix: String
    ) {
        val targetFile: KtFile = findKtFileUseCase.invoke(settings.selectedFileName)
            ?: (sourceClass as KtLightClassForSourceDeclaration).kotlinOrigin.containingKtFile

        WriteCommandAction.runWriteCommandAction(project) {
            sourceClass.kotlinFqName?.let { targetFile.addImport(it, false, null, project) }
        }

        sourceClass.fields.forEach { sourceField ->
            val targetField = targetClass.fields.find { it.name == sourceField.name }

            if (sourceField.type.asPsiClass().isKotlinDataClass()) {
                if (targetField == null || targetField.type.asPsiClass().isKotlinDataClass().not()) {
                    stringBuilder.append("${sourceField.name} = null,")
                } else {
                    stringBuilder.append("${sourceField.name} = ${sourceField.type.asPsiClass()?.name}(")
                    build(
                        project = project,
                        sourceClass = sourceField.type.asPsiClass()!!,
                        targetClass = targetField.type.asPsiClass()!!,
                        parentChainName = "$parentChainName.${sourceField.name}",
                        stringBuilder = stringBuilder,
                        prefix = prefix
                    )
                    stringBuilder.append(")")
                }
            } else {
                if (targetField == null) {
                    stringBuilder.append("${sourceField.name} = null,")
                } else {
                    stringBuilder.append("${sourceField.name} = ${prefix}${parentChainName}.${sourceField.name}" + ",")
                }
            }
        }
    }

    private fun PsiElement?.isKotlinDataClass(): Boolean {
        this ?: return false

        if (this is KtLightClassForSourceDeclaration) {
            return kotlinOrigin.isData()
        }
        return false
    }

    private fun String?.capitalizeStr() = CapitalizeString(this.orEmpty()).invoke()

    private fun String?.decapitalizeStr() = this?.replaceFirstChar { it.lowercase() }

    private fun PsiType.asPsiClass(): PsiClass? = PsiUtil.resolveClassInType(this)
}