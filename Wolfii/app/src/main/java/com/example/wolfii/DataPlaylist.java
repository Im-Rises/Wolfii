package com.example.wolfii;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Playlist")
public class DataPlaylist {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "nom")
    @NonNull
    private String nom;

    public String getNom () {
        return nom;
    }

    public void setNom (String nom) {
        this.nom = nom;
    }
}
