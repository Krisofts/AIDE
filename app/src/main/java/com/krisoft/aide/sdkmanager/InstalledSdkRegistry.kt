package com.krisoft.aide.sdkmanager

import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class InstalledComponent(
    val id: String,
    val version: String,
    val installPath: String,
)

@Serializable
private data class InstalledComponentsFile(
    val entries: List<InstalledComponent> = emptyList(),
)

/**
 * Mencatat komponen SDK yang sudah terinstal di penyimpanan privat aplikasi,
 * supaya UI tahu status install tanpa perlu scan filesystem tiap kali dibuka.
 */
class InstalledSdkRegistry(private val registryFile: File) {

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    @Synchronized
    fun loadAll(): List<InstalledComponent> {
        if (!registryFile.exists()) return emptyList()
        return try {
            json.decodeFromString<InstalledComponentsFile>(registryFile.readText()).entries
        } catch (_: Exception) {
            emptyList()
        }
    }

    @Synchronized
    fun get(id: String): InstalledComponent? = loadAll().firstOrNull { it.id == id }

    @Synchronized
    fun markInstalled(component: InstalledComponent) {
        val current = loadAll().filterNot { it.id == component.id }
        save(current + component)
    }

    @Synchronized
    fun markRemoved(id: String) {
        save(loadAll().filterNot { it.id == id })
    }

    private fun save(entries: List<InstalledComponent>) {
        registryFile.parentFile?.mkdirs()
        registryFile.writeText(json.encodeToString(InstalledComponentsFile(entries)))
    }
}
