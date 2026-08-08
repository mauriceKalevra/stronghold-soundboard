package com.crusader.soundboard.data

/** Ein einzelner Sound, der als Asset in der App liegt. */
data class Sound(
    val id: String,
    val file: String,
    val assetPath: String,
    val label: String,
    val groupId: String,
    val groupName: String,
    val categoryId: String
)

/** Eine Gruppe, z. B. ein Charakter oder eine Einheit. */
data class SoundGroup(
    val id: String,
    val name: String,
    val role: String,
    val side: String,
    val categoryId: String,
    val sounds: List<Sound>
)

/** Eine Kachel auf dem Startbildschirm. */
data class Category(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: String,
    val groups: List<SoundGroup>
) {
    val soundCount: Int get() = groups.sumOf { it.sounds.size }
}

data class Catalog(val categories: List<Category>) {

    val allSounds: List<Sound> = categories.flatMap { category ->
        category.groups.flatMap { it.sounds }
    }

    private val byId: Map<String, Sound> = allSounds.associateBy { it.id }

    val soundCount: Int get() = allSounds.size

    fun sound(id: String): Sound? = byId[id]

    fun category(id: String): Category? = categories.firstOrNull { it.id == id }

    fun group(categoryId: String, groupId: String): SoundGroup? =
        category(categoryId)?.groups?.firstOrNull { it.id == groupId }
}
