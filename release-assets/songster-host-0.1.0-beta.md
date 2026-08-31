# Songster Host 0.1.0-beta

Dieses Asset beschreibt den ersten sideloadbaren Beta-Stand. Die APKs werden mit
`scripts/build-beta.ps1` aus dem Android-Projekt erzeugt und landet als:

```text
dist/songster-host-0.1.0-beta-debug.apk
dist/songster-host-0.1.0-beta-release-unsigned.apk
```

Die Debug-APK ist für lokale Fire-TV-Tests gedacht, nicht für öffentliche
Verteilung. Für eine weitergegebene Beta sollte eine eigene Signatur
verwendet werden.
