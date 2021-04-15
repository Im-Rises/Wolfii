package com.example.musique_rv;

import java.util.ArrayList;

public class Artiste {
    private String name;
    private ArrayList<Musique> musiques = new ArrayList<Musique>();

    Artiste(String n) {
        name = n;
    }
    public void addMusique(Musique m) {
        musiques.add(m);
    }

    //--------------- GETTER -----------------------------------------------------------------------
    public String getName() {
        return name;
    }
    public ArrayList<Musique> getMusiques() {
        return musiques;
    }
    //---------------- SETTER ----------------------------------------------------------------------

    public void setName(String name) {
        this.name = name;
    }
}
