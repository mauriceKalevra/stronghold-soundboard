package com.crusader.soundboard.data

import android.content.Context

/** Merkt sich die gewaehlte Sprache. */
class SettingsStore(context: Context) {

    private val prefs = context.getSharedPreferences("soundboard", Context.MODE_PRIVATE)

    fun language(): Lang =
        if (prefs.getString(KEY, Lang.DE.code) == Lang.EN.code) Lang.EN else Lang.DE

    fun saveLanguage(lang: Lang) {
        prefs.edit().putString(KEY, lang.code).apply()
    }

    private companion object {
        const val KEY = "language"
    }
}
