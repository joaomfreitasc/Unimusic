package br.unibh.sdm.unimusic_music.entidades;

public class Favorite {
    private String userId;
    private String musicId;

    public Favorite() {
    }
    public Favorite(String userId, String musicId) {
        this.userId = userId;
        this.musicId = musicId;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getMusicId() {
        return musicId;
    }
    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        result = prime * result + ((musicId == null) ? 0 : musicId.hashCode());
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
        Favorite other = (Favorite) obj;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        if (musicId == null) {
            if (other.musicId != null)
                return false;
        } else if (!musicId.equals(other.musicId))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "Favorite [userId=" + userId + ", musicId=" + musicId + "]";
    }
}
