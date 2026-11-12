# StreamBox — Android IPTV starter

A dark, set-top-box-inspired Android IPTV player starter built with Kotlin, Jetpack Compose and AndroidX Media3/ExoPlayer.

## Features
- TV-box dashboard with category navigation and channel list
- M3U/M3U8 playlist URL loading (`#EXTINF`, `group-title`, `tvg-logo` parsing)
- HLS playback through Media3 ExoPlayer
- Search, favourites, fullscreen-capable player controls
- Leanback launcher entry and D-pad-focusable navigation surfaces

## Build
1. Open this folder in Android Studio (latest stable recommended).
2. Let Gradle sync; use JDK 17.
3. Run on an Android phone, tablet, or Android TV device.

The project uses Gradle plugin versions declared in the root build file and downloads dependencies from Google Maven Central.

## Important
- No IPTV channels or paid service are included. Add an M3U/M3U8 playlist URL for streams you are authorized to access.
- The two initial entries are public demo HLS streams and may change or become unavailable.
- For production, add secure credential storage, playlist import via Android document picker, EPG/XMLTV support, robust error/retry handling, parental controls, and a proper TV-specific focus/navigation pass.
