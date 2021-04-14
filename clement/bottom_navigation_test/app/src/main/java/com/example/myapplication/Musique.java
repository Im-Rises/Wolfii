package com.example.myapplication;

// plain old java object (POJO)

public class Musique {
    private String name;
    private String path;
    private String author;

    Musique(String name, String path) {
        this.name = name;
        this.path = path;
    }

    // GETTER
    public String getName() {return name;}
    public String getPath() {return path;}
}
