package com.example.musique_affichage_recycler_view;

public class JeuVideo {
    private String name;
    private float price;

    JeuVideo(String name, float price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {return name;}
    public float getPrice() {return price;}

}
