package com.crusader.soundboard.audio

import android.content.Context
import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Musik fuer den Ladebildschirm. Sucht die erste Datei in assets/music/,
 * bevorzugt eine, die mit "intro" beginnt. Fehlt die Datei, bleibt es still.
 */
class IntroMusic(private val context: Context) {

    private var player: MediaPlayer? = null

    fun start() {
        if (player != null) return
        val path = findTrack() ?: return
        try {
            val descriptor = context.assets.openFd(path)
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
            descriptor.close()
            mediaPlayer.isLooping = true
            mediaPlayer.setVolume(VOLUME, VOLUME)
            mediaPlayer.prepare()
            mediaPlayer.start()
            player = mediaPlayer
        } catch (e: Exception) {
            player = null
        }
    }

    fun fadeOut(scope: CoroutineScope) {
        val active = player ?: return
        player = null
        scope.launch(Dispatchers.Main) {
            var volume = VOLUME
            while (volume > 0f) {
                volume -= 0.06f
                val level = volume.coerceAtLeast(0f)
                runCatching { active.setVolume(level, level) }
                delay(30)
            }
            runCatching { if (active.isPlaying) active.stop() }
            active.release()
        }
    }

    fun release() {
        player?.let { active ->
            runCatching { if (active.isPlaying) active.stop() }
            active.release()
        }
        player = null
    }

    private fun findTrack(): String? {
        val files = runCatching { context.assets.list("music") }.getOrNull() ?: return null
        val audio = files.filter {
            it.endsWith(".mp3", true) || it.endsWith(".ogg", true) || it.endsWith(".m4a", true)
        }
        val track = audio.firstOrNull { it.startsWith("intro", ignoreCase = true) } ?: audio.firstOrNull()
        return track?.let { "music/$it" }
    }

    private companion object {
        const val VOLUME = 0.7f
    }
}
