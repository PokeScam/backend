# PokeScan API – README

Diese Dokumentation beschreibt die öffentlich genutzten Endpunkte der PokeScan-API für das Frontend.  
Alle Antworten sind JSON. Pagination folgt dem Spring-`Page`-Format (`content`, `number`, `size`, `totalElements`, …).

- **Base URL (lokal):** `http://localhost:8080/api`
- **Content-Type:** `application/json`
- **Fehler:** `application/problem+json` (RFC 7807)

---

## Endpunkte

- [`GET /api/cards`](#get-apicards) – Karten durchsuchen/auflisten
- [`GET /api/sets`](#get-apisets) – Sets durchsuchen/auflisten
- [`GET /api/series`](#get-apiseries) – Serien durchsuchen/auflisten

---

## Gemeinsame Konzepte

### Pagination
- Query-Parameter: `page` (0-basiert, Standard `0`), `size` (Standard `50`, Max `200`)
- Antwortfelder:
    - `content`: Liste der Ergebnisse
    - `number`: aktuelle Seite (0-basiert)
    - `size`: Seitengröße
    - `totalElements`: Gesamttreffer

### Validierung & Fehler
- Ungültige Parameter → **400** mit Payload:
  ```json
  {
    "title": "Validation failed",
    "status": 400,
    "detail": "invalid type for 'page'"
  }
  ```
- Typische Validierungen:
    - `page` ≥ 0
    - `size` ∈ [1, 200]
    - `set`, `setId`, `seriesId`: `[A-Za-z0-9_-]{0,20}`
    - `lang`: `[A-Za-z]{2,5}`
    - `q`, `series`: Buchstaben/Ziffern/Leerz./`-_'!?.` (max. 50 Zeichen)
    - `rarity`: `[A-Za-z0-9 \-/]{0,30}`

---

## `GET /api/cards`

Kartenliste mit Filtern. Ohne Filter gilt eine **Default-Sortierung** (siehe unten).

### Query-Parameter

| Name       | Typ     | Beschreibung |
|------------|---------|--------------|
| `set`      | string  | **Lokalisierte** Set-ID inkl. Sprache, z. B. `xy7-en`. **Hat Priorität** vor `setId` + `lang`. |
| `setId`    | string  | Set-ID **ohne** Sprache, z. B. `xy7`. |
| `lang`     | string  | Sprache (z. B. `en`, `de`, `ja`). |
| `rarity`   | string  | Filter auf Rarität (Teilstring). |
| `q`        | string  | Volltextsuche im **Kartennamen**. |
| `series`   | string  | Serienname (Teilstring). |
| `seriesId` | string  | Serien-ID (exakt). |
| `page`     | int     | Pagination (0-basiert). |
| `size`     | int     | Seitengröße (1–200). |

### Sortierung
- **Ohne Filter**: Sprache (`en` → `de` → `ja` → sonst), dann Set-Release (neu → alt), dann `localId` aufsteigend.
- **Mit Filtern**: *noch* keine garantierte Sortierung.

### Antwortschema (Auszug)

```jsonc
{
  "content": [{
    "id": "xy7-12-en",
    "name": "Groudon",
    "rarity": "Rare Holo",
    "localId": 12,
    "lang": "en",
    "illustrator": "Ken Sugimori",
    "category": 1,
    "type": "Pokemon",
    "dexId": 383,
    "set": {
      "id": "xy7",
      "name": "Ancient Origins",
      "releaseDate": "2015-08-12",
      "seriesId": "xy",
      "seriesName": "XY Series",
      "cardCount": { "official": 98, "total": 101 },
      "localizedId": "xy7-en"
    },
    "variants": {
      "normal": false,
      "holo": true,
      "reverse": true,
      "firstEdition": false,
      "wPromo": false
    }
  }],
  "number": 0,
  "size": 50,
  "totalElements": 1234
}
```

---

## `GET /api/sets`

Set-Liste mit optionalen Filtern.  
**Default-Sortierung (ohne Filter):** `releaseDate` absteigend (neu → alt), Nulls zuletzt.

### Query-Parameter

| Name       | Typ     | Beschreibung                          |
|------------|---------|---------------------------------------|
| `seriesId` | string  | Serien-ID (exakt).                    |
| `series`   | string  | Serien-**name** (Teilstring).         |
| `q`        | string  | Set-**name** (Teilstring).            |
| `page`     | int     | Pagination (0-basiert).               |
| `size`     | int     | Seitengröße (1–200).                  |

### Antwortschema (Auszug)

```json
{
  "content": [{
    "id": "xy7-en",
    "name": "Ancient Origins",
    "releaseDate": "2015-08-12",
    "seriesId": "xy",
    "seriesName": "XY Series"
  }],
  "number": 0,
  "size": 50,
  "totalElements": 123
}
```

---

## `GET /api/series`

Serienübersicht, optional mit Namenssuche.  
**Sortierung:** `releaseYear` aufsteigend, dann `seriesId`.

### Query-Parameter

| Name | Typ    | Beschreibung               |
|------|--------|----------------------------|
| `q`  | string | Serien-**name** (Teilstring) |
| `page` | int  | Pagination (0-basiert)     |
| `size` | int  | Seitengröße (1–200)        |

### Antwortschema (Auszug)

```json
{
  "content": [{
    "id": "xy",
    "name": "XY Series",
    "releaseYear": 2013,
    "lang": "en"
  }],
  "number": 0,
  "size": 50,
  "totalElements": 12
}
```
