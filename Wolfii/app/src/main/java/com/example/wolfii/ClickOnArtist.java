package com.example.wolfii;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.example.wolfii.MainActivity.maMusique;

public class ClickOnArtist implements MyStringAdapter.ArtisteItemClickListener {

    private MyMusiqueAdapter monMusiqueAdapter;
    private ArrayList<Musique> musiques;
    private RecyclerView mRecyclerView;
    private Context context;
    public void setRecyclerViewForMusic(RecyclerView rv) { mRecyclerView = rv; }
    public void setContext(Context sContext){context = sContext;}

    @Override
    public void onArtisteItemClick (View view, String artiste, int position) {
        musiques = recuperer_musique (artiste);

        monMusiqueAdapter = new MyMusiqueAdapter (musiques, context);
        ClickOnMusic clicker = new ClickOnMusic();
        clicker.setMesMusiques (musiques);
        monMusiqueAdapter.setmMusiqueItemClickListener (clicker);
        mRecyclerView.setAdapter (monMusiqueAdapter);
    }

    @Override
    public void onArtisteItemLongClick (View view, String musique, int position) {

    }
    private ArrayList<Musique> recuperer_musique (String artiste) {
        // on recupere toutes les musiques selon l'artiste qui nous interesse
        ArrayList<Musique> musiques = new ArrayList<> ();
        for (Musique m : maMusique) if (m.getAuthor ().equals (artiste)) musiques.add (m);
        return musiques;
    }
}