package org.mapper.generator.mapperplugin.data

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiType
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiUtil
import org.jetbrains.kotlin.asJava.classes.KtLightClassForSourceDeclaration
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.base.psi.imports.addImport
import org.jetbrains.kotlin.idea.base.psi.kotlinFqName
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.mapper.generator.mapperplugin.buisness.FindKtFileUseCase
import org.mapper.generator.mapperplugin.buisness.FindPsiClassUseCase
import org.mapper.generator.mapperplugin.data.states.MappingSettings

class GeneratorEngine(
    private val project: Project,
    private val settings: MappingSettings
) {

    private val findPsiClassUseCase = FindPsiClassUseCase(project)
    private val findKtFileUseCase = FindKtFileUseCase(project)

    private val stringBuilder = StringBuilder()

    fun run(): Result<String> {
        stringBuilder.clear()
        val sourceClass = findPsiClassUseCase.invoke(settings.sourceClassName)
        val targetClass = findPsiClassUseCase(settings.targetClassName)

        if (sourceClass != null && targetClass != null) {
            val prefix = if (settings.isExtensionFunc) "this" else sourceClass.name!!.lowercase()
            val targetFile = findKtFileUseCase.invoke(settings.selectedFileName)
                ?: (sourceClass as KtLightClassForSourceDeclaration).kotlinOrigin.containingKtFile

            if (settings.isExtensionFunc) {
                stringBuilder.append("fun ${sourceClass.name}.To${targetClass.name}(): ${targetClass.name} { return ${targetClass.name}(")
                build(
                    project = project,
                    sourceClass = targetClass,
                    targetClass = sourceClass,
                    parentChainName = "",
                    targetFile = targetFile,
                    stringBuilder = stringBuilder,
                    prefix = prefix
                )
                stringBuilder.append(")}")
            } else {
                stringBuilder.append("fun ${sourceClass.name}to${targetClass.name}(${sourceClass.name?.lowercase()}: ${sourceClass.name}): ${targetClass.name} { return ${targetClass.name}(")
                build(
                    project = project,
                    sourceClass = targetClass,
                    targetClass = sourceClass,
                    parentChainName = "",targetFile = targetFile,
                    stringBuilder = stringBuilder,
                    prefix = prefix)
                stringBuilder.append(")}")
            }
            try {
                appendGeneratedCode(project, settings.targetClassName, sourceClass, targetClass, targetFile, stringBuilder)
            } finally {
                NotificationGroupManager.getInstance()
                    .getNotificationGroup("Kotlin Data Mapper")
                    .createNotification("Mapping function generated", NotificationType.INFORMATION)
                    .notify(project)
            }

        }
        return Result.success("")
    }

    private fun appendGeneratedCode(
        project: Project,
        selectedFileName: String?,
        sourceClass: PsiClass,
        targetClass: PsiClass,
        targetFile: KtFile,
        stringBuilder: StringBuilder
    ) {
        WriteCommandAction.runWriteCommandAction(project) {
            val psiFactory = KtPsiFactory(project)

            val containingFile = targetFile ?: findKtFileByName(project, selectedFileName)
            ?: (sourceClass as KtLightClassForSourceDeclaration).kotlinOrigin.containingKtFile

            containingFile.findDescendantOfType<KtFunction> {
                it.name?.contains("to${targetClass.name}") ?: false && it.receiverTypeReference?.text == sourceClass.name
            }?.apply {
                delete()
            }

            val newFunction = psiFactory.createFunction(stringBuilder.toString())
            containingFile.add(newFunction)
        }
    }

    private fun findKtFileByName(project: Project, fileName: String?): KtFile? {
        fileName ?: return null

        val ktExtension = KotlinFileType.INSTANCE.defaultExtension
        val ktFile: KtFile? =
            FilenameIndex.getVirtualFilesByName(fileName, GlobalSearchScope.allScope(project)).find { virtualFile ->
                virtualFile.extension == ktExtension && virtualFile.toPsiFile(project) is KtFile
            }?.toPsiFile(project) as KtFile?

        return ktFile
    }

    private fun build(
        project: Project,
        sourceClass: PsiClass,
        targetClass: PsiClass,
        parentChainName: String,
        targetFile: KtFile,
        stringBuilder: StringBuilder,
        prefix: String
    ) {
        WriteCommandAction.runWriteCommandAction(project) {
            sourceClass.kotlinFqName?.let { targetFile?.addImport(it, false, null, project) }
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
                        targetFile = targetFile,
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

    private fun PsiType.asPsiClass(): PsiClass? = PsiUtil.resolveClassInType(this)


    companion object Constant {

    }
}