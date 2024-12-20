package org.mapper.generator.mapperplugin.data

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import org.mapper.generator.mapperplugin.buisness.generation.*
import org.mapper.generator.mapperplugin.buisness.generation.CreateMethodNameUseCase
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

    fun run() {
        //hard code
        println(mappingSettings.mappingRules[0].sourceClassMetaData)
        val mapFunction = createFunSpec(
            sourceClassMetaData = mappingSettings.mappingRules[0].sourceClassMetaData,
            targetClassMetaData = mappingSettings.mappingRules[0].targetMetaData
        )
        val mapperClass: TypeSpec = createMapperSpec(mapFunction)
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

        return FunSpec.builder(methodName)
            .returns(ClassName(targetPackageName, targetShortClassName))
            .addParameter("source", ClassName(sourcePackageName, sourceShortClassName))
            .addStatement("return %T()", ClassName(targetPackageName, targetShortClassName))
            //.addStatement("target.someField = source.someField")
            .build()
    }

    private fun createMapperSpec(funSpec: FunSpec): TypeSpec {
        return TypeSpec.objectBuilder("Mapper")
            .addFunction(funSpec)
            .build()
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