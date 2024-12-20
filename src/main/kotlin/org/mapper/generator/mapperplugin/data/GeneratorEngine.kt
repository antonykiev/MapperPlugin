package org.mapper.generator.mapperplugin.data

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import org.mapper.generator.mapperplugin.buisness.generation.CreateMethodNameUseCase
import org.mapper.generator.mapperplugin.buisness.generation.CreateStatementUseCase
import org.mapper.generator.mapperplugin.buisness.generation.GetClassNameFromFullNameUseCase
import org.mapper.generator.mapperplugin.buisness.generation.GetPackageUseClass
import org.mapper.generator.mapperplugin.buisness.states.ClassMetadata
import org.mapper.generator.mapperplugin.buisness.states.MappingSettings
import java.io.File

class GeneratorEngine(
    private val mappingSettings: MappingSettings
) {

    private val createMethodNameUseCase = CreateMethodNameUseCase()
    private val getPackageUseClass = GetPackageUseClass()
    private val getClassNameFromFullNameUseCase = GetClassNameFromFullNameUseCase()
    private val createStatementUseCase = CreateStatementUseCase()

    fun run() {
        val functionList = mappingSettings.mappingRules
            .map {
                createFunSpec(
                    sourceClassMetaData = it.sourceClassMetaData,
                    targetClassMetaData = it.targetMetaData
                )
            }

        val mapperClass: TypeSpec = createMapperSpec(functionList)
        val fileSpec: FileSpec = createFileSpec(mapperClass)
        writeMapperToFile(fileSpec)
    }

    private fun createFunSpec(
        sourceClassMetaData: ClassMetadata,
        targetClassMetaData: ClassMetadata,
    ): FunSpec {

        val sourceShortClassName = getClassNameFromFullNameUseCase(sourceClassMetaData.className)
        val targetShortClassName = getClassNameFromFullNameUseCase(targetClassMetaData.className)

        val methodName = createMethodNameUseCase(sourceShortClassName, targetShortClassName)

        val sourcePackageName = getPackageUseClass(sourceClassMetaData.className)
        val targetPackageName = getPackageUseClass(targetClassMetaData.className)

        val statement = createStatementUseCase(
            sourceProperties = sourceClassMetaData.properties.map { it.first },
            targetProperties = targetClassMetaData.properties.map { it.first },
        )

        return FunSpec.builder(methodName)
            .returns(ClassName(targetPackageName, targetShortClassName))
            .addParameter("source", ClassName(sourcePackageName, sourceShortClassName))
            .addStatement("return %T($statement)", ClassName(targetPackageName, targetShortClassName))
            .build()
    }

    private fun createMapperSpec(functionList: List<FunSpec>): TypeSpec {
        val builder = TypeSpec.objectBuilder("Mapper")
        functionList.forEach { builder.addFunction(it) }
        return builder.build()
    }

    private fun createFileSpec(typeSpec: TypeSpec): FileSpec {
        return FileSpec.builder("com.example.mapper", "Mapper")
            .addType(typeSpec)
            .build()
    }

    private fun writeMapperToFile(fileSpec: FileSpec) {
        val outputDirectory = File(mappingSettings.projectBasePath + mappingSettings.outputDir)
        outputDirectory.mkdirs()
        fileSpec.writeTo(outputDirectory)
    }
}