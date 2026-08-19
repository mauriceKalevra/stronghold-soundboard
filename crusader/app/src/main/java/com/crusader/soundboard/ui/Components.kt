package com.crusader.soundboard.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.crusader.soundboard.R
import com.crusader.soundboard.data.Sound

/** Grundgeruest: Pixelwueste im Hintergrund, darueber Kopfzeile, Inhalt, Tableiste. */
@Composable
fun DesertScaffold(
    dimBackground: Boolean = false,
    scrim: Brush? = null,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Box(Modifier.fillMaxSize().background(Palette.Night)) {
        Image(
            bitmap = ImageBitmap.imageResource(R.drawable.stronghold),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            alignment = Alignment.Center,
            contentScale = ContentScale.Fit,
            filterQuality = FilterQuality.None
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(scrim ?: if (dimBackground) DesertScrimDim else DesertScrim)
        )
        Column(Modifier.fillMaxSize().systemBarsPadding()) {
            topBar()
            Column(Modifier.fillMaxWidth().weight(1f)) { content() }
            bottomBar()
        }
    }
}

@Composable
fun TopBanner(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    sideColor: Color? = null,
    action: (@Composable () -> Unit)? = null
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BannerGradient)
                .padding(start = 6.dp, end = 6.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück", tint = Palette.Ink)
                }
            } else {
                Spacer(Modifier.width(10.dp))
            }
            if (sideColor != null) {
                Box(Modifier.width(4.dp).height(30.dp).background(sideColor))
                Spacer(Modifier.width(10.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = title.uppercase(),
                    style = Type.Display,
                    color = Palette.Parchment,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = Type.Meta,
                        color = Palette.InkDim,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
            }
            action?.invoke()
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Palette.Edge)
        )
    }
}

@Composable
fun BottomTabs(
    selected: String,
    homeLabel: String,
    favoritesLabel: String,
    onSelect: (String) -> Unit
) {
    Column {
        Box(Modifier.fillMaxWidth().height(1.dp).background(Palette.Edge))
        Row(
            Modifier
                .fillMaxWidth()
                .background(StoneGradient)
                .padding(vertical = 8.dp)
        ) {
            TabItem(Modifier.weight(1f), Icons.Filled.Home, homeLabel, selected == "home") { onSelect("home") }
            TabItem(Modifier.weight(1f), Icons.Filled.Star, favoritesLabel, selected == "fav") { onSelect("fav") }
        }
    }
}

@Composable
private fun TabItem(
    modifier: Modifier,
    icon: ImageVector,
    label: String,
    active: Boolean,
    onClick: () -> Unit
) {
    val tint = if (active) Palette.Brass else Palette.InkDim
    Column(
        modifier = modifier.clickable(onClick = onClick).padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(5.dp))
        Text(label, style = Type.Label, color = tint)
    }
}

@Composable
fun SectionLabel(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text.uppercase(), style = Type.Label, color = Palette.InkDim)
        Spacer(Modifier.width(10.dp))
        Box(Modifier.weight(1f).height(1.dp).background(Palette.EdgeSoft))
    }
}

@Composable
fun SearchField(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    val shape = RoundedCornerShape(3.dp)
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(
            color = Palette.Parchment,
            fontFamily = Type.File.fontFamily,
            fontSize = Type.File.fontSize
        ),
        cursorBrush = SolidColor(Palette.Brass),
        modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
        decorationBox = { inner ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(Palette.StoneDeep, shape)
                    .border(1.dp, Palette.EdgeSoft, shape)
                    .padding(horizontal = 12.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Search, contentDescription = null, tint = Palette.InkDim, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(9.dp))
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(placeholder, style = Type.File, color = Color(0xFF5F4F33))
                    }
                    inner()
                }
            }
        }
    )
}

/** Kleine Flagge zum Umschalten der Sprache. */
@Composable
fun FlagButton(
    flagRes: Int,
    active: Boolean,
    contentDescription: String,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(2.dp)
    Image(
        painter = painterResource(flagRes),
        contentDescription = contentDescription,
        modifier = Modifier
            .size(width = 26.dp, height = 17.dp)
            .alpha(if (active) 1f else 0.4f)
            .clip(shape)
            .border(1.dp, if (active) Palette.Brass else Palette.Edge, shape)
            .clickable(onClick = onClick)
    )
}

/** Steinkachel fuer Kategorien und Gruppen. */
@Composable
fun StoneTile(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val shape = RoundedCornerShape(3.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .background(StoneGradient, shape)
            .border(1.dp, Palette.Edge, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

/** Ordnet Charakter-Ids den hinterlegten Portraitdateien unter assets/bilder zu. */
private val CharacterPortraitAssets: Map<String, String> = mapOf(
    "richard" to "bilder/Richard_ai.jpg",
    "saladin" to "bilder/Saladin_ai.jpg",
    "sultan" to "bilder/Sultan_ai.jpg",
    "caliph" to "bilder/Caliph_ai.jpg",
    "emir" to "bilder/Emir_ai.jpg",
    "wazir" to "bilder/Wazir_ai.jpg",
    "nizar" to "bilder/Nizar_ai.jpg",
    "sheriff" to "bilder/Sheriff_ai.jpg",
    "marshal" to "bilder/Marshal_ai.jpg",
    "abbot" to "bilder/Abbot_ai.jpg",
    "rat" to "bilder/Rat_ai.jpg",
    "snake" to "bilder/Snake_ai.jpg",
    "pig" to "bilder/Pig_ai.jpg",
    "wolf" to "bilder/Wolf_ai.jpg",
    "lord_ph" to "bilder/Phillip_ai.jpg",
    "lord_fr" to "bilder/Frederick_ai.jpg",
    "archer" to "bilder/units/Archer.png",
    "arab_archer" to "bilder/units/Arab_archer.png",
    "arab_swordsman" to "bilder/units/Arab_swordsman.png",
    "crossbowman" to "bilder/units/Crossbowman.png",
    "spearman" to "bilder/units/Spearman.png",
    "pikeman" to "bilder/units/Pikeman.png",
    "maceman" to "bilder/units/Maceman.png",
    "swordsman" to "bilder/units/Swordsman.png",
    "knight" to "bilder/units/Knight.png",
    "monk" to "bilder/units/Monk.png",
    "engineer" to "bilder/units/Engineer.png",
    "tunneler" to "bilder/units/Engineer.png",
    "ladderman" to "bilder/units/Ladderman.png",
    "slinger" to "bilder/units/Slinger.png",
    "horsearcher" to "bilder/units/Horsearcher.png",
    "assassin" to "bilder/units/Assassin.png",
    "slave" to "bilder/units/Slave.png",
    "grenadier" to "bilder/units/Grenadier.png",
    "advisor" to "bilder/advisor.jpg",
    "narrator" to "bilder/advisor.jpg",
    "briefings" to "bilder/advisor.jpg"
)

private val assetImageCache = mutableMapOf<String, ImageBitmap?>()

private fun loadAssetImage(context: android.content.Context, path: String): ImageBitmap? =
    assetImageCache.getOrPut(path) {
        runCatching {
            context.assets.open(path).use { android.graphics.BitmapFactory.decodeStream(it).asImageBitmap() }
        }.getOrNull()
    }

/**
 * Steinkachel mit Charakterportrait als Icon links, Name/Infos rechts davon.
 * Laedt das Bild aus assets/bilder (siehe CharacterPortraitAssets); falls
 * (noch) keins hinterlegt ist, wird ein farbiger Platzhalter mit Initiale
 * angezeigt, damit sich das Layout nicht aendert, sobald eins ergaenzt wird.
 */
@Composable
fun CharacterTile(
    characterId: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    portraitSize: Dp = 52.dp,
    content: @Composable RowScope.() -> Unit
) {
    val shape = RoundedCornerShape(3.dp)
    val portraitShape = RoundedCornerShape(3.dp)
    val context = LocalContext.current
    val portraitPath = CharacterPortraitAssets[characterId]
    val portrait = remember(portraitPath) { portraitPath?.let { loadAssetImage(context, it) } }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .background(StoneGradient, shape)
            .border(1.dp, Palette.Edge, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(portraitSize)
                .clip(portraitShape)
                .background(Palette.StoneDeep)
                .border(1.dp, Palette.Edge, portraitShape),
            contentAlignment = Alignment.Center
        ) {
            if (portrait != null) {
                Image(
                    bitmap = portrait,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(
                    characterId.take(1).uppercase(),
                    style = Type.TileTitle,
                    color = accentColor.copy(alpha = 0.6f)
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        content()
    }
}

@Composable
fun TileGlyph(icon: ImageVector) {
    val shape = RoundedCornerShape(3.dp)
    Box(
        Modifier
            .size(38.dp)
            .background(Palette.StoneDeep, shape)
            .border(1.dp, Palette.Edge, shape),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Palette.Brass, modifier = Modifier.size(20.dp))
    }
}

/** Eine Sound-Kachel mit Abspielen, Stern, Download und Teilen. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundRow(
    sound: Sound,
    isFavorite: Boolean,
    isActive: Boolean,
    isPlaying: Boolean,
    progress: Float,
    duration: String,
    showGroupName: Boolean,
    playLabel: String,
    pauseLabel: String,
    addFavoriteLabel: String,
    removeFavoriteLabel: String,
    downloadLabel: String,
    shareLabel: String,
    onPlay: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDownload: () -> Unit,
    onShare: () -> Unit,
    onSeek: (Float) -> Unit
) {
    val shape = RoundedCornerShape(3.dp)
    val meta = buildString {
        append(sound.label)
        if (duration.isNotBlank()) append(" · ").append(duration)
        if (showGroupName) append(" · ").append(sound.groupName)
    }
    var dragProgress by remember(sound.id) { mutableStateOf<Float?>(null) }
    Column(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .background(StoneGradient, shape)
            .border(1.dp, Palette.Edge, shape)
            .clickable(onClick = onPlay)
    ) {
            Row(
                Modifier.fillMaxWidth().padding(start = 12.dp, end = 6.dp, top = 12.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(40.dp)
                        .background(if (isActive) Palette.Brass else Palette.StoneDeep, CircleShape)
                        .border(1.dp, if (isActive) Palette.Brass else Palette.Edge, CircleShape)
                        .clickable(onClick = onPlay),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) pauseLabel else playLabel,
                        tint = if (isActive) Palette.Night else Palette.Brass,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = sound.title,
                        style = Type.File,
                        color = if (isActive) Palette.Brass else Palette.Parchment
                    )
                    Text(
                        text = meta,
                        style = Type.Meta,
                        color = Palette.InkDim,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
                IconButton(onClick = onToggleFavorite, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = if (isFavorite) removeFavoriteLabel else addFavoriteLabel,
                        tint = if (isFavorite) Palette.Brass else Palette.InkDim,
                        modifier = Modifier.size(19.dp)
                    )
                }
                IconButton(onClick = onDownload, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Filled.Download, contentDescription = downloadLabel, tint = Palette.InkDim, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onShare, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Filled.Share, contentDescription = shareLabel, tint = Palette.InkDim, modifier = Modifier.size(17.dp))
                }
            }
            if (isActive) {
                Slider(
                    value = dragProgress ?: progress.coerceIn(0f, 1f),
                    onValueChange = {
                        dragProgress = it
                        onSeek(it)
                    },
                    onValueChangeFinished = { dragProgress = null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 6.dp)
                        .height(28.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = Palette.Brass,
                        activeTrackColor = Palette.Ember,
                        inactiveTrackColor = Palette.StoneDeep
                    ),
                    track = { state ->
                        val fraction = (state.value - state.valueRange.start) /
                            (state.valueRange.endInclusive - state.valueRange.start)
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Palette.StoneDeep)
                        ) {
                            Box(
                                Modifier
                                    .fillMaxWidth(fraction.coerceIn(0f, 1f))
                                    .fillMaxHeight()
                                    .background(Palette.Ember)
                            )
                        }
                    }
                )
            }
        }
    }

/** Leerer Zustand mit Hinweis, was als Naechstes zu tun ist. */
@Composable
fun EmptyState(title: String, message: String) {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 56.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title.uppercase(), style = Type.Label, color = Palette.Ink)
        Spacer(Modifier.height(10.dp))
        Text(message, style = Type.Body, color = Palette.InkDim)
    }
}
