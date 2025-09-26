package br.unibh.sdm.unimusic_music.entidades;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "ZFAVORITO")
public class Favorito {
    private String userId;
    private String musicId;

    public Favorito() {
    }
    public Favorito(String userId, String musicId) {
        this.userId = userId;
        this.musicId = musicId;
    }

    @DynamoDBHashKey
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBHashKey
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
        Favorito other = (Favorito) obj;
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
        return "Favorito [userId=" + userId + ", musicId=" + musicId + "]";
    }
}
