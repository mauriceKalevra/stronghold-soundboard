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

    fun load(context: Context): Catalog {
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
                val groupName = groupJson.getString("name")
                val directory = "sounds/$categoryId/$groupId"

                val sounds = listAudioFiles(context, directory).map { fileName ->
                    Sound(
                        id = "$directory/$fileName",
                        file = fileName,
                        assetPath = "$directory/$fileName",
                        label = labelFor(fileName),
                        groupId = groupId,
                        groupName = groupName,
                        categoryId = categoryId
                    )
                }

                groups.add(
                    SoundGroup(
                        id = groupId,
                        name = groupName,
                        role = groupJson.optString("role", ""),
                        side = groupJson.optString("side", ""),
                        categoryId = categoryId,
                        sounds = sounds
                    )
                )
            }

            categories.add(
                Category(
                    id = categoryId,
                    title = categoryJson.getString("title"),
                    subtitle = categoryJson.optString("subtitle", ""),
                    icon = categoryJson.optString("icon", "shield"),
                    groups = groups
                )
            )
        }
        return Catalog(categories)
    }

    private fun listAudioFiles(context: Context, directory: String): List<String> =
        runCatching { context.assets.list(directory) }
            .getOrNull()
            ?.filter { name -> AUDIO_EXTENSIONS.any { name.endsWith(it, ignoreCase = true) } }
            ?.sorted()
            ?: emptyList()

    /** Aus "ri_anger_02.mp3" wird "Wut". */
    private fun labelFor(fileName: String): String {
        val base = fileName.substringBeforeLast('.')
        val parts = base.split('_', '-').filter { it.isNotBlank() }
        val kind = when {
            parts.size >= 2 -> parts[1]
            parts.isNotEmpty() -> parts[0]
            else -> base
        }.lowercase().trimEnd('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

        return KIND_LABELS[kind] ?: kind.replaceFirstChar { it.uppercaseChar() }
    }
}
