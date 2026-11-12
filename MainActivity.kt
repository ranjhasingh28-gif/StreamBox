package com.example.streambox

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import java.net.URL

private val Bg = Color(0xFF080D18)
private val Panel = Color(0xFF111B2B)
private val Accent = Color(0xFF19A7FF)
private val Muted = Color(0xFF91A0B5)

data class Channel(val name: String, val url: String, val group: String = "Live TV", val logo: String = "")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (4 or 1024 or 4096)
        setContent { StreamBoxApp() }
    }
}

@Composable
fun StreamBoxApp() {
    val context = LocalContext.current
    val player = remember { ExoPlayer.Builder(context).build() }
    var channels by remember { mutableStateOf(sampleChannels) }
    var selected by remember { mutableStateOf<Channel?>(null) }
    var playlistUrl by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("Add your M3U playlist to start watching") }
    var category by remember { mutableStateOf("All") }
    var query by remember { mutableStateOf("") }
    var favorites by remember { mutableStateOf(setOf<String>()) }

    DisposableEffect(player) { onDispose { player.release() } }

    val groups = listOf("All", "Live TV", "Sports", "Movies", "News", "Kids", "Favourites")
    val visible = channels.filter {
        (category == "All" || (category == "Favourites" && favorites.contains(it.url)) || it.group.equals(category, true)) &&
            it.name.contains(query, true)
    }

    MaterialTheme(colorScheme = darkColorScheme(primary = Accent, background = Bg, surface = Panel)) {
        Row(Modifier.fillMaxSize().background(Bg).padding(18.dp)) {
            Column(Modifier.width(225.dp).fillMaxHeight()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Tv, null, tint = Accent, modifier = Modifier.size(34.dp))
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("STREAMBOX", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        Text("IPTV ENTERTAINMENT", color = Muted, fontSize = 9.sp, letterSpacing = 2.sp)
                    }
                }
                Spacer(Modifier.height(28.dp))
                groups.forEach { item ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(12.dp))
                        .background(if (category == item) Color(0xFF12365A) else Color.Transparent)
                        .clickable { category = item }.focusable().padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(when(item) {
                            "All" -> Icons.Default.GridView
                            "Sports" -> Icons.Default.SportsSoccer
                            "Movies" -> Icons.Default.Movie
                            "News" -> Icons.Default.Newspaper
                            "Kids" -> Icons.Default.ChildCare
                            "Favourites" -> Icons.Default.Favorite
                            else -> Icons.Default.LiveTv
                        }, null, tint = if (category == item) Accent else Muted, modifier = Modifier.size(19.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(item, color = if (category == item) Color.White else Muted, fontWeight = if (category == item) FontWeight.Bold else FontWeight.Medium)
                    }
                }
                Spacer(Modifier.weight(1f))
                Text("STREAMBOX  •  v1.0", color = Muted, fontSize = 10.sp)
            }

            Spacer(Modifier.width(18.dp))
            Column(Modifier.weight(1f).fillMaxHeight()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Good evening", color = Muted, fontSize = 13.sp)
                        Text("Your entertainment hub", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.width(220.dp),
                        placeholder = { Text("Search channels", fontSize = 12.sp) }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true)
                    Spacer(Modifier.width(10.dp))
                    IconButton(onClick = { message = "Enter an M3U playlist URL below." }) { Icon(Icons.Default.Settings, null, tint = Muted) }
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(Modifier.weight(1.25f)) {
                        Box(Modifier.fillMaxWidth().weight(0.9f).clip(RoundedCornerShape(18.dp)).background(Color.Black), contentAlignment = Alignment.Center) {
                            AndroidView(factory = { ctx -> PlayerView(ctx).apply { this.player = player; useController = true; layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) } }, modifier = Modifier.fillMaxSize())
                            if (selected == null) Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.LiveTv, null, tint = Accent, modifier = Modifier.size(48.dp))
                                Spacer(Modifier.height(8.dp))
                                Text("STREAMBOX PLAYER", color = Color.White, fontWeight = FontWeight.Bold)
                                Text("Select a channel to begin", color = Muted, fontSize = 12.sp)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Panel).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(selected?.name ?: "No channel selected", color = Color.White, fontWeight = FontWeight.Bold)
                                Text(message, color = Muted, fontSize = 11.sp, maxLines = 1)
                            }
                            IconButton(onClick = { selected?.let { ch -> favorites = if (favorites.contains(ch.url)) favorites - ch.url else favorites + ch.url } }) {
                                Icon(Icons.Default.Favorite, null, tint = if (selected != null && favorites.contains(selected!!.url)) Color(0xFFFF5577) else Muted)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("PLAYLIST SOURCE", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(value = playlistUrl, onValueChange = { playlistUrl = it }, modifier = Modifier.weight(1f),
                                placeholder = { Text("https://example.com/playlist.m3u") }, singleLine = true)
                            Spacer(Modifier.width(8.dp))
                            Button(enabled = !loading && playlistUrl.isNotBlank(), onClick = {
                                loading = true
                                message = "Loading playlist…"
                                kotlinx.coroutines.MainScope().launch {
                                    try {
                                        val loaded = withContext(Dispatchers.IO) { parseM3u(URL(playlistUrl).readText()) }
                                        channels = loaded
                                        category = "All"
                                        message = "Loaded ${loaded.size} channels"
                                    } catch (e: Exception) { message = "Could not load playlist. Check URL and network." }
                                    loading = false
                                }
                            }) { Text(if (loading) "Loading" else "Load") }
                        }
                    }
                    Column(Modifier.weight(0.8f).fillMaxHeight().clip(RoundedCornerShape(18.dp)).background(Panel).padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("CHANNELS", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.weight(1f))
                            Text("${visible.size}", color = Accent, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(8.dp))
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            items(visible) { channel ->
                                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                                    .background(if (selected == channel) Color(0xFF153B60) else Color(0xFF172335))
                                    .clickable {
                                        selected = channel
                                        player.setMediaItem(MediaItem.fromUri(channel.url))
                                        player.prepare(); player.play()
                                        message = "Now playing • ${channel.group}"
                                    }.focusable().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF263B55)), contentAlignment = Alignment.Center) {
                                        Text(channel.name.take(1).uppercase(), color = Accent, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(Modifier.width(9.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(channel.name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                        Text(channel.group, color = Muted, fontSize = 10.sp, maxLines = 1)
                                    }
                                    if (favorites.contains(channel.url)) Icon(Icons.Default.Star, null, tint = Color(0xFFFFC857), modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun parseM3u(text: String): List<Channel> {
    val result = mutableListOf<Channel>()
    var name = "Channel"
    var group = "Live TV"
    var logo = ""
    text.lineSequence().forEach { raw ->
        val line = raw.trim()
        if (line.startsWith("#EXTINF", true)) {
            name = line.substringAfterLast(",", "Channel").trim().ifBlank { "Channel" }
            group = Regex("group-title=\"([^\"]+)\"", RegexOption.IGNORE_CASE).find(line)?.groupValues?.get(1) ?: "Live TV"
            logo = Regex("tvg-logo=\"([^\"]+)\"", RegexOption.IGNORE_CASE).find(line)?.groupValues?.get(1) ?: ""
        } else if (line.isNotBlank() && !line.startsWith("#") && (line.startsWith("http://") || line.startsWith("https://"))) {
            result.add(Channel(name, line, group, logo))
        }
    }
    return result
}

private val sampleChannels = listOf(
    Channel("Welcome to StreamBox", "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8", "Live TV"),
    Channel("Sample Sports Stream", "https://storage.googleapis.com/shaka-demo-assets/angel-one-hls/hls.m3u8", "Sports")
)
