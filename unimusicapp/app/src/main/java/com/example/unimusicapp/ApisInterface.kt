package com.example.unimusicapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

object ApiConfigs {
    const val BASE_URL_MAIN = "http://13.217.210.20:32771/"
    const val BASE_URL_PLAYLIST = "http://3.215.135.252:32768/playlist-api/"
}

interface UnimusicMainApi {
    @POST("usuarios/login")
    suspend fun login(@Body request: LoginDTO): UsuarioDTO
    @POST("usuarios/registrar")
    suspend fun registrar(@Body request: RegisterDTO): UsuarioDTO
    @GET("musicas")
    suspend fun getAllMusicas(): List<MusicaResponse>
}

interface UnimusicPlaylistApi {
    @GET("playlist/usuario/{usuarioId}")
    suspend fun getPlaylistsByUserId(@Path("usuarioId") userId: String): List<PlaylistResponse>
    @POST("playlist")
    suspend fun createPlaylist(@Body request: PlaylistCreateDTO): PlaylistDTO
    @DELETE("playlist/{id}")
    suspend fun deletePlaylist(@Path("id") id: String?)
    @POST("playlist/{id}/musica")
    suspend fun addMusicaToPlaylist(@Path("id") playlistId: String?, @Body request: AddMusicaDTO)
    @DELETE("playlist/{id}/musica/{musicaId}")
    suspend fun removeMusicaFromPlaylist(@Path("id") playlistId: String?, @Path("musicaId") musicaId: String)
}

object NetworkModule {
    val mainApi: UnimusicMainApi by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfigs.BASE_URL_MAIN)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UnimusicMainApi::class.java)
    }
    val playlistApi: UnimusicPlaylistApi by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfigs.BASE_URL_PLAYLIST)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UnimusicPlaylistApi::class.java)
    }
}
