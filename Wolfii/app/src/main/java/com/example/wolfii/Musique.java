package com.example.wolfii;

// plain old java object (POJO)

import android.net.Uri;

import java.io.File;

public class Musique {
    private String name;
    private String path;
    private String author;
    private String duration;
    private String dateTaken;
    private String genre;

    Musique(String name, String author, String path, String duration, String dateTaken, String genre) {
        this.name = name;
        this.path = path;
        this.author = author;
        this.duration = duration;
        this.dateTaken = dateTaken;
        this.genre = genre;
    }

    // GETTER
    public String getName() {return name;}
    public String getPath() {return path;}
    public Uri getPathUri() {return Uri.fromFile(new File(path));}
    public String getDuration() {return duration;}
    public String getAuthor() {return author;}
    public String getDateTaken() {return dateTaken;}
    public String getGenre() {return genre;}
}
