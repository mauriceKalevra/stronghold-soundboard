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
    val progress: Float = 0f
)

/** Spielt genau einen Sound zur Zeit, direkt aus den Assets. */
class SoundPlayer(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var player: MediaPlayer? = null
    private var ticker: Job? = null

    private val _state = MutableStateFlow(PlaybackState())
    val state: StateFlow<PlaybackState> = _state.asStateFlow()

    /** Erneutes Tippen auf den laufenden Sound stoppt ihn. */
    fun toggle(sound: Sound) {
        if (_state.value.soundId == sound.id) stop() else play(sound)
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
            _state.value = PlaybackState(sound.id, 0f)
            startTicker(sound.id)
        } catch (e: Exception) {
            _state.value = PlaybackState()
        }
    }

    private fun startTicker(soundId: String) {
        ticker?.cancel()
        ticker = scope.launch {
            while (isActive) {
                val active = player ?: break
                val progress = try {
                    val duration = active.duration.coerceAtLeast(1)
                    (active.currentPosition.toFloat() / duration).coerceIn(0f, 1f)
                } catch (e: Exception) {
                    null
                }
                if (progress == null) break
                _state.value = PlaybackState(soundId, progress)
                delay(40)
            }
        }
    }
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
