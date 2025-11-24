package com.example.unimusicapp

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException


class UnimusicViewModel : ViewModel() {
    private val _currentUser = MutableStateFlow<UsuarioDTO?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _musicas = MutableStateFlow<List<MusicaDTO>>(emptyList())
    val musicas = _musicas.asStateFlow()

    private val _playlists = MutableStateFlow<List<PlaylistDTO>>(emptyList())
    val playlists = _playlists.asStateFlow()

    private val _currentSong = MutableStateFlow<MusicaDTO?>(null)
    val currentSong = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private var playbackQueue: List<MusicaDTO> = emptyList()
    private var currentQueueIndex = -1

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var playerController: PlayerController? = null

    fun initPlayer(context: Context) {
        if (playerController == null) {
            playerController = PlayerController(context) { playNext() }
        }
    }

    fun login(username: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = NetworkModule.mainApi.login(LoginDTO(username, pass))
                _currentUser.value = user
                fetchContent()
            } catch (e: Exception) {
                handleAuthError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(username: String, email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                NetworkModule.mainApi.registrar(RegisterDTO(username, email, pass))
                login(username, pass)
            } catch (e: Exception) {
                _error.value = "Falha no registro: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    private fun handleAuthError(e: Exception) {
        if (e is HttpException) {
            _error.value = when (e.code()) {
                401 -> "Usuário ou senha inválidos"
                404 -> "Serviço de login não encontrado."
                else -> "Erro do servidor (${e.code()})."
            }
        } else {
            _error.value = "Erro de rede. Verifique a conexão."
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.Main) {
            playerController?.release()
            playerController = null
            _currentSong.value = null
            _isPlaying.value = false
            _currentUser.value = null
        }
    }

    fun fetchContent() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = NetworkModule.mainApi.getAllMusicas()
                val songs = response.map { it.musicaDTO }
                _musicas.value = songs
                fetchPlaylists()
            } catch (e: Exception) {
                _error.value = "Falha ao carregar músicas."
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchPlaylists() {
        currentUser.value?.id?.let { userId ->
            try {
                val response = NetworkModule.playlistApi.getPlaylistsByUserId(userId)
                val rawPlaylists = response.mapNotNull { it.playlistDTO ?: PlaylistDTO(it.id ?: "", it.nome, it.musicas) }
                val fullSongs = _musicas.value

                val fixedPlaylists = rawPlaylists.map { playlist ->
                    val fixedSongs = playlist.musicas?.map { song ->
                        fullSongs.find { it.id == song.id } ?: song
                    } ?: emptyList()
                    playlist.copy(musicas = fixedSongs)
                }
                _playlists.value = fixedPlaylists
            } catch (e: Exception) {
                Log.e("VM", "Err playlists", e)
            }
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            currentUser.value?.id?.let { userId ->
                try {
                    NetworkModule.playlistApi.createPlaylist(PlaylistCreateDTO(name, userId))
                    fetchPlaylists()
                } catch (e: Exception) {
                    _error.value = "Falha ao criar playlist"
                }
            }
        }
    }

    fun deletePlaylist(playlistId: String?) {
        viewModelScope.launch {
            try {
                NetworkModule.playlistApi.deletePlaylist(playlistId)
                fetchPlaylists()
            } catch (e: Exception) {
                _error.value = "Falha ao deletar playlist"
            }
        }
    }

    fun addSongToPlaylist(playlistId: String?, song: MusicaDTO) {
        viewModelScope.launch {
            try {
                NetworkModule.playlistApi.addMusicaToPlaylist(
                    playlistId,
                    AddMusicaDTO(song.id, song.titulo, song.getSafeArtist())
                )
                fetchPlaylists()
                _error.value = "Música adicionada à playlist!"
            } catch (e: Exception) {
                _error.value = "Falha ao adicionar música."
            }
        }
    }

    fun removeSongFromPlaylist(playlistId: String?, songId: String) {
        viewModelScope.launch {
            try {
                NetworkModule.playlistApi.removeMusicaFromPlaylist(playlistId, songId)
                fetchPlaylists()
            } catch (e: Exception) {
                _error.value = "Falha ao remover música."
            }
        }
    }

    fun playSong(song: MusicaDTO, queue: List<MusicaDTO> = _musicas.value) {
        if (playerController == null) return

        viewModelScope.launch {
            playbackQueue = queue
            currentQueueIndex = playbackQueue.indexOfFirst { it.id == song.id }
            if (currentQueueIndex == -1) {
                playbackQueue = listOf(song)
                currentQueueIndex = 0
            }

            _currentSong.value = song
            _isPlaying.value = true

            val artist = Uri.encode(song.getSafeArtist())
            val album = Uri.encode(song.getSafeAlbum())
            val title = Uri.encode(song.titulo)
            val streamUrl = "${ApiConfigs.BASE_URL_MAIN}musicas/stream/$artist/$album/$title"

            playerController?.play(streamUrl)
        }
    }

    fun playNext() {
        if (playbackQueue.isNotEmpty() && currentQueueIndex < playbackQueue.size - 1) {
            playSong(playbackQueue[currentQueueIndex + 1], playbackQueue)
        }
    }

    fun playPrevious() {
        if (playbackQueue.isNotEmpty() && currentQueueIndex > 0) {
            playSong(playbackQueue[currentQueueIndex - 1], playbackQueue)
        }
    }

    fun togglePlayPause() {
        viewModelScope.launch {
            playerController?.togglePlayPause()
            _isPlaying.value = playerController?.isPlaying() ?: false
        }
    }

    fun getCoverUrl(song: MusicaDTO?): String {
        song ?: return ""
        val artist = Uri.encode(song.getSafeArtist())
        val album = Uri.encode(song.getSafeAlbum())
        val title = Uri.encode(song.titulo)
        return "${ApiConfigs.BASE_URL_MAIN}musicas/capa/$artist/$album/$title"
    }

    fun clearError() { _error.value = null }
}
