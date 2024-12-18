package org.mapper.generator.mapperplugin.logic

class ClassByName(
    private val classLoader: ClassLoader
) {

    fun any(className: String): Any? {
        return try {
            val clazz = Class.forName(className, true, classLoader)
            clazz.getDeclaredConstructor().newInstance()
        } catch (e: ClassNotFoundException) {
            println("Class not found: $className")
            null
        } catch (e: NoSuchMethodException) {
            println("No default constructor available for: $className")
            null
        } catch (e: Exception) {
            println("Failed to initialize class $className: ${e.message}")
            null
        }
    }

    fun loadClassByName(className: String): Class<*>? {
        return try {
            Class.forName(className, true, classLoader)
        } catch (e: ClassNotFoundException) {
            println("Class not found: $className")
            null
        }
    }
}