package com.crusader.soundboard.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer

/** Kurzer Klickton, wenn eine Kachel (Kategorie, Gruppe, ...) angetippt wird. */
fun playTileClickSound(context: Context) {
    try {
        val descriptor = context.assets.openFd("sounds/_unsorted/fx/swhit11.ogg")
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        mediaPlayer.setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
        descriptor.close()
        mediaPlayer.setOnCompletionListener { it.release() }
        mediaPlayer.setOnErrorListener { mp, _, _ -> mp.release(); true }
        mediaPlayer.prepare()
        mediaPlayer.start()
    } catch (e: Exception) {
        // Kein Klickton, wenn die Datei fehlt oder die Wiedergabe fehlschlaegt.
    }
}
