# Echo Journal 🎙️

A simple voice-first journal for Android. Record how you feel, add a quick text or photo if you want, and let it save itself — that's the whole idea.

I built this after watching a movie scene where someone's old voice recordings became the way their family remembered them. It made me realize we photograph everything but rarely record our own voice, our own thoughts, for our future selves. So instead of hunting for an app that already did this, I built one over a night.

## What it does

- 🎙️ **Voice notes** — tap, talk, done
- 📝 **Text notes** — for when you'd rather write
- 📷 **Photos & videos** — attach a moment alongside your words
- 🕒 **Auto date & time** — every entry is timestamped automatically
- 🔀 **Sorting** — newest, oldest, or grouped by type
- ☁️ **Google Drive auto-sync** — your entries back themselves up in the background

No ads, no account walls, no clutter. Just you and your own memories.

## Tech stack

Kotlin, Jetpack Compose, Room (local storage), WorkManager (background sync), Google Drive REST API.

## Getting started

1. Clone the repo and open it in Android Studio.
2. Let Gradle sync (needs internet on first run).
3. Run on a device or emulator — Android 8.0 (API 26) or higher.

That covers everything except Drive sync — text, voice, photo, and video notes, sorting, and dates all work fully offline out of the box.

### Enabling Google Drive sync

Drive sync needs your own free OAuth credentials from Google Cloud Console (a few minutes, one-time):

1. Create a project at [console.cloud.google.com](https://console.cloud.google.com) and enable the **Google Drive API**.
2. Create an OAuth client of type **Android** — package name `com.echojournal.app`, plus your debug SHA-1 (get it via Android Studio's Gradle panel → `app > Tasks > android > signingReport`).
3. Create a second OAuth client of type **Web application** and copy its Client ID.
4. Paste that ID into `app/src/main/res/values/strings.xml`:
   ```xml
   <string name="drive_web_client_id">YOUR_CLIENT_ID.apps.googleusercontent.com</string>
   ```
5. Rebuild, run, and tap the cloud icon to sign in.

## Project structure

```
app/src/main/java/com/echojournal/app/
  data/        Room entity, DAO, database
  repository/  Sorting + CRUD logic
  viewmodel/   Screen state
  ui/          Compose screens & components
  util/        Date formatting, file storage, voice recording
  sync/        Google Drive sync
```

## Why

Photos capture what things looked like. This is for capturing what you actually sounded like — what you were thinking, worrying about, hoping for — on an ordinary Tuesday you'd otherwise forget.

One day these recordings might not just remind me what happened. They might remind me who I used to be.


