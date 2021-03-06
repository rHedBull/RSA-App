# RSA-App

## Project Goals

### Nächstes Ziel

- Desktop Chat-App mit folgenden Funktionalitäten

  - **Übertragung von Nachrichten** von Client über Server (Raspberry Pi) an andere Clients
  - **Verschlüsselung** der Nachrichten **per RSA**
  - Wechsel zwischen **manuellem und automatischen Modus**
    - Manuelle Schlüsseleingabe (und Darstellung aller Rechenvorgänge) im manuellen Modus
    - Automatische Schlüsselgenerierung im automatischen Modus
  - Darstellung des Ganzen durch ein simples **GUI**
  - **Automatisches** Loggen aller Ereignisse zum Debuggen und für Bug-Fixes

### Hauptziel

- Android Chat-App mit obigen Funktionalitäten  

## Repository Struktur

Zurzeit ist unser Repository in zwei größere Ordner unterteilt:

- **mavenProject**
  - Dieser Ordner stellt ein [Maven](https://de.wikipedia.org/wiki/Apache_Maven)-Projekt dar. Hier läuft die eigentliche Entwicklung der App ab.
    - Im Ordner **mavenProject\src\main\java** liegt
      der _Source-Code_.
    - Im Ordner **mavenProject\src\main\test** liegt der Code, der den Source-Code mit Hilfe von _[JUnit](https://de.wikipedia.org/wiki/JUnit) 5_ testet. Also der _Test-Code_.
    - Die Datei **mavenProject\pom.xml** beschreibt die Eigenschaften des Maven-Projekts. Damit ist nicht nur der Projekt-Name (siehe `<artifactId> ... </artifactId>`) gemeint, sondern auch die verwendeten Libraries (siehe `<dependencies> ... </dependencies>`). Zudem wird fest gelegt, welche `.jar`-Dateien bei einem `mvn install` erstellt werden (siehe `<executions> ... </executions>`).
- **organization**
  - In diesem Ordner liegen alle Dateien zur Planung und Organisation des Projektes. Der Ordner ist noch einmal in fünf Ordner unterteilt. Je ein Ordner für jedes Team und ein einziger Ordner für alle. Im Ordner für alle sind beispielsweise Tutorials für alle und Meeting Protokolle von größeren Meetings, auf denen grundlegende Sachen besprochen wurden, festgehalten. Auch die festgelegten Konventionen sind im _organization_-Verzeichnis zu finden.

## Beim Projekt mitmachen

Wenn du zu diesem Repository hinzugefügt wurdest, fehlen nur noch wenige Schritte, um auch selbst den Source-Code aktiv zu entwickeln. Eine detaillierte Schritt-für-Schritt Anleitung findest du [hier](https://github.com/STAMACODING/RSA-App/blob/master/organization/all/tutorials/setupTutorial.md).

## Unsere Webseite

Eine Übersicht über unser Repository kannst du auch über unsere [Webseite](https://stamacoding.github.io/RSA-App/) finden.
