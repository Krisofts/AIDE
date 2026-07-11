package com.krisoft.aide.sdkmanager

import android.content.Context
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Sumber daftar komponen SDK yang tersedia untuk diinstal.
 *
 * Belum ada endpoint manifest resmi milik proyek ini (lihat catatan di
 * docs/MASTER_PLAN.md - arsitektur eksekusi JDK/build-tools di Android belum
 * diputuskan). Selama itu belum final, [loadBundledSampleManifest] dipakai
 * sebagai sumber data untuk pengembangan & pengujian UI, BUKAN sumber
 * produksi - URL di dalamnya hanyalah contoh format, belum tentu valid.
 */
class SdkComponentRepository(
    private val context: Context,
    private val httpClient: OkHttpClient = OkHttpClient(),
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun fetchManifest(manifestUrl: String): Result<SdkManifest> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(manifestUrl).build()
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(
                        IOException("Gagal mengambil manifest: HTTP ${response.code}")
                    )
                }
                val body = response.body?.string()
                    ?: return@withContext Result.failure(IOException("Manifest kosong"))
                Result.success(json.decodeFromString<SdkManifest>(body))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loadBundledSampleManifest(): Result<SdkManifest> = withContext(Dispatchers.IO) {
        try {
            val text = context.assets.open("sdk_manifest_sample.json")
                .bufferedReader()
                .use { it.readText() }
            Result.success(json.decodeFromString<SdkManifest>(text))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
