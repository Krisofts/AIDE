package com.krisoft.aide.sdkmanager

import kotlinx.serialization.Serializable

@Serializable
enum class SdkComponentType {
    JDK,
    CMDLINE_TOOLS,
    PLATFORM,
    BUILD_TOOLS,
    PLATFORM_TOOLS,
}

/**
 * Bagaimana komponen ini benar-benar dipasang. Berdasarkan riset terhadap proyek
 * sejenis (AndroidCSOfficial/android-code-studio) - SDK Manager di ekosistem ini
 * umumnya bukan installer custom untuk setiap komponen, melainkan JDK + Android
 * command-line tools yang di-bootstrap manual, lalu tool `sdkmanager` RESMI dari
 * Google (bagian dari command-line tools) yang dipanggil lewat Terminal untuk
 * pasang platform/build-tools/platform-tools - bukan reimplementasi sendiri.
 * Lihat docs/MASTER_PLAN.md §7.
 */
@Serializable
enum class InstallMethod {
    /** Diunduh & dipasang langsung oleh AIDE (dipakai untuk JDK & command-line tools). */
    DIRECT_DOWNLOAD,

    /** Dipasang lewat `sdkmanager` resmi via Terminal - butuh Phase 3 (Terminal) selesai dulu. */
    VIA_SDKMANAGER,
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
    val installMethod: InstallMethod = InstallMethod.DIRECT_DOWNLOAD,
)

@Serializable
data class SdkManifest(
    val schemaVersion: Int,
    val components: List<SdkComponent>,
)
