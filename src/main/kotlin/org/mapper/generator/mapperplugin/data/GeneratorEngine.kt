package org.mapper.generator.mapperplugin.data

import com.squareup.kotlinpoet.*
import org.mapper.generator.mapperplugin.buisness.data.ClassMetadata
import java.io.File

class GeneratorEngine(
    private val metadata: ClassMetadata
) {


    fun run() {
        println("Generating mapper for class ${metadata.className}")
        val mapFunction = FunSpec.builder("map")
            .addParameter("source", ClassName("com.example", metadata.className))
            .addParameter("target", ClassName("com.example", "TargetClass"))
            .addStatement("target.someField = source.someField")  // Example mapping
            .build()

        // Define the mapper class
        val mapperClass = TypeSpec.classBuilder("Mapper")
            .addFunction(mapFunction)
            .build()

        // Create the Kotlin file
        val fileSpec = FileSpec.builder("com.example.mapper", "Mapper")
            .addType(mapperClass)
            .build()

        val outputDirectory = File(metadata.projectBasePath + outputDir)
        outputDirectory.mkdirs() // Ensure the directory exists
        val fileResult = fileSpec.writeTo(outputDirectory)

        println("Generating mapper finished FILE RESULT: $fileResult")

    }
}

// Specify the output directory
val outputDir = "/generated"