package com.ceassar.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SourceEntity(
    val name: String,
    val url: String
) {
}