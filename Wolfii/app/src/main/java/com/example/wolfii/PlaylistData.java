package com.example.wolfii;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

// define table name
@Entity(tableName = "playlist")
public class PlaylistData implements Serializable {
    // create id column

    //String name, String author, String path, String duration, String dateTaken
    @PrimaryKey(autoGenerate = true)
    private int id;

    // create text column
    @ColumnInfo(name = "nom")
    private String nom;

    public int getId () {
        return id;
    }

    public void setId (int id) {
        this.id = id;
    }

    public String getNom () {
        return nom;
    }

    public void setNom (String playlist) {
        this.nom = playlist;
    }
}
