package com.crusader.soundboard.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.crusader.soundboard.data.Sound
import java.io.File

/** Herunterladen und Teilen einzelner Sounds. */
object SoundActions {

    private const val FOLDER = "Crusader Soundboard"

    /** Kopiert den Sound nach Downloads/Crusader Soundboard. */
    fun saveToDownloads(context: Context, sound: Sound): Boolean = try {
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, sound.file)
            put(MediaStore.Downloads.MIME_TYPE, mimeOf(sound.file))
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + FOLDER)
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
        if (uri == null) {
            false
        } else {
            resolver.openOutputStream(uri)?.use { output ->
                context.assets.open(sound.assetPath).use { input -> input.copyTo(output) }
            }
            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
            true
        }
    } catch (e: Exception) {
        false
    }

    /** Legt eine Kopie im Cache ab und oeffnet das Teilen-Menue von Android. */
    fun share(context: Context, sound: Sound, chooserTitle: String = "Sound teilen"): Boolean = try {
        val directory = File(context.cacheDir, "shared")
        if (!directory.exists()) directory.mkdirs()
        val file = File(directory, sound.file)
        context.assets.open(sound.assetPath).use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
        }
        val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeOf(sound.file)
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, sound.file)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, chooserTitle))
        true
    } catch (e: Exception) {
        false
    }

    private fun mimeOf(fileName: String): String = when {
        fileName.endsWith(".ogg", true) -> "audio/ogg"
        fileName.endsWith(".wav", true) -> "audio/wav"
        fileName.endsWith(".m4a", true) -> "audio/mp4"
        else -> "audio/mpeg"
    }
}
