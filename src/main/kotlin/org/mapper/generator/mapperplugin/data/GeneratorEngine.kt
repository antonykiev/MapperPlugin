package org.mapper.generator.mapperplugin.data

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import org.mapper.generator.mapperplugin.buisness.generation.*
import org.mapper.generator.mapperplugin.buisness.states.ClassMetadata
import org.mapper.generator.mapperplugin.buisness.states.Detail
import org.mapper.generator.mapperplugin.buisness.states.MappingSettings
import java.io.File

class GeneratorEngine(
    private val mappingSettings: MappingSettings
) {

    private val nestedMappingValidator = NestedMappingValidator()
    private val createMethodNameUseCase = CreateMethodNameUseCase()
    private val getPackageUseClass = GetPackageFromFullNameUseClass()
    private val getClassNameFromFullNameUseCase = GetClassNameFromFullNameUseCase()
    private val createStatementUseCase = CreateStatementUseCase()
    private val successMessageUseCase = SuccessMessageUseCase()

    fun run(): Result<String> {
        return runCatching {
            nestedMappingValidator(mappingSettings.mappingRules)

            val functionList = mappingSettings.mappingRules
                .map {
                    createFunSpec(
                        sourceClassMetaData = it.sourceClassMetaData,
                        targetClassMetaData = it.detail
                    )
                }

            val mapperClass: TypeSpec = createMapperSpec(functionList)
            val fileSpec: FileSpec = createFileSpec(mapperClass)
            writeMapperToFile(fileSpec)
            return@runCatching Result.success(successMessageUseCase(fileSpec, mappingSettings))
        }.getOrElse {
            return@getOrElse Result.failure(it)
        }
    }

    private fun createFunSpec(
        sourceClassMetaData: ClassMetadata,
        targetClassMetaData: Detail,
    ): FunSpec {

        val sourceShortClassName = getClassNameFromFullNameUseCase(sourceClassMetaData.className)
        val targetShortClassName = getClassNameFromFullNameUseCase(targetClassMetaData.targetMetaData.className)

        val methodName = createMethodNameUseCase(sourceShortClassName, targetShortClassName)

        val sourcePackageName = getPackageUseClass(sourceClassMetaData.className)
        val targetPackageName = getPackageUseClass(targetClassMetaData.targetMetaData.className)

        val statement = createStatementUseCase(
            sourceProperties = sourceClassMetaData.properties.map { it.first },
            detail = targetClassMetaData,
        )

        return FunSpec.builder(methodName)
            .returns(ClassName(targetPackageName, targetShortClassName))
            .addParameter("source", ClassName(sourcePackageName, sourceShortClassName))
            .addStatement("return %T($statement)", ClassName(targetPackageName, targetShortClassName))
            .build()
    }

    private fun createMapperSpec(functionList: List<FunSpec>): TypeSpec {
        val builder =
            TypeSpec.objectBuilder(getClassNameFromFullNameUseCase(mappingSettings.mapperName).ifEmpty { DEFAULT_MAPPER_NAME })
        functionList.forEach { builder.addFunction(it) }
        return builder.build()
    }

    private fun createFileSpec(typeSpec: TypeSpec): FileSpec {
        val packageName = mappingSettings.outputDir.ifEmpty { DEFAULT_MAPPER_PACKAGE }
        val fileName = getClassNameFromFullNameUseCase(mappingSettings.mapperName).ifEmpty { DEFAULT_MAPPER_NAME }
        return FileSpec.builder(
            packageName = packageName,
            fileName = fileName
        )
            .addType(typeSpec)
            .build()
    }

    private fun writeMapperToFile(fileSpec: FileSpec) {
        val outputDirectory = File(mappingSettings.projectBasePath)
        outputDirectory.mkdirs()
        fileSpec.writeTo(outputDirectory)
    }

    companion object Constant {
        const val DEFAULT_MAPPER_PACKAGE = "com.plugin.default"
        const val DEFAULT_MAPPER_NAME = "Mapper"
    }
}