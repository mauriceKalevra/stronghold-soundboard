package com.crusader.soundboard.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** Farben aus dem Prototyp: Sandstein, Messing, Glut. */
object Palette {
    val Night = Color(0xFF0C0803)
    val Stone = Color(0xFF1B130A)
    val StoneLit = Color(0xFF2A1E11)
    val StoneDeep = Color(0xFF150E06)
    val Edge = Color(0xFF4B3719)
    val EdgeSoft = Color(0xFF33240F)
    val Parchment = Color(0xFFF0E2C0)
    val Ink = Color(0xFFB69C71)
    val InkDim = Color(0xFF7D6844)
    val Brass = Color(0xFFD8A441)
    val BrassSoft = Color(0xFF9E7726)
    val Ember = Color(0xFFC4382B)
    val EmberLit = Color(0xFFD9452F)
    val EmberDark = Color(0xFF6E1B14)
    val Blood = Color(0xFF8E2B22)
    val Oasis = Color(0xFF3E8C7A)
    val BarFrame = Color(0xFF6B5730)
    val BarWell = Color(0xFF0B0703)
}

/** Der Verlauf, der die Wuestengrafik abdunkelt, damit die Schrift lesbar bleibt. */
val DesertScrim = Brush.verticalGradient(
    0.00f to Color(0xDB080603),
    0.26f to Color(0x9E080603),
    0.52f to Color(0x66080603),
    0.78f to Color(0x94080603),
    1.00f to Color(0xD6080603)
)
/** Ladebildschirm: Bild bleibt sichtbar, nur unten wird es für Balken und Text dunkler. */
val SplashScrim = Brush.verticalGradient(
    0.00f to Color(0x33080603),
    0.55f to Color(0x1A080603),
    1.00f to Color(0xC2080603)
)
val DesertScrimDim = Brush.verticalGradient(
    0f to Color(0xE6080603),
    1f to Color(0xE6080603)
)

val StoneGradient = Brush.verticalGradient(listOf(Palette.StoneLit, Palette.Stone))
val BannerGradient = Brush.verticalGradient(listOf(Color(0xFF241A0E), Color(0xFF170F07)))
val EmberGradient = Brush.verticalGradient(listOf(Palette.EmberLit, Palette.Ember, Palette.EmberDark))

/** Serifen fuer Titel, Monospace fuer Dateinamen. */
object Type {
    val Logo = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        letterSpacing = 4.sp
    )
    val LogoSub = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        letterSpacing = 6.sp
    )
    val Display = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        letterSpacing = 2.sp
    )
    val TileTitle = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    )
    val Meta = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp
    )
    val File = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 13.5.sp
    )
    val Label = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 10.sp,
        letterSpacing = 3.sp
    )
    val Body = TextStyle(
        fontFamily = FontFamily.Serif,
        fontSize = 14.sp
    )
}

private val ColorScheme = darkColorScheme(
    primary = Palette.Brass,
    onPrimary = Palette.Night,
    secondary = Palette.Ember,
    background = Palette.Night,
    onBackground = Palette.Parchment,
    surface = Palette.Stone,
    onSurface = Palette.Parchment
)

/** Die App ist bewusst immer dunkel, unabhaengig von der Systemeinstellung. */
@Composable
fun SoundboardTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = ColorScheme, content = content)
}
