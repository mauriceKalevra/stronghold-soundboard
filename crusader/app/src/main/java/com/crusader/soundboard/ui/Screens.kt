package com.crusader.soundboard.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cottage
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.crusader.soundboard.MainViewModel
import com.crusader.soundboard.data.Category
import com.crusader.soundboard.data.Sound
import com.crusader.soundboard.data.SoundGroup
import com.crusader.soundboard.util.SoundActions
import kotlinx.coroutines.delay

private const val LOAD_MILLIS = 7000L

private val LOADING_LINES = listOf(
    "Karawane wird beladen …",
    "Steine werden geschlagen …",
    "Wüstenwind zieht auf …",
    "Tore öffnen sich …"
)

@Composable
fun SoundboardApp(viewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(viewModel) {
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }

        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onCategory = { category ->
                    val single = category.groups.singleOrNull()
                    if (single != null) {
                        navController.navigate("group/${category.id}/${single.id}")
                    } else {
                        navController.navigate("category/${category.id}")
                    }
                },
                onFavorites = { navController.navigate("favorites") },
                onReplayIntro = {
                    navController.navigate("splash") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("category/{categoryId}") { entry ->
            val categoryId = entry.arguments?.getString("categoryId").orEmpty()
            val category = viewModel.catalog.category(categoryId)
            if (category == null) {
                LaunchedEffect(Unit) { navController.popBackStack() }
            } else {
                GroupListScreen(
                    viewModel = viewModel,
                    category = category,
                    onGroup = { group -> navController.navigate("group/${category.id}/${group.id}") },
                    onBack = { navController.popBackStack() },
                    onHome = { backToHome(navController) },
                    onFavorites = { navController.navigate("favorites") }
                )
            }
        }

        composable("group/{categoryId}/{groupId}") { entry ->
            val categoryId = entry.arguments?.getString("categoryId").orEmpty()
            val groupId = entry.arguments?.getString("groupId").orEmpty()
            val group = viewModel.catalog.group(categoryId, groupId)
            if (group == null) {
                LaunchedEffect(Unit) { navController.popBackStack() }
            } else {
                SoundListScreen(
                    viewModel = viewModel,
                    group = group,
                    onBack = { navController.popBackStack() },
                    onHome = { backToHome(navController) },
                    onFavorites = { navController.navigate("favorites") }
                )
            }
        }

        composable("favorites") {
            FavoritesScreen(
                viewModel = viewModel,
                onHome = { backToHome(navController) }
            )
        }
    }
}

private fun backToHome(navController: NavHostController) {
    navController.navigate("home") {
        popUpTo("home") { inclusive = true }
    }
}

/* ---------------------------------------------------------------- Splash */


@Composable
private fun SplashScreen(viewModel: MainViewModel, onFinished: () -> Unit) {
    var progress by remember { mutableFloatStateOf(0f) }
    var lineIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.startIntro()
        val start = System.currentTimeMillis()
        while (true) {
            val elapsed = System.currentTimeMillis() - start
            val value = (elapsed.toFloat() / LOAD_MILLIS).coerceIn(0f, 1f)
            progress = value
            lineIndex = (value * LOADING_LINES.size).toInt().coerceAtMost(LOADING_LINES.lastIndex)
            if (value >= 1f) break
            delay(16)
        }
        delay(260)
        viewModel.stopIntro()
        onFinished()
    }

    DesertScaffold(scrim = SplashScrim) {
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "STRONGHOLD SOUNDBOARD",
                    style = Type.Body,
                    color = Palette.Parchment.copy(alpha = 0.65f)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Maurice Kalevra",
                    style = Type.Body,
                    color = Palette.Parchment.copy(alpha = 0.62f)
                )
            }
            Column(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(start = 28.dp, end = 28.dp, bottom = 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = LOADING_LINES[lineIndex],
                    style = Type.Meta,
                    color = Palette.Parchment.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(14.dp))
                LoadingBar(progress)
            }
        }
    }
}

@Composable
private fun LoadingBar(progress: Float) {
    val shape = RoundedCornerShape(2.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .height(17.dp)
            .background(Palette.BarWell, shape)
            .border(1.dp, Palette.BarFrame, shape)
            .padding(2.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth(progress.coerceIn(0.001f, 1f))
                .fillMaxHeight()
                .background(EmberGradient, RoundedCornerShape(1.dp))
        )
    }
}

