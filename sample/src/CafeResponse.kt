package org.mapper.generator

data class CafeResponse(
    val id: Long,
    val address: String,
    val code: Int,
    val description: String,
    val manager: ManagerResponse
)
