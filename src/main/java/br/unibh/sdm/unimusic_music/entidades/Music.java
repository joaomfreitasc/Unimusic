package br.unibh.sdm.unimusic_music.entidades;

public class Music {
    private Long id;
    private String titulo;
    private String artista;
    private String album;
    private String genero;
    private Integer duracaoSegundos;
    private String urlArquivo;

    public Music() {}
    
    public Music(Long id, String titulo, String artista, String album, String genero, Integer duracaoSegundos,
            String urlArquivo) {
        this.id = id;
        this.titulo = titulo;
        this.artista = artista;
        this.album = album;
        this.genero = genero;
        this.duracaoSegundos = duracaoSegundos;
        this.urlArquivo = urlArquivo;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getArtista() {
        return artista;
    }
    public void setArtista(String artista) {
        this.artista = artista;
    }
    public String getAlbum() {
        return album;
    }
    public void setAlbum(String album) {
        this.album = album;
    }
    public String getGenero() {
        return genero;
    }
    public void setGenero(String genero) {
        this.genero = genero;
    }
    public Integer getDuracaoSegundos() {
        return duracaoSegundos;
    }
    public void setDuracaoSegundos(Integer duracaoSegundos) {
        this.duracaoSegundos = duracaoSegundos;
    }
    public String getUrlArquivo() {
        return urlArquivo;
    }
    public void setUrlArquivo(String urlArquivo) {
        this.urlArquivo = urlArquivo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((titulo == null) ? 0 : titulo.hashCode());
        result = prime * result + ((artista == null) ? 0 : artista.hashCode());
        result = prime * result + ((album == null) ? 0 : album.hashCode());
        result = prime * result + ((genero == null) ? 0 : genero.hashCode());
        result = prime * result + ((duracaoSegundos == null) ? 0 : duracaoSegundos.hashCode());
        result = prime * result + ((urlArquivo == null) ? 0 : urlArquivo.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Music other = (Music) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (titulo == null) {
            if (other.titulo != null)
                return false;
        } else if (!titulo.equals(other.titulo))
            return false;
        if (artista == null) {
            if (other.artista != null)
                return false;
        } else if (!artista.equals(other.artista))
            return false;
        if (album == null) {
            if (other.album != null)
                return false;
        } else if (!album.equals(other.album))
            return false;
        if (genero == null) {
            if (other.genero != null)
                return false;
        } else if (!genero.equals(other.genero))
            return false;
        if (duracaoSegundos == null) {
            if (other.duracaoSegundos != null)
                return false;
        } else if (!duracaoSegundos.equals(other.duracaoSegundos))
            return false;
        if (urlArquivo == null) {
            if (other.urlArquivo != null)
                return false;
        } else if (!urlArquivo.equals(other.urlArquivo))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Music [id=" + id + ", titulo=" + titulo + ", artista=" + artista + ", album=" + album + ", genero="
                + genero + ", duracaoSegundos=" + duracaoSegundos + ", urlArquivo=" + urlArquivo + "]";
    }


}
