package com.krisoft.aide.sdkmanager

import kotlinx.serialization.Serializable

@Serializable
enum class SdkComponentType {
    JDK,
    PLATFORM,
    BUILD_TOOLS,
    PLATFORM_TOOLS,
}

/**
 * Satu komponen SDK yang bisa diinstal (JDK, Android platform, build-tools, dst).
 * [abi] adalah target arsitektur perangkat untuk build ini (mis. "arm64-v8a") supaya
 * satu manifest bisa menampung beberapa build untuk ABI berbeda.
 */
@Serializable
data class SdkComponent(
    val id: String,
    val type: SdkComponentType,
    val displayName: String,
    val version: String,
    val abi: String,
    val downloadUrl: String,
    val sha256: String,
    val sizeBytes: Long,
    val stripTopLevelDir: Boolean = true,
)

@Serializable
data class SdkManifest(
    val schemaVersion: Int,
    val components: List<SdkComponent>,
)
