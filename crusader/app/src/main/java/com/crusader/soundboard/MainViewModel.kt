package com.crusader.soundboard

import android.app.Application
import android.media.MediaMetadataRetriever
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.crusader.soundboard.audio.IntroMusic
import com.crusader.soundboard.audio.PlaybackState
import com.crusader.soundboard.audio.SoundPlayer
import com.crusader.soundboard.data.Catalog
import com.crusader.soundboard.data.CatalogRepository
import com.crusader.soundboard.data.FavoritesStore
import com.crusader.soundboard.data.Sound
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.crusader.soundboard.audio.AmbientMusic

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val favoritesStore = FavoritesStore(application)
    private val player = SoundPlayer(application)
    private val intro = IntroMusic(application)
    private val ambient = AmbientMusic(application)

    val catalog: Catalog = CatalogRepository.load(application)

    val playback: StateFlow<PlaybackState> = player.state

    private val _favorites = MutableStateFlow(favoritesStore.load())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    /** Laufzeiten werden erst gelesen, wenn eine Kachel sichtbar wird. */
    val durations = mutableStateMapOf<String, String>()

    fun play(sound: Sound) = player.toggle(sound)

    fun stopPlayback() = player.stop()

    fun toggleFavorite(id: String) {
        val next = _favorites.value.toMutableSet()
        if (!next.add(id)) next.remove(id)
        _favorites.value = next
        favoritesStore.save(next)
    }

    fun favoriteSounds(ids: Set<String>): List<Sound> =
        catalog.allSounds.filter { ids.contains(it.id) }

    fun startIntro() = intro.start()

    fun stopIntro() = intro.fadeOut(viewModelScope)
    /** Läuft einmal pro App-Start auf dem Startbildschirm. */
    fun startHomeAmbient() = ambient.playOnce(viewModelScope)


    fun requestDuration(sound: Sound) {
        if (durations.containsKey(sound.id)) return
        durations[sound.id] = ""
        viewModelScope.launch {
            val text = withContext(Dispatchers.IO) { readDuration(sound) }
            durations[sound.id] = text
        }
    }

    private fun readDuration(sound: Sound): String = try {
        val descriptor = getApplication<Application>().assets.openFd(sound.assetPath)
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
        val millis = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
        retriever.release()
        descriptor.close()
        val seconds = (millis / 1000L).toInt()
        String.format("%d:%02d", seconds / 60, seconds % 60)
    } catch (e: Exception) {
        ""
    }

    override fun onCleared() {
        player.release()
        intro.release()
        ambient.release()
        super.onCleared()
    }
}
