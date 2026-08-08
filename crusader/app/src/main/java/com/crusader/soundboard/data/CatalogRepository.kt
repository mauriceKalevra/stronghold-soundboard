package com.crusader.soundboard.data

import android.content.Context
import org.json.JSONObject

/**
 * Liest die Struktur aus assets/catalog.json und sucht die Sounds
 * anschliessend selbst in assets/sounds/<kategorie>/<gruppe>/.
 *
 * Neue Sounds hinzufuegen heisst also: Datei in den passenden Ordner legen,
 * neu bauen, fertig. Die JSON-Datei muss nur angefasst werden, wenn eine
 * neue Kategorie oder Gruppe dazukommt.
 */
object CatalogRepository {

    private val KIND_LABELS_EN = mapOf(
        "anger" to "Anger",
        "angry" to "Anger",
        "congrats" to "Congratulations",
        "taunt" to "Taunt",
        "greet" to "Greeting",
        "hello" to "Greeting",
        "attack" to "Attack",
        "defeat" to "Defeat",
        "peace" to "Peace",
        "victory" to "Victory",
        "intro" to "Intro",
        "mission" to "Mission",
        "hint" to "Hint",
        "cheer" to "Cheer",
        "boo" to "Booing",
        "work" to "Work",
        "hunger" to "Hunger",
        "buy" to "Buying",
        "sell" to "Selling",
        "trade" to "Trade",
        "select" to "Select",
        "move" to "Move",
        "death" to "Death",
        "fire" to "Fire",
        "load" to "Load",
        "hit" to "Hit",
        "theme" to "Theme",
        "siege" to "Siege",
        "oasis" to "Oasis",
        "wind" to "Desert wind"
    )

    private val KIND_LABELS = mapOf(
        "anger" to "Wut",
        "angry" to "Wut",
        "congrats" to "Glückwunsch",
        "taunt" to "Spott",
        "greet" to "Begrüßung",
        "hello" to "Begrüßung",
        "attack" to "Angriff",
        "defeat" to "Niederlage",
        "peace" to "Frieden",
        "victory" to "Sieg",
        "intro" to "Einleitung",
        "mission" to "Auftrag",
        "hint" to "Hinweis",
        "cheer" to "Jubel",
        "boo" to "Unmut",
        "work" to "Arbeit",
        "hunger" to "Hunger",
        "buy" to "Kauf",
        "sell" to "Verkauf",
        "trade" to "Handel",
        "select" to "Auswahl",
        "move" to "Marsch",
        "death" to "Tod",
        "fire" to "Schuss",
        "load" to "Laden",
        "hit" to "Treffer",
        "theme" to "Thema",
        "siege" to "Belagerung",
        "oasis" to "Oase",
        "wind" to "Wüstenwind"
    )

    private val AUDIO_EXTENSIONS = listOf(".mp3", ".ogg", ".wav", ".m4a")

    fun load(context: Context, lang: Lang = Lang.DE): Catalog {
        val raw = context.assets.open("catalog.json").bufferedReader().use { it.readText() }
        val root = JSONObject(raw)
        val categoriesJson = root.getJSONArray("categories")
        val categories = ArrayList<Category>(categoriesJson.length())

        for (i in 0 until categoriesJson.length()) {
            val categoryJson = categoriesJson.getJSONObject(i)
            val categoryId = categoryJson.getString("id")
            val groupsJson = categoryJson.getJSONArray("groups")
            val groups = ArrayList<SoundGroup>(groupsJson.length())

            for (j in 0 until groupsJson.length()) {
                val groupJson = groupsJson.getJSONObject(j)
                val groupId = groupJson.getString("id")
                val groupName = pick(groupJson, "name", lang)
                val directory = soundDirectory(context, categoryId, groupId, lang)

                val sounds = listAudioFiles(context, directory).map { fileName ->
                    Sound(
                        // Die Kennung bleibt ohne Sprachordner, damit Favoriten
                        // einen Sprachwechsel ueberstehen.
                        id = "sounds/$categoryId/$groupId/$fileName",
                        file = fileName,
                        assetPath = "$directory/$fileName",
                        label = labelFor(fileName, lang),
                        groupId = groupId,
                        groupName = groupName,
                        categoryId = categoryId
                    )
                }

                groups.add(
                    SoundGroup(
                        id = groupId,
                        name = groupName,
                        role = pick(groupJson, "role", lang),
                        side = groupJson.optString("side", ""),
                        categoryId = categoryId,
                        sounds = sounds
                    )
                )
            }

            categories.add(
                Category(
                    id = categoryId,
                    title = pick(categoryJson, "title", lang),
                    subtitle = pick(categoryJson, "subtitle", lang),
                    icon = categoryJson.optString("icon", "shield"),
                    groups = groups
                )
            )
        }
        return Catalog(categories)
    }

    /**
     * Englisch nimmt "name" + "En", also nameEn/titleEn/subtitleEn/roleEn.
     * Fehlt das Feld, bleibt der deutsche Text stehen.
     */
    private fun pick(json: JSONObject, key: String, lang: Lang): String {
        val base = json.optString(key, "")
        if (lang == Lang.DE) return base
        val translated = json.optString(key + "En", "")
        return if (translated.isBlank()) base else translated
    }

    /**
     * Sucht zuerst den Sprachordner (z. B. sounds/characters/richard/en).
     * Ist er leer oder fehlt er, werden die Dateien direkt im Gruppenordner genommen.
     */
    private fun soundDirectory(context: Context, categoryId: String, groupId: String, lang: Lang): String {
        val base = "sounds/$categoryId/$groupId"
        val localised = "$base/${lang.code}"
        return if (listAudioFiles(context, localised).isNotEmpty()) localised else base
    }

    private fun listAudioFiles(context: Context, directory: String): List<String> =
        runCatching { context.assets.list(directory) }
            .getOrNull()
            ?.filter { name -> AUDIO_EXTENSIONS.any { name.endsWith(it, ignoreCase = true) } }
            ?.sorted()
            ?: emptyList()

    /** Aus "ri_anger_02.mp3" wird "Wut" bzw. "Anger". */
    private fun labelFor(fileName: String, lang: Lang): String {
        val base = fileName.substringBeforeLast('.')
        val parts = base.split('_', '-').filter { it.isNotBlank() }
        val kind = when {
            parts.size >= 2 -> parts[1]
            parts.isNotEmpty() -> parts[0]
            else -> base
        }.lowercase().trimEnd('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

        val labels = if (lang == Lang.EN) KIND_LABELS_EN else KIND_LABELS
        return labels[kind] ?: kind.replaceFirstChar { it.uppercaseChar() }
    }
}
