package com.example.wolfii;

import java.util.ArrayList;

public class Artiste {
    private String nom;
    private ArrayList<Musique> musiques = new ArrayList<>();
    Artiste(String name) {
        this.nom = name;
    }
    public void addMusique(Musique m) {
        musiques.add(m);
    }
    public String getNom() {
        return nom;
    }
}
