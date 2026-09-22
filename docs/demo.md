# Demomodus

Adolar Host selbst hat kein Backend und keine Nutzerdaten - die eigentlichen
Demomodi leben in den Server-Repos (`songster`/`adolar` fuer Songster,
`bloeki`). Diese App braucht deshalb keinen eigenen Reset-Mechanismus,
sondern nur eine Möglichkeit, ein Vorführgerät fest auf die jeweilige
Demo-Instanz zu binden, statt eine beliebige (oder gar eine echte) Server-URL
eingeben zu lassen.

## Was der Demomodus macht

- Die Auswahl "Songster" / "blöki" bleibt, aber die Server-URL-Eingabe
  entfällt: die App lädt direkt die zur Buildzeit fest einkompilierte
  Demo-URL des jeweiligen Spiels.
- Kein Speichern/Lesen einer selbst eingegebenen URL in den
  `SharedPreferences` - ein Demo-Gerät kann so nicht versehentlich (oder
  absichtlich) auf eine andere, insbesondere eine echte, Instanz umgestellt
  werden.
- Ein kleines "DEMO"-Wasserzeichen wird oben rechts über die WebView gelegt.
  Da diese App keine eigene HTML-Seite hat, in die sich - wie bei den
  Web-Demos von Songster/Adolar und blöki - ein Banner einbauen ließe, ist
  das hier eine native Einblendung.

## Aktivierung

Rein über Gradle-Properties beim Bauen - **kein** Build-Flavor, **keine**
Laufzeit-Umschaltung. Ein Build ohne diese Properties ist byte-identisch mit
dem, was CI ohnehin über `gradle assembleDebug assembleRelease` erzeugt;
`demoMode` ist standardmäßig `false`.

```powershell
.\scripts\build-demo.ps1 `
  -DemoSongsterUrl "https://songster-demo.example.de" `
  -DemoBloekiUrl "https://bloeki-demo.example.de"
```

Das entspricht direkt aufgerufen:

```powershell
gradle assembleDebug assembleRelease `
  -PdemoMode=true `
  -PdemoSongsterUrl=https://songster-demo.example.de `
  -PdemoBloekiUrl=https://bloeki-demo.example.de
```

Die beiden Demo-URLs sind die `/host-app`-Basis-URLs der jeweiligen
isolierten Demo-Deployments (siehe `docs/demo.md` in den Server-Repos:
`bloeki`s `compose.demo.yaml`, Adolars `docker-compose.demo.yml` für
Songster).

## Installation

Wie im Haupt-README beschrieben, nur mit der Demo-APK aus
`dist/adolar-host-demo-*.apk` statt der normalen Beta-APK.
