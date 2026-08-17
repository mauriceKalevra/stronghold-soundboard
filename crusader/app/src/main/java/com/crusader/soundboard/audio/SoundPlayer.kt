package com.crusader.soundboard.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.crusader.soundboard.data.Sound
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class PlaybackState(
    val soundId: String? = null,
    val progress: Float = 0f,
    val isPlaying: Boolean = false
)

/** Spielt genau einen Sound zur Zeit, direkt aus den Assets. Pausieren behaelt die Stelle. */
class SoundPlayer(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var player: MediaPlayer? = null
    private var ticker: Job? = null

    private val _state = MutableStateFlow(PlaybackState())
    val state: StateFlow<PlaybackState> = _state.asStateFlow()

    /**
     * Tippen auf den geladenen Sound pausiert oder setzt fort, an derselben Stelle.
     * Tippen auf einen anderen Sound startet ihn neu von vorn.
     */
    fun toggle(sound: Sound) {
        val current = _state.value
        when {
            current.soundId != sound.id -> play(sound)
            current.isPlaying -> pause()
            else -> resume()
        }
    }

    fun play(sound: Sound) {
        stop()
        try {
            val descriptor = context.assets.openFd(sound.assetPath)
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            mediaPlayer.setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
            descriptor.close()
            mediaPlayer.setOnCompletionListener { stop() }
            mediaPlayer.setOnErrorListener { _, _, _ -> stop(); true }
            mediaPlayer.prepare()
            mediaPlayer.start()
            player = mediaPlayer
            _state.value = PlaybackState(sound.id, 0f, isPlaying = true)
            startTicker(sound.id)
        } catch (e: Exception) {
            _state.value = PlaybackState()
        }
    }

    /** Haelt den Sound an, ohne ihn freizugeben – die Stelle bleibt erhalten. */
    fun pause() {
        val active = player ?: return
        ticker?.cancel()
        ticker = null
        val progress = runCatching { active.progressFraction() }.getOrNull()
        runCatching { active.pause() }
        _state.value = _state.value.copy(progress = progress ?: _state.value.progress, isPlaying = false)
    }

    /** Spielt den pausierten Sound ab derselben Stelle weiter. */
    fun resume() {
        val active = player ?: return
        val soundId = _state.value.soundId ?: return
        runCatching { active.start() }
        _state.value = _state.value.copy(isPlaying = true)
        startTicker(soundId)
    }

    private fun startTicker(soundId: String) {
        ticker?.cancel()
        ticker = scope.launch {
            while (isActive) {
                val active = player ?: break
                val progress = try {
                    active.progressFraction()
                } catch (e: Exception) {
                    null
                }
                if (progress == null) break
                _state.value = PlaybackState(soundId, progress, isPlaying = true)
                delay(40)
            }
        }
    }

    private fun MediaPlayer.progressFraction(): Float {
        val duration = duration.coerceAtLeast(1)
        return (currentPosition.toFloat() / duration).coerceIn(0f, 1f)
    }

    /** Springt an die gegebene Stelle (0f..1f) im geladenen Sound, egal ob gerade pausiert. */
    fun seekTo(fraction: Float) {
        val active = player ?: return
        val clamped = fraction.coerceIn(0f, 1f)
        val duration = active.duration.coerceAtLeast(1)
        active.seekTo((clamped * duration).toInt())
        _state.value = _state.value.copy(progress = clamped)
    }

    /** Stoppt vollstaendig und gibt den Player frei – die Stelle geht verloren. */
    fun stop() {
        ticker?.cancel()
        ticker = null
        player?.let { active ->
            runCatching { if (active.isPlaying) active.stop() }
            active.release()
        }
        player = null
        _state.value = PlaybackState()
    }

    fun release() {
        stop()
        scope.cancel()
    }
}
