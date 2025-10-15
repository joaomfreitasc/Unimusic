package br.unibh.sdm.unimusic_playlist.dto;

import java.util.UUID;

public class MusicaWrapperDTO {
    private MusicaInterna musicaDTO;

    public MusicaInterna getMusicaDTO() { 
        return musicaDTO; 
    }
    public void setMusicaDTO(MusicaInterna musicaDTO) { 
        this.musicaDTO = musicaDTO; 
    }

    public static class MusicaInterna {
        private UUID id;
        private String titulo;
        private Artista artista;

        public UUID getId() {
            return id; 
        }
        public void setId(UUID id) { 
            this.id = id; 
        }
        public String getTitulo() { 
            return titulo; 
        }
        public void setTitulo(String titulo) { 
            this.titulo = titulo;
        }
        public Artista getArtista() { 
            return artista; 
        }
        public void setArtista(Artista artista) { 
            this.artista = artista; 
        }
        public static class Artista {
            private String nome;

            public String getNome() { 
                return nome; 
            }
            public void setNome(String nome) { 
                this.nome = nome;
            }
        }
    }
}