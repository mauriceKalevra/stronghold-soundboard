# Crusader Soundboard – Android-App
By Maurice Kalevra.
This is a private project.  

---

## 1. Voraussetzungen

| | |
|---|---|
| Android Studio | Ladybug (2024.2) oder neuer |
| JDK | 17 (bringt Android Studio mit) |
| Gradle | 8.9 (lädt Android Studio automatisch) |
| Mindest-Android | 10 (API 29) |


## 2. Struktur
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


## 8.ToDo:

* Sound als Klingelton oder Benachrichtigungston setzen (`RingtoneManager`, braucht `WRITE_SETTINGS`)
* Zufallswiedergabe und „zuletzt gespielt“
* Widget mit den drei häufigsten Sounds
* Lautstärkeregler pro Sound und Wiedergabe im Hintergrund (Media3/ExoPlayer statt MediaPlayer)
