package com.crusader.soundboard.audio

import android.content.Context
import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Hintergrundmusik auf dem Startbildschirm: spielt eine feste Zeit und blendet dann aus.
 * Erwartet eine Datei in assets/music/, deren Name mit "home" beginnt, z. B. home.mp3.
 */
class AmbientMusic(private val context: Context) {

    private var player: MediaPlayer? = null
    private var job: Job? = null
    private var alreadyPlayed = false

    fun playOnce(
        scope: CoroutineScope,
        playMillis: Long = 20_000L,
        fadeMillis: Long = 3_000L
    ) {
        if (alreadyPlayed || player != null) return
        val path = findTrack() ?: return
        alreadyPlayed = true

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
            return
        }

        job = scope.launch(Dispatchers.Main) {
            delay(playMillis)
            val active = player ?: return@launch
            val steps = 30
            for (step in steps downTo 0) {
                val level = VOLUME * step / steps
                runCatching { active.setVolume(level, level) }
                delay(fadeMillis / steps)
            }
            release()
        }
    }

    fun release() {
        job?.cancel()
        job = null
        player?.let { active ->
            runCatching { if (active.isPlaying) active.stop() }
            active.release()
        }
        player = null
    }

    private fun findTrack(): String? {
        val files = runCatching { context.assets.list("music") }.getOrNull() ?: return null
        return files
            .filter { it.endsWith(".mp3", true) || it.endsWith(".ogg", true) || it.endsWith(".m4a", true) }
            .firstOrNull { it.startsWith("home", ignoreCase = true) }
            ?.let { "music/$it" }
    }

    private companion object {
        const val VOLUME = 0.55f
    }
}