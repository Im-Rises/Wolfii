package com.example.wolfii;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

// define table name
@Entity(tableName = "playlists")
public class MainData implements Serializable {
    // create id column
    @PrimaryKey(autoGenerate = true)
    private int id;

    // create text column
    @ColumnInfo(name = "nomMusique")
    private String nomMusique;

    // create text column
    @ColumnInfo(name = "path")
    private String path;

    @ColumnInfo(name = "playlist")
    private String playlist;

    // GETTER
    public int getId() {
        return id;
    }
    public String getNomMusique () {return nomMusique;}
    public String getPath () {return path;}
    public String getPlaylist() {return playlist;}

    // SETTER
    public void setId(int id) {
        this.id = id;
    }
    public void setNomMusique (String nomMusique) {this.nomMusique = nomMusique;}
    public void setPath (String path) {this.path = path;}
    public void setPlaylist(String playlist) {this.playlist=playlist;}
}
