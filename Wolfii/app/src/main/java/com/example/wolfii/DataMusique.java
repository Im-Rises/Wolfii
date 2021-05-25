package com.example.wolfii;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Music")
public class DataMusique {

    @ColumnInfo(name = "path")
    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String path;

    @ColumnInfo(name = "nomMusique")
    private String nomMusique;

    @ColumnInfo(name = "playlist")
    private String playlist;

    @ColumnInfo(name = "author")
    private String author;

    @ColumnInfo(name = "duration")
    private String duration;

    @ColumnInfo(name = "dateTaken")
    private String dateTaken;

    @ColumnInfo(name = "genre")
    private String genre;

    @ColumnInfo(name = "album")
    private String album;


    // GETTER
    public String getNomMusique () {return nomMusique;}
    public String getPath () {return path;}
    public String getPlaylist() {return playlist;}
    public String getAuthor() {return author;}
    public String getDuration() {return duration;}
    public String getDateTaken() {return dateTaken;}
    public String getGenre() {return genre;}
    public String getAlbum() {return album;}

    // SETTER
    public void setNomMusique (String nomMusique) {this.nomMusique = nomMusique;}
    public void setPath (String path) {this.path = path;}
    public void setPlaylist(String playlist) {this.playlist=playlist;}
    public void setAuthor(String author) {this.author = author;}
    public void setDuration(String duration) {this.duration = duration;}
    public void setDateTaken(String dateTaken) {this.dateTaken = dateTaken;}
    public void setGenre(String genre) {this.genre = genre;}
    public void setAlbum(String album) {this.album = album;}

}
