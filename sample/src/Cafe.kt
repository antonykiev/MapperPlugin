package org.mapper.generator

data class Cafe(
    val id: Long,
    val address: String,
    val code: Int,
    val description: String,
    val manager: Manager
)
