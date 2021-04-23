package com.example.wolfii;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

// define table name
@Entity(tableName = "Wolfii")
public class MainData implements Serializable {
    // create id column

    //String name, String author, String path, String duration, String dateTaken
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

    @ColumnInfo(name = "author")
    private String author;

    @ColumnInfo(name = "duration")
    private String duration;

    @ColumnInfo(name = "dateTaken")
    private String dateTaken;

    @ColumnInfo(name = "genre")
    private String genre;


    // GETTER
    public int getId() {
        return id;
    }
    public String getNomMusique () {return nomMusique;}
    public String getPath () {return path;}
    public String getPlaylist() {return playlist;}
    public String getAuthor() {return author;}
    public String getDuration() {return duration;}
    public String getDateTaken() {return dateTaken;}
    public String getGenre() {return genre;}

    // SETTER
    public void setId(int id) {
        this.id = id;
    }
    public void setNomMusique (String nomMusique) {this.nomMusique = nomMusique;}
    public void setPath (String path) {this.path = path;}
    public void setPlaylist(String playlist) {this.playlist=playlist;}
    public void setAuthor(String author) {this.author = author;}
    public void setDuration(String duration) {this.duration = duration;}
    public void setDateTaken(String dateTaken) {this.dateTaken = dateTaken;}
    public void setGenre(String genre) {this.genre = genre;}
}
