# Songster Host

Sideloadbare Android-/Fire-TV-App für den reinen Songster-Hostmodus.

Die App selbst enthält keine Spiellogik. Sie ist eine kleine Fullscreen-WebView,
fragt einmal nach der Songster-URL und öffnet danach:

```text
https://dein-songster-server/host-app
```

Der Songster-Server stellt Pairing, Nutzerautorisierung, Hostgeräte-Verwaltung
und den eigentlichen Displaymodus bereit.

## Voraussetzung im Songster-Repo

Der Songster-Server braucht die Hostgeräte-Erweiterung aus dem Branch:

```text
codex/host-fire-tv-app
```

Wichtige Server/Web-Funktionen dort:

- `/host-app` als TV-Weboberfläche
- `POST /api/v1/host-devices/pairings`
- Profil: Host-App-Code bestätigen und Geräte trennen
- privater Tischraum: aktives Hostgerät als Anzeige verwenden
- gerätegebundene Display-Tokens

## App bauen

Benötigt:

- Android SDK
- JDK 17 oder neuer
- Gradle oder ein nachträglich erzeugter Gradle Wrapper

Debug-Beta bauen:

```powershell
.\scripts\build-beta.ps1
```

Das erzeugt:

```text
dist/songster-host-0.1.0-beta-debug.apk
dist/songster-host-0.1.0-beta-release.apk
```

Falls kein systemweites `gradle` installiert ist, im Repo einmalig einen Wrapper
erzeugen:

```powershell
gradle wrapper
.\gradlew.bat assembleDebug
```

## Installation auf Fire TV

1. Debugging/Sideloading auf dem Fire TV aktivieren.
2. APK aus `dist/` auf das Gerät übertragen.
3. Installieren, zum Beispiel per `adb`:

```powershell
adb connect FIRE_TV_IP:5555
adb install -r .\dist\songster-host-0.1.0-beta-debug.apk
```

## Nutzung

1. App starten.
2. Songster-URL eingeben.
3. Die App zeigt den Host-App-Code.
4. In Songster am Handy anmelden.
5. Profil öffnen und Code unter „Host-App“ bestätigen.
6. Einen privaten Tisch öffnen.
7. Im Tischraum das aktive Hostgerät auswählen.
8. Die Fire-TV-App wechselt automatisch in den Hostmodus.

Mit der Menü-Taste kann die gespeicherte Songster-URL zurückgesetzt werden.

## Status

`0.1.0-beta` ist ein bewusst dünner MVP:

- native URL-Eingabe
- Fullscreen-WebView
- DOM Storage und Audio aktiviert
- Wake-Lock während der App-Nutzung
- Fire-TV/Android-TV Launcher-Eintrag

Noch offen für eine spätere Beta:

- hübscherer nativer Setup-Screen
- signierte Release-APK
- eigenes Launcher-Banner
- optionaler QR-Code auf dem nativen URL-Screen
