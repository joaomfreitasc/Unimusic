package com.project.unimusic.dto;

import com.project.unimusic.entidades.Musica;

public class MusicaResponseDTO {
    private MusicaDTO musica;

    public MusicaResponseDTO(Musica musica) {
        this.musica = new MusicaDTO();
        this.musica.setId(musica.getId());
        this.musica.setTitulo(musica.getTitulo());
        this.musica.setDuracao(musica.getDuracao());

        ArtistaDTO artista = new ArtistaDTO();
        artista.setId(musica.getArtista().getId());
        artista.setNome(musica.getArtista().getNome());
        this.musica.setArtista(artista);

        AlbumDTO album = new AlbumDTO();
        album.setId(musica.getAlbum().getId());
        album.setTitulo(musica.getAlbum().getTitulo());
        album.setDataDeLancamento(musica.getAlbum().getDataDeLancamento());
        this.musica.setAlbum(album);
    }

    public MusicaDTO getMusicaDTO() {
        return musica;
    }

    public void setMusicaDTO(MusicaDTO musicaDTO) {
        this.musica = musicaDTO;
    }
}
