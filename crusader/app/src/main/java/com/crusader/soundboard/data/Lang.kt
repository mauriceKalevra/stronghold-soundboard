package com.crusader.soundboard.data

enum class Lang(val code: String) {
    DE("de"),
    EN("en");

    fun other(): Lang = if (this == DE) EN else DE
}

/**
 * Alle Texte der Oberflaeche. Platzhalter %d und %s werden mit .format() gefuellt.
 * Neue Texte hier in beiden Sprachen ergaenzen.
 */
data class Strings(
    val appTitle: String,
    val archiveCount: String,
    val categories: String,
    val favorites: String,
    val favoritesSubtitle: String,
    val savedCount: String,
    val soundsCount: String,
    val entriesCount: String,
    val sounds: String,
    val searchName: String,
    val searchFile: String,
    val tabStart: String,
    val tabFavorites: String,
    val nothingFoundTitle: String,
    val nothingFoundGroups: String,
    val nothingFoundSounds: String,
    val noFilesTitle: String,
    val noFilesText: String,
    val noFavoritesTitle: String,
    val noFavoritesText: String,
    val loadingLines: List<String>,
    val replayIntro: String,
    val back: String,
    val play: String,
    val pause: String,
    val addFavorite: String,
    val removeFavorite: String,
    val download: String,
    val share: String,
    val shareChooser: String,
    val favoriteAdded: String,
    val favoriteRemoved: String,
    val downloadDone: String,
    val downloadFailed: String,
    val shareFailed: String,
    val languageGerman: String,
    val languageEnglish: String
)

private val GERMAN = Strings(
    appTitle = "Crusader Soundboard",
    archiveCount = "%d Sounds im Archiv",
    categories = "Kategorien",
    favorites = "Favoriten",
    favoritesSubtitle = "Deine Auswahl",
    savedCount = "%d gemerkt",
    soundsCount = "%d Sounds",
    entriesCount = "%d Einträge",
    sounds = "Sounds",
    searchName = "Name suchen",
    searchFile = "Dateiname oder Art",
    tabStart = "START",
    tabFavorites = "FAVORITEN",
    nothingFoundTitle = "Nichts gefunden",
    nothingFoundGroups = "Für „%s“ gibt es hier keinen Eintrag. Suchbegriff kürzen oder Kategorie wechseln.",
    nothingFoundSounds = "Kein Dateiname passt zu „%s“.",
    noFilesTitle = "Noch keine Dateien",
    noFilesText = "Lege deine Audiodateien unter %s ab und baue die App neu.",
    noFavoritesTitle = "Noch nichts gemerkt",
    noFavoritesText = "Tippe bei einem Sound auf den Stern, dann liegt er hier – auch nach dem Schließen der App.",
    loadingLines = listOf(
        "Karawane wird beladen …",
        "Steine werden geschlagen …",
        "Wüstenwind zieht auf …",
        "Tore öffnen sich …"
    ),
    replayIntro = "Intro erneut abspielen",
    back = "Zurück",
    play = "Abspielen",
    pause = "Pausieren",
    addFavorite = "Zu Favoriten hinzufügen",
    removeFavorite = "Aus Favoriten entfernen",
    download = "Herunterladen",
    share = "Teilen",
    shareChooser = "Sound teilen",
    favoriteAdded = "%s zu Favoriten hinzugefügt",
    favoriteRemoved = "%s aus Favoriten entfernt",
    downloadDone = "Gespeichert unter Downloads/Crusader Soundboard",
    downloadFailed = "Speichern nicht möglich",
    shareFailed = "Teilen nicht möglich",
    languageGerman = "Deutsch",
    languageEnglish = "Englisch"
)

private val ENGLISH = Strings(
    appTitle = "Crusader Soundboard",
    archiveCount = "%d sounds in the archive",
    categories = "Categories",
    favorites = "Favourites",
    favoritesSubtitle = "Your picks",
    savedCount = "%d saved",
    soundsCount = "%d sounds",
    entriesCount = "%d entries",
    sounds = "Sounds",
    searchName = "Search by name",
    searchFile = "File name or type",
    tabStart = "HOME",
    tabFavorites = "FAVOURITES",
    nothingFoundTitle = "Nothing found",
    nothingFoundGroups = "No entry matches “%s”. Try a shorter term or another category.",
    nothingFoundSounds = "No file name matches “%s”.",
    noFilesTitle = "No files yet",
    noFilesText = "Put your audio files into %s and rebuild the app.",
    noFavoritesTitle = "Nothing saved yet",
    noFavoritesText = "Tap the star on a sound and it will appear here – even after closing the app.",
    loadingLines = listOf(
        "Loading the caravan …",
        "Cutting the stones …",
        "Desert wind rising …",
        "Opening the gates …"
    ),
    replayIntro = "Play intro again",
    back = "Back",
    play = "Play",
    pause = "Pause",
    addFavorite = "Add to favourites",
    removeFavorite = "Remove from favourites",
    download = "Download",
    share = "Share",
    shareChooser = "Share sound",
    favoriteAdded = "%s added to favourites",
    favoriteRemoved = "%s removed from favourites",
    downloadDone = "Saved to Downloads/Crusader Soundboard",
    downloadFailed = "Could not save the file",
    shareFailed = "Could not share the file",
    languageGerman = "German",
    languageEnglish = "English"
)

fun stringsFor(lang: Lang): Strings = if (lang == Lang.EN) ENGLISH else GERMAN
