# Crusader Soundboard – Android-App

Vollständiges Android-Projekt (Kotlin + Jetpack Compose) für ein Soundboard im Stil des
Prototyps: Ladebildschirm mit rotem Balken, Pixel-Wüste als Hintergrund, Kategorien →
Charaktere → Sounds, dazu Favoriten, Download und Teilen.

Die App enthält **keine Audiodateien**. Die Ordnerstruktur ist vorbereitet – du legst deine
eigenen Dateien hinein (siehe „Sounds einfügen“).

---

## 1. Voraussetzungen

| | |
|---|---|
| Android Studio | Ladybug (2024.2) oder neuer |
| JDK | 17 (bringt Android Studio mit) |
| Gradle | 8.9 (lädt Android Studio automatisch) |
| Mindest-Android | 10 (API 29) |

## 2. Projekt öffnen

1. ZIP entpacken.
2. In Android Studio **File → Open** und den entpackten Ordner auswählen.
3. Den ersten Gradle-Sync durchlaufen lassen (lädt die Abhängigkeiten, braucht Internet).

> Im ZIP fehlt bewusst die Datei `gradle/wrapper/gradle-wrapper.jar` – eine Binärdatei, die
> ich hier nicht mitliefern kann. Android Studio ergänzt sie beim ersten Sync selbst.
> Falls du lieber auf der Kommandozeile arbeitest: einmal `gradle wrapper` ausführen,
> danach funktioniert `./gradlew`.

## 3. Sounds einfügen

Die App liest ihre Ordner beim Start selbst aus. Du musst nur Dateien ablegen:

```
app/src/main/assets/
├── catalog.json                  ← Struktur (Kategorien und Gruppen)
├── music/
│   └── intro.mp3                 ← Musik für den Ladebildschirm (optional)
└── sounds/
    ├── characters/
    │   ├── richard/              ← ri_anger_01.mp3, ri_congrats_01.mp3, …
    │   ├── saladin/
    │   └── …
    ├── narrator/narrator/
    ├── population/peasants/
    ├── units/archer/
    └── music/themes/
```

**Namensschema:** `kürzel_art_nummer.mp3`, zum Beispiel `ri_anger_02.mp3`.
Aus dem mittleren Teil macht die App automatisch die Beschriftung („Wut“).
Bekannte Arten: anger, congrats, taunt, greet, attack, defeat, peace, victory, intro,
mission, hint, cheer, boo, work, hunger, buy, sell, trade, select, move, death, fire,
load, hit, theme, siege, oasis, wind. Unbekannte Wörter werden einfach übernommen.

Erlaubte Endungen: `.mp3`, `.ogg`, `.wav`, `.m4a`.
Die `README.txt` in den Ordnern kannst du liegen lassen oder löschen.

### Neue Kategorie oder Figur

`app/src/main/assets/catalog.json` erweitern und den passenden Ordner anlegen:

```json
{ "id": "khan", "name": "Der Khan", "role": "Reiterfürst", "side": "sarazenen" }
```

`side` färbt den Wimpel: `kreuzfahrer` rot, `sarazenen` grün, leer = kein Wimpel.
Ordner dazu: `assets/sounds/characters/khan/`.

## 4. App bauen

* **Zum Testen:** Gerät anschließen oder Emulator starten, dann in Android Studio auf ▶.
* **APK zum Weitergeben:** `Build → Build Bundle(s)/APK(s) → Build APK(s)`
  oder auf der Kommandozeile `./gradlew assembleDebug`.
  Ergebnis: `app/build/outputs/apk/debug/app-debug.apk`.
* **Signierte Version:** `Build → Generate Signed App Bundle / APK`.

## 5. Was wo liegt

```
app/src/main/java/com/crusader/soundboard/
├── MainActivity.kt          Einstieg, setzt Theme und Navigation
├── MainViewModel.kt         Katalog, Favoriten, Wiedergabe, Laufzeiten
├── data/
│   ├── Models.kt            Sound, SoundGroup, Category, Catalog
│   ├── CatalogRepository.kt liest catalog.json + durchsucht die Asset-Ordner
│   └── FavoritesStore.kt    Favoriten in SharedPreferences
├── audio/
│   ├── SoundPlayer.kt       spielt einen Sound zur Zeit, meldet den Fortschritt
│   └── IntroMusic.kt        Musik im Ladebildschirm inkl. Ausblenden
├── ui/
│   ├── Theme.kt             Farben, Schriften, Verläufe
│   ├── Components.kt        Steinkacheln, Sound-Zeile, Kopf- und Tableiste
│   └── Screens.kt           Ladebildschirm, Start, Listen, Favoriten
└── util/SoundActions.kt     Download nach Downloads/, Teilen über FileProvider
```

Grafiken: `app/src/main/res/drawable-nodpi/bg_desert.png` (Hintergrund) und die
Launcher-Icons unter `res/mipmap-*`. Beide entstehen aus `tools_generate_art.py`
(Python + Pillow) – Werte ändern, Skript laufen lassen, fertig. Ein eigenes Foto oder
Artwork geht genauso: Datei als `bg_desert.png` ablegen, gleiche Stelle.

## 6. Stellschrauben

| Was | Wo |
|---|---|
| Dauer des Ladebalkens | `ui/Screens.kt`, `LOAD_MILLIS = 3000L` |
| Texte im Ladebildschirm | `ui/Screens.kt`, `LOADING_LINES` |
| Farbe des Ladebalkens | `ui/Theme.kt`, `EmberGradient` |
| Abdunklung des Hintergrunds | `ui/Theme.kt`, `DesertScrim` |
| Lautstärke der Intromusik | `audio/IntroMusic.kt`, `VOLUME` |
| Älteres Android unterstützen | `app/build.gradle.kts`, `minSdk` – unter 29 braucht der Download zusätzlich die Berechtigung `WRITE_EXTERNAL_STORAGE` |

## 7. Rechtliches

Die Sounds, die Musik und die Grafiken von *Stronghold Crusader* gehören Firefly Studios.
Für den privaten Gebrauch auf deinem eigenen Gerät ist das unkritisch – eine
Veröffentlichung im Play Store oder anderswo wäre ohne Erlaubnis von Firefly nicht zulässig.
Hintergrundbild und Icon in diesem Projekt sind eigene Pixelgrafiken und enthalten kein
Material aus dem Spiel.

## 8. Naheliegende Erweiterungen

* Sound als Klingelton oder Benachrichtigungston setzen (`RingtoneManager`, braucht `WRITE_SETTINGS`)
* Zufallswiedergabe und „zuletzt gespielt“
* Widget mit den drei häufigsten Sounds
* Lautstärkeregler pro Sound und Wiedergabe im Hintergrund (Media3/ExoPlayer statt MediaPlayer)
