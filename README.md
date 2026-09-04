# Adolar Host

Sideloadbare Android-/Fire-TV-App fuer den gemeinsamen Hostmodus von Songster
und bloeki.

Die App selbst enthaelt keine Spiellogik. Sie ist eine kleine
Fullscreen-WebView, fragt beim Start welches Spiel gehostet werden soll und
oeffnet danach die jeweilige Web-Host-App:

```text
https://dein-songster-server/host-app
https://dein-bloeki-server/host-app
```

Pairing, Nutzerautorisierung, Hostgeraete-Verwaltung und der eigentliche
Displaymodus kommen weiterhin aus dem jeweils ausgewaehlten Server.

## Voraussetzung in den Server-Repos

Songster und bloeki brauchen denselben Hostgeraete-Contract:

- `/host-app` als TV-Weboberflaeche
- `POST /api/v1/host-devices/pairings`
- Profil: Host-App-Code bestaetigen und Geraete trennen
- privater Tischraum: aktives Hostgeraet als Anzeige verwenden
- geraetegebundene Display-Tokens

Songster und bloeki werden bewusst separat gekoppelt, weil beide eigene
Backends und eigene `host_device`-Tabellen haben.

## App bauen

Benoetigt:

- Android SDK
- JDK 17 oder neuer
- Gradle oder ein nachtraeglich erzeugter Gradle Wrapper

Debug-Beta bauen:

```powershell
.\scripts\build-beta.ps1
```

Das erzeugt:

```text
dist/adolar-host-0.1.0-beta-debug.apk
dist/adolar-host-0.1.0-beta-release.apk
```

Falls kein systemweites `gradle` installiert ist, im Repo einmalig einen
Wrapper erzeugen:

```powershell
gradle wrapper
.\gradlew.bat assembleDebug
```

## Installation auf Fire TV

1. Debugging/Sideloading auf dem Fire TV aktivieren.
2. APK aus `dist/` auf das Geraet uebertragen.
3. Installieren, zum Beispiel per `adb`:

```powershell
adb connect FIRE_TV_IP:5555
adb install -r .\dist\adolar-host-0.1.0-beta-debug.apk
```

## Nutzung

1. App starten.
2. Songster oder bloeki auswaehlen.
3. Beim ersten Start des jeweiligen Spiels die passende Server-URL eingeben.
4. Die Web-Host-App zeigt den Host-App-Code und QR-Code.
5. Im passenden Spiel am Handy anmelden.
6. Profil oeffnen und Code unter "Host-App" bestaetigen.
7. Einen privaten Tisch oeffnen.
8. Im Tischraum das aktive Hostgeraet auswaehlen.
9. Die Fire-TV-App wechselt automatisch in den Hostmodus.

Mit der Menue-Taste kommt man jederzeit zur Spielauswahl zurueck. Die App
speichert die Songster- und bloeki-Server-URL getrennt.

## Status

`0.1.0-beta` ist eine bewusst duenne Host-Shell:

- native Spielauswahl fuer Songster und bloeki
- getrennte Server-URLs je Spiel
- Fullscreen-WebView
- DOM Storage und Medienwiedergabe ohne User-Geste aktiviert
- Wake-Lock waehrend der App-Nutzung
- Fire-TV/Android-TV Launcher-Eintrag

Noch offen fuer eine spaetere Beta:

- huebscherer nativer Setup-Screen
- signierte Release-APK
- eigenes Launcher-Banner
- optionaler QR-Code auf dem nativen URL-Screen
