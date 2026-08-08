package com.crusader.soundboard.data

import android.content.Context

/** Favoriten liegen lokal in den SharedPreferences, ohne Konto und ohne Netz. */
class FavoritesStore(context: Context) {

    private val prefs = context.getSharedPreferences("soundboard", Context.MODE_PRIVATE)

    fun load(): Set<String> = prefs.getStringSet(KEY, emptySet())?.toSet() ?: emptySet()

    fun save(ids: Set<String>) {
        prefs.edit().putStringSet(KEY, ids).apply()
    }

    private companion object {
        const val KEY = "favorites"
    }
}
