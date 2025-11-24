package com.example.unimusicapp

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlayerController(private val context: Context, private val onNext: () -> Unit) {
    private var exoPlayer: ExoPlayer? = null

    init {
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            addListener(object : com.google.android.exoplayer2.Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == ExoPlayer.STATE_ENDED) onNext()
                }
            })
        }
    }

    suspend fun play(url: String) {
        withContext(Dispatchers.Main) {
            val player = exoPlayer ?: return@withContext
            val mediaItem = MediaItem.fromUri(url)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        }
    }

    suspend fun togglePlayPause() {
        withContext(Dispatchers.Main) {
            val player = exoPlayer ?: return@withContext
            if (player.isPlaying) player.pause() else player.play()
        }
    }

    suspend fun release() {
        withContext(Dispatchers.Main) {
            exoPlayer?.release()
            exoPlayer = null
        }
    }

    fun isPlaying(): Boolean = exoPlayer?.isPlaying ?: false
}