/* ------------------------------------------------------------------ Home */

@Composable
private fun HomeScreen(
    viewModel: MainViewModel,
    onCategory: (Category) -> Unit,
    onFavorites: () -> Unit,
    onReplayIntro: () -> Unit
) {
    val favorites by viewModel.favorites.collectAsState()
    val catalog = viewModel.catalog
    LaunchedEffect(Unit) { viewModel.startHomeAmbient() }

    DesertScaffold(
        topBar = {
            TopBanner(
                title = "Crusader Soundboard",
                subtitle = "${catalog.soundCount} Sounds im Archiv",
                action = {
                    IconButton(onClick = onReplayIntro) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Intro erneut abspielen", tint = Palette.Ink)
                    }
                }
            )
        },
        bottomBar = { BottomTabs("home") { if (it == "fav") onFavorites() } }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 16.dp, bottom = 24.dp)
        ) {
            item { SectionLabel("Kategorien") }

            items(catalog.categories, key = { it.id }) { category ->
                StoneTile(onClick = { onCategory(category) }) {
                    TileGlyph(iconFor(category.icon))
                    Spacer(Modifier.width(13.dp))
                    Column(Modifier.weight(1f)) {
                        Text(category.title, style = Type.TileTitle, color = Palette.Parchment)
                        Text(
                            "${category.soundCount} Sounds · ${category.subtitle}",
                            style = Type.Meta,
                            color = Palette.InkDim,
                            modifier = Modifier.padding(top = 3.dp)
                        )
                    }
                }
            }

            item {
                StoneTile(onClick = onFavorites) {
                    TileGlyph(Icons.Filled.Star)
                    Spacer(Modifier.width(13.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Favoriten", style = Type.TileTitle, color = Palette.Parchment)
                        Text(
                            "${favorites.size} gemerkt",
                            style = Type.Meta,
                            color = Palette.InkDim,
                            modifier = Modifier.padding(top = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun iconFor(name: String): ImageVector = when (name) {
    "scroll" -> Icons.Filled.MenuBook
    "house" -> Icons.Filled.Cottage
    "sword" -> Icons.Filled.Gavel
    "note" -> Icons.Filled.MusicNote
    "shield" -> Icons.Filled.Shield
    else -> Icons.Filled.GraphicEq
}

/* ------------------------------------------------------------ Gruppenliste */

@Composable
private fun GroupListScreen(
    viewModel: MainViewModel,
    category: Category,
    onGroup: (SoundGroup) -> Unit,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onFavorites: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val visible = category.groups.filter { it.name.contains(query, ignoreCase = true) }

    DesertScaffold(
        topBar = { TopBanner(title = category.title, subtitle = category.subtitle, onBack = onBack) },
        bottomBar = { BottomTabs("home") { if (it == "fav") onFavorites() else onHome() } }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 16.dp, bottom = 24.dp)
        ) {
            item { SearchField(query, { query = it }, "Name suchen") }
            item { SectionLabel("${category.groups.size} Einträge") }

            if (visible.isEmpty()) {
                item {
                    EmptyState(
                        "Nichts gefunden",
                        "Für „$query“ gibt es hier keinen Eintrag. Suchbegriff kürzen oder Kategorie wechseln."
                    )
                }
            }

            items(visible, key = { it.id }) { group ->
                StoneTile(onClick = { onGroup(group) }) {
                    val sideColor = when (group.side) {
                        "sarazenen" -> Palette.Oasis
                        "kreuzfahrer" -> Palette.Blood
                        else -> null
                    }
                    if (sideColor != null) {
                        Box(Modifier.width(4.dp).height(34.dp).background(sideColor))
                    } else {
                        TileGlyph(iconFor(category.icon))
                    }
                    Spacer(Modifier.width(13.dp))
                    Column(Modifier.weight(1f)) {
                        Text(group.name, style = Type.TileTitle, color = Palette.Parchment)
                        Text(
                            listOfNotNull(
                                "${group.sounds.size} Sounds",
                                group.role.takeIf { it.isNotBlank() }
                            ).joinToString(" · "),
                            style = Type.Meta,
                            color = Palette.InkDim,
                            modifier = Modifier.padding(top = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

/* -------------------------------------------------------------- Soundliste */

@Composable
private fun SoundListScreen(
    viewModel: MainViewModel,
    group: SoundGroup,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onFavorites: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val visible = group.sounds.filter {
        it.file.contains(query, ignoreCase = true) || it.label.contains(query, ignoreCase = true)
    }
    val sideColor = when (group.side) {
        "sarazenen" -> Palette.Oasis
        "kreuzfahrer" -> Palette.Blood
        else -> null
    }

    DesertScaffold(
        topBar = {
            TopBanner(
                title = group.name,
                subtitle = group.role.ifBlank { "${group.sounds.size} Sounds" },
                onBack = onBack,
                sideColor = sideColor
            )
        },
        bottomBar = { BottomTabs("home") { if (it == "fav") onFavorites() else onHome() } }
    ) {
        SoundList(
            viewModel = viewModel,
            sounds = visible,
            showGroupName = false,
            header = {
                SearchField(query, { query = it }, "Dateiname oder Art")
                SectionLabel("${group.sounds.size} Sounds")
            },
            emptyContent = {
                if (group.sounds.isEmpty()) {
                    EmptyState(
                        "Noch keine Dateien",
                        "Lege deine Audiodateien unter assets/sounds/${group.categoryId}/${group.id}/ ab und baue die App neu."
                    )
                } else {
                    EmptyState("Nichts gefunden", "Kein Dateiname passt zu „$query“.")
                }
            }
        )
    }
}

/* --------------------------------------------------------------- Favoriten */

@Composable
private fun FavoritesScreen(viewModel: MainViewModel, onHome: () -> Unit) {
    val favorites by viewModel.favorites.collectAsState()
    val sounds = viewModel.favoriteSounds(favorites)

    DesertScaffold(
        topBar = { TopBanner(title = "Favoriten", subtitle = "Deine Auswahl", onBack = onHome) },
        bottomBar = { BottomTabs("fav") { if (it == "home") onHome() } }
    ) {
        SoundList(
            viewModel = viewModel,
            sounds = sounds,
            showGroupName = true,
            header = { SectionLabel("${sounds.size} gemerkt") },
            emptyContent = {
                EmptyState(
                    "Noch nichts gemerkt",
                    "Tippe bei einem Sound auf den Stern, dann liegt er hier – auch nach dem Schließen der App."
                )
            }
        )
    }
}

/* ------------------------------------------------------- gemeinsame Liste */

@Composable
private fun SoundList(
    viewModel: MainViewModel,
    sounds: List<Sound>,
    showGroupName: Boolean,
    header: @Composable () -> Unit,
    emptyContent: @Composable () -> Unit
) {
    val context = LocalContext.current
    val favorites by viewModel.favorites.collectAsState()
    val playback by viewModel.playback.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 16.dp, bottom = 24.dp)
    ) {
        item { Column { header() } }

        if (sounds.isEmpty()) {
            item { emptyContent() }
        }

        items(sounds, key = { it.id }) { sound ->
            LaunchedEffect(sound.id) { viewModel.requestDuration(sound) }
            SoundRow(
                sound = sound,
                isFavorite = favorites.contains(sound.id),
                isPlaying = playback.soundId == sound.id,
                progress = if (playback.soundId == sound.id) playback.progress else 0f,
                duration = viewModel.durations[sound.id].orEmpty(),
                showGroupName = showGroupName,
                onPlay = { viewModel.play(sound) },
                onToggleFavorite = {
                    viewModel.toggleFavorite(sound.id)
                    val added = !favorites.contains(sound.id)
                    Toast.makeText(
                        context,
                        if (added) "${sound.file} zu Favoriten hinzugefügt" else "${sound.file} aus Favoriten entfernt",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onDownload = {
                    val ok = SoundActions.saveToDownloads(context, sound)
                    Toast.makeText(
                        context,
                        if (ok) "Gespeichert unter Downloads/Crusader Soundboard" else "Speichern nicht möglich",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onShare = {
                    val ok = SoundActions.share(context, sound)
                    if (!ok) Toast.makeText(context, "Teilen nicht möglich", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}
