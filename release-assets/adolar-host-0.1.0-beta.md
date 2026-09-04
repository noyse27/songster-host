# Adolar Host 0.1.0-beta

Dieses Asset beschreibt den sideloadbaren Beta-Stand fuer die gemeinsame
Songster-/bloeki-Host-App. Die APKs werden mit `scripts/build-beta.ps1` aus
dem Android-Projekt erzeugt und landen als:

```text
dist/adolar-host-0.1.0-beta-debug.apk
dist/adolar-host-0.1.0-beta-release.apk
```

Die Debug-APK ist fuer lokale Fire-TV-Tests gedacht, nicht fuer oeffentliche
Verteilung. Die Release-Beta-APK ist ebenfalls mit der Android-Debug-Signatur
signiert, damit Sideload-Tests auf Android/Fire TV funktionieren. Fuer eine
weitergegebene Beta sollte eine eigene Signatur verwendet werden.
