package com.example.unimusicapp

data class LoginDTO(val nomeUsuario: String, val senha: String)
data class RegisterDTO(val nomeUsuario: String, val email: String, val senha: String)

data class UsuarioDTO(
    val id: String,
    val nome: String,
    val email: String,
    val tipoUsuario: String?
)

data class MusicaResponse(val musicaDTO: MusicaDTO)

data class MusicaDTO(
    val id: String,
    val titulo: String,
    val duracao: Double,
    val artista: ArtistaDTO?,
    val album: AlbumDTO?,
    val artistaNome: String? = null,
    val albumTitulo: String? = null
) {
    fun getSafeArtist(): String = artista?.nome ?: artistaNome ?: "Artista Desconhecido"
    fun getSafeAlbum(): String = album?.titulo ?: albumTitulo ?: "Álbum Desconhecido"
}

data class ArtistaDTO(val id: String, val nome: String, val genero: String?)
data class AlbumDTO(val id: String, val titulo: String, val anoLancamento: Int?)

data class PlaylistResponse(
    val playlistDTO: PlaylistDTO? = null,
    val id: String? = null,
    val nome: String? = null,
    val musicas: List<MusicaDTO>? = emptyList()
)

data class PlaylistDTO(
    val id: String,
    val nome: String?,
    val musicas: List<MusicaDTO>? = emptyList()
)

data class PlaylistCreateDTO(val nome: String, val usuarioId: String)
data class AddMusicaDTO(val musicaId: String, val titulo: String, val artistaNome: String)
