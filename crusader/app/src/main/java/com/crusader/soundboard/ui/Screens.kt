package com.crusader.soundboard.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Shuffle
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.crusader.soundboard.MainViewModel
import com.crusader.soundboard.R
import com.crusader.soundboard.data.Category
import com.crusader.soundboard.data.Lang
import com.crusader.soundboard.data.Sound
import com.crusader.soundboard.data.SoundGroup
import com.crusader.soundboard.util.SoundActions
import kotlinx.coroutines.delay

/** Dauer des Ladebalkens in Millisekunden. */
private const val LOAD_MILLIS = 3000L

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
                },
                onRandomSound = { sound ->
                    navController.navigate("group/${sound.categoryId}/${sound.groupId}")
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
    val strings = viewModel.strings
    var progress by remember { mutableFloatStateOf(0f) }
    var lineIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.startIntro()
        val start = System.currentTimeMillis()
        while (true) {
            val elapsed = System.currentTimeMillis() - start
            val value = (elapsed.toFloat() / LOAD_MILLIS).coerceIn(0f, 1f)
            progress = value
            lineIndex = (value * strings.loadingLines.size).toInt()
                .coerceAtMost(strings.loadingLines.lastIndex)
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
                    style = Type.Meta.copy(fontSize = 16.sp),
                    color = Palette.Parchment.copy(alpha = 0.45f)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Maurice Kalevra",
                    style = Type.Meta.copy(fontSize = 16.sp),
                    color = Palette.Brass
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
                    text = strings.loadingLines[lineIndex],
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
    onReplayIntro: () -> Unit,
    onRandomSound: (Sound) -> Unit
) {
    val strings = viewModel.strings
    val favorites by viewModel.favorites.collectAsState()
    val catalog = viewModel.catalog
    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.startHomeAmbient() }

    DesertScaffold(
        topBar = {
            TopBanner(
                title = strings.appTitle,
                subtitle = strings.archiveCount.format(catalog.soundCount),
                action = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FlagButton(
                            flagRes = R.drawable.flag_de,
                            active = viewModel.language == Lang.DE,
                            contentDescription = strings.languageGerman,
                            onClick = { viewModel.switchLanguage(Lang.DE) }
                        )
                        Spacer(Modifier.width(6.dp))
                        FlagButton(
                            flagRes = R.drawable.flag_en,
                            active = viewModel.language == Lang.EN,
                            contentDescription = strings.languageEnglish,
                            onClick = { viewModel.switchLanguage(Lang.EN) }
                        )
                        IconButton(onClick = onReplayIntro) {
                            Icon(
                                Icons.Filled.Refresh,
                                contentDescription = strings.replayIntro,
                                tint = Palette.Ink
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomTabs("home", strings.tabStart, strings.tabFavorites) {
                if (it == "fav") onFavorites()
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 16.dp, bottom = 24.dp)
        ) {
            item { SectionLabel(strings.categories) }

            items(catalog.categories, key = { it.id }) { category ->
                StoneTile(onClick = { onCategory(category) }) {
                    TileGlyph(iconFor(category.icon))
                    Spacer(Modifier.width(13.dp))
                    Column(Modifier.weight(1f)) {
                        Text(category.title, style = Type.TileTitle, color = Palette.Parchment)
                        Text(
                            strings.soundsCount.format(category.soundCount) + " · " + category.subtitle,
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
                        Text(strings.favorites, style = Type.TileTitle, color = Palette.Parchment)
                        Text(
                            strings.savedCount.format(favorites.size),
                            style = Type.Meta,
                            color = Palette.InkDim,
                            modifier = Modifier.padding(top = 3.dp)
                        )
                    }
                }
            }

            item {
                StoneTile(onClick = {
                    val sound = catalog.allSounds.randomOrNull()
                    if (sound != null) {
                        viewModel.play(sound)
                        Toast.makeText(
                            context,
                            strings.randomSoundToast.format(sound.groupName, sound.label),
                            Toast.LENGTH_SHORT
                        ).show()
                        onRandomSound(sound)
                    }
                }) {
                    TileGlyph(Icons.Filled.Shuffle)
                    Spacer(Modifier.width(13.dp))
                    Column(Modifier.weight(1f)) {
                        Text(strings.randomSound, style = Type.TileTitle, color = Palette.Parchment)
                        Text(
                            strings.randomSoundSubtitle,
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

private fun iconFor(name: String) = when (name) {
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
    val strings = viewModel.strings
    var query by remember { mutableStateOf("") }
    val visible = category.groups.filter { it.name.contains(query, ignoreCase = true) }

    DesertScaffold(
        topBar = { TopBanner(title = category.title, subtitle = category.subtitle, onBack = onBack) },
        bottomBar = {
            BottomTabs("home", strings.tabStart, strings.tabFavorites) {
                if (it == "fav") onFavorites() else onHome()
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 16.dp, bottom = 24.dp)
        ) {
            item {
                Column {
                    SearchField(query, { query = it }, strings.searchName)
                    SectionLabel(strings.entriesCount.format(category.groups.size))
                }
            }

            if (visible.isEmpty()) {
                item {
                    EmptyState(strings.nothingFoundTitle, strings.nothingFoundGroups.format(query))
                }
            }

            items(visible, key = { it.id }) { group ->
                val sideColor = when (group.side) {
                    "sarazenen" -> Palette.Oasis
                    "kreuzfahrer" -> Palette.Blood
                    else -> null
                }
                CharacterTile(
                    characterId = group.id,
                    accentColor = sideColor ?: Palette.Brass,
                    onClick = { onGroup(group) },
                    portraitSize = if (category.id == "units") 68.dp else 52.dp
                ) {
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
                                strings.soundsCount.format(group.sounds.size),
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
    val strings = viewModel.strings
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
                subtitle = group.role.ifBlank { strings.soundsCount.format(group.sounds.size) },
                onBack = onBack,
                sideColor = sideColor
            )
        },
        bottomBar = {
            BottomTabs("home", strings.tabStart, strings.tabFavorites) {
                if (it == "fav") onFavorites() else onHome()
            }
        }
    ) {
        SoundList(
            viewModel = viewModel,
            sounds = visible,
            showGroupName = false,
            header = {
                SearchField(query, { query = it }, strings.searchFile)
                SectionLabel(strings.soundsCount.format(group.sounds.size))
            },
            emptyContent = {
                if (group.sounds.isEmpty()) {
                    EmptyState(
                        strings.noFilesTitle,
                        strings.noFilesText.format(
                            "assets/sounds/${group.categoryId}/${group.id}/${viewModel.language.code}/"
                        )
                    )
                } else {
                    EmptyState(strings.nothingFoundTitle, strings.nothingFoundSounds.format(query))
                }
            }
        )
    }
}

/* --------------------------------------------------------------- Favoriten */

@Composable
private fun FavoritesScreen(viewModel: MainViewModel, onHome: () -> Unit) {
    val strings = viewModel.strings
    val favorites by viewModel.favorites.collectAsState()
    val sounds = viewModel.favoriteSounds(favorites)

    DesertScaffold(
        topBar = {
            TopBanner(title = strings.favorites, subtitle = strings.favoritesSubtitle, onBack = onHome)
        },
        bottomBar = {
            BottomTabs("fav", strings.tabStart, strings.tabFavorites) {
                if (it == "home") onHome()
            }
        }
    ) {
        SoundList(
            viewModel = viewModel,
            sounds = sounds,
            showGroupName = true,
            header = { SectionLabel(strings.savedCount.format(sounds.size)) },
            emptyContent = { EmptyState(strings.noFavoritesTitle, strings.noFavoritesText) }
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
    val strings = viewModel.strings
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
                isActive = playback.soundId == sound.id,
                isPlaying = playback.soundId == sound.id && playback.isPlaying,
                progress = if (playback.soundId == sound.id) playback.progress else 0f,
                duration = viewModel.durations[sound.id].orEmpty(),
                showGroupName = showGroupName,
                playLabel = strings.play,
                pauseLabel = strings.pause,
                addFavoriteLabel = strings.addFavorite,
                removeFavoriteLabel = strings.removeFavorite,
                downloadLabel = strings.download,
                shareLabel = strings.share,
                onPlay = { viewModel.play(sound) },
                onToggleFavorite = {
                    val added = !favorites.contains(sound.id)
                    viewModel.toggleFavorite(sound.id)
                    Toast.makeText(
                        context,
                        if (added) strings.favoriteAdded.format(sound.title)
                        else strings.favoriteRemoved.format(sound.title),
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onDownload = {
                    val ok = SoundActions.saveToDownloads(context, sound)
                    Toast.makeText(
                        context,
                        if (ok) strings.downloadDone else strings.downloadFailed,
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onShare = {
                    val ok = SoundActions.share(context, sound, strings.shareChooser)
                    if (!ok) Toast.makeText(context, strings.shareFailed, Toast.LENGTH_SHORT).show()
                },
                onSeek = { fraction -> viewModel.seek(fraction) }
            )
        }
    }
}
