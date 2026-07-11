package com.krisoft.aide.sdkmanager

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.security.MessageDigest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.commons.compress.archivers.ar.ArArchiveInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream

/**
 * Download, verifikasi checksum, ekstraksi, lalu pasang satu [SdkComponent]
 * ke direktori instalasi milik aplikasi.
 *
 * Prinsip desain: setiap kegagalan (jaringan putus, checksum tidak cocok, arsip
 * korup) harus bisa di-retry lewat pemanggilan ulang [install] dari awal -
 * tidak ada state yang membuat komponen "macet" tanpa jalan keluar.
 */
class SdkInstaller(
    private val sdkRootDir: File,
    private val registry: InstalledSdkRegistry,
    private val httpClient: OkHttpClient = OkHttpClient(),
) {

    suspend fun install(
        component: SdkComponent,
        onProgress: (InstallProgress) -> Unit,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        if (component.installMethod == InstallMethod.VIA_SDKMANAGER) {
            val message = "${component.displayName} dipasang lewat sdkmanager resmi via Terminal " +
                "(belum tersedia - lihat Phase 3 di docs/ROADMAP.md), bukan lewat AIDE langsung."
            onProgress(InstallProgress.Failed(message))
            return@withContext Result.failure(UnsupportedOperationException(message))
        }

        val workDir = File(sdkRootDir, "tmp/${component.id}-${System.currentTimeMillis()}")
        val downloadFile = File(workDir, component.downloadUrl.substringAfterLast('/'))
        val finalDir = installDirFor(component)

        try {
            workDir.mkdirs()

            onProgress(InstallProgress.Downloading(0, component.sizeBytes))
            downloadTo(component, downloadFile, onProgress)

            onProgress(InstallProgress.Verifying)
            val actualSha256 = sha256Of(downloadFile)
            if (!actualSha256.equals(component.sha256, ignoreCase = true)) {
                return@withContext Result.failure(
                    IllegalStateException(
                        "Checksum tidak cocok (dapat: $actualSha256, diharapkan: ${component.sha256}). " +
                            "File kemungkinan rusak saat diunduh - coba lagi."
                    )
                )
            }

            onProgress(InstallProgress.Extracting)
            val extractDir = File(workDir, "extracted")
            extractArchive(downloadFile, extractDir, component.stripTopLevelDir)

            finalDir.parentFile?.mkdirs()
            if (finalDir.exists()) finalDir.deleteRecursively()
            if (!extractDir.renameTo(finalDir)) {
                extractDir.copyRecursively(finalDir, overwrite = true)
            }

            registry.markInstalled(
                InstalledComponent(id = component.id, version = component.version, installPath = finalDir.absolutePath)
            )
            onProgress(InstallProgress.Installed)
            Result.success(Unit)
        } catch (e: Exception) {
            onProgress(InstallProgress.Failed(e.message ?: "Gagal memasang ${component.displayName}", e))
            Result.failure(e)
        } finally {
            workDir.deleteRecursively()
        }
    }

    fun uninstall(component: SdkComponent) {
        val installed = registry.get(component.id) ?: return
        File(installed.installPath).deleteRecursively()
        registry.markRemoved(component.id)
    }

    fun isInstalled(component: SdkComponent): Boolean {
        val installed = registry.get(component.id) ?: return false
        return installed.version == component.version && File(installed.installPath).exists()
    }

    fun installDirFor(component: SdkComponent): File =
        File(sdkRootDir, "${component.type.name.lowercase()}/${component.id}")

    private fun downloadTo(component: SdkComponent, target: File, onProgress: (InstallProgress) -> Unit) {
        target.parentFile?.mkdirs()
        val request = Request.Builder().url(component.downloadUrl).build()
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw java.io.IOException("Download gagal: HTTP ${response.code}")
            }
            val body = response.body ?: throw java.io.IOException("Respons download kosong")
            val total = body.contentLength().takeIf { it > 0 } ?: component.sizeBytes
            var downloaded = 0L

            body.byteStream().use { input ->
                FileOutputStream(target).use { output ->
                    val buffer = ByteArray(64 * 1024)
                    while (true) {
                        val read = input.read(buffer)
                        if (read == -1) break
                        output.write(buffer, 0, read)
                        downloaded += read
                        onProgress(InstallProgress.Downloading(downloaded, total))
                    }
                }
            }
        }
    }

    private fun sha256Of(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(64 * 1024)
            while (true) {
                val read = input.read(buffer)
                if (read == -1) break
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    private fun extractArchive(archiveFile: File, destDir: File, stripTopLevelDir: Boolean) {
        destDir.mkdirs()

        if (archiveFile.name.endsWith(".deb")) {
            extractDebPackage(archiveFile, destDir, stripTopLevelDir)
            return
        }

        val rawStream = archiveFile.inputStream().buffered()
        val decompressed = decompressorFor(archiveFile.name, rawStream)
        extractTar(decompressed, destDir, stripTopLevelDir)
    }

    /**
     * Buka file .deb (arsip `ar` berisi debian-binary, control.tar.xz, data.tar.xz),
     * lalu ekstrak isi payload `data.tar.xz`-nya saja - lihat docs/PACKAGE_REPO.md §2.
     * Repo paket AIDE sendiri selalu memakai kompresi .xz (bukan .zst/.gz) supaya
     * tidak butuh dependency tambahan di sini.
     */
    private fun extractDebPackage(debFile: File, destDir: File, stripTopLevelDir: Boolean) {
        ArArchiveInputStream(debFile.inputStream().buffered()).use { arStream ->
            var entry = arStream.nextEntry
            while (entry != null) {
                if (entry.name.startsWith("data.tar")) {
                    val decompressed = decompressorFor(entry.name, arStream)
                    extractTar(decompressed, destDir, stripTopLevelDir)
                    return
                }
                entry = arStream.nextEntry
            }
        }
        throw IllegalArgumentException("Entry data.tar.* tidak ditemukan di dalam ${debFile.name}")
    }

    private fun decompressorFor(fileName: String, rawStream: InputStream): InputStream = when {
        fileName.endsWith(".tar.xz") -> XZCompressorInputStream(rawStream)
        fileName.endsWith(".tar.gz") || fileName.endsWith(".tgz") -> GzipCompressorInputStream(rawStream)
        else -> throw IllegalArgumentException("Format arsip tidak didukung: $fileName")
    }

    private fun extractTar(tarInput: InputStream, destDir: File, stripTopLevelDir: Boolean) {
        TarArchiveInputStream(tarInput).use { tarStream ->
            var entry: TarArchiveEntry? = tarStream.nextEntry as TarArchiveEntry?
            while (entry != null) {
                val entryPath = stripFirstPathSegment(entry.name, stripTopLevelDir)
                if (entryPath.isNotEmpty()) {
                    val outFile = File(destDir, entryPath)
                    if (!outFile.canonicalPath.startsWith(destDir.canonicalPath)) {
                        throw SecurityException("Entry arsip mencoba keluar dari direktori tujuan: ${entry.name}")
                    }
                    if (entry.isDirectory) {
                        outFile.mkdirs()
                    } else {
                        outFile.parentFile?.mkdirs()
                        FileOutputStream(outFile).use { out -> tarStream.copyTo(out) }
                        if (entry.mode and 0b001_000_000 != 0) {
                            outFile.setExecutable(true)
                        }
                    }
                }
                entry = tarStream.nextEntry as TarArchiveEntry?
            }
        }
    }

    private fun stripFirstPathSegment(path: String, strip: Boolean): String {
        if (!strip) return path
        val idx = path.indexOf('/')
        return if (idx == -1) "" else path.substring(idx + 1)
    }
}
