package com.example.wolfii;

// plain old java object (POJO)

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.File;

@Entity(tableName = "Music")
public class Musique {

    @ColumnInfo(name = "path")
    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String path;

    @ColumnInfo(name = "nomMusique")
    private String nomMusique;

    @ColumnInfo(name = "author")
    private String author;

    @ColumnInfo(name = "duration")
    private String duration;

    @ColumnInfo(name = "dateTaken")
    private String dateTaken;

    @ColumnInfo(name = "genre")
    private String genre;

    public String getNomMusique () {
        return nomMusique;
    }

    @ColumnInfo(name = "album")
    private String album;

    public void setPath (@NonNull String path) {
        this.path = path;
    }

    public void setNomMusique (String nomMusique) {
        this.nomMusique = nomMusique;
    }

    public void setAuthor (String author) {
        this.author = author;
    }

    public void setDuration (String duration) {
        this.duration = duration;
    }

    public void setDateTaken (String dateTaken) {
        this.dateTaken = dateTaken;
    }

    public void setGenre (String genre) {
        this.genre = genre;
    }

    public void setAlbum (String album) {
        this.album = album;
    }

    // GETTER
    public String getName() {return nomMusique;}
    public String getPath() {return path;}
    public Uri getPathUri() {return Uri.fromFile(new File(path));}
    public String getDuration() {return duration;}
    public String getAuthor() {return author;}
    public String getDateTaken() {return dateTaken;}
    public String getGenre() {return genre;}
    public String getAlbum() {return album;}
}
