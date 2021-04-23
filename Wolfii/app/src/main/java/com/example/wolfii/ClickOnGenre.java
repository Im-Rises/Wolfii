package com.example.wolfii;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.example.wolfii.MainActivity.mesMusiques;

public class ClickOnGenre implements MyStringAdapter.ArtisteItemClickListener {

    private MyMusiqueAdapter monMusiqueAdapter;
    private ArrayList<Musique> musiques;
    private RecyclerView mRecyclerView;
    private Context context;
    public void setRecyclerViewForMusic(RecyclerView rv) { mRecyclerView = rv; }
    public void setContext(Context sContext){context = sContext;}

    @SuppressLint("WrongConstant")
    @Override
    public void onArtisteItemClick (View view, String genre, int position) {
        musiques = !genre.equals ("Download")  ? recuperer_musique (genre) : mesMusiques;

        monMusiqueAdapter = new MyMusiqueAdapter (musiques, context);
        ClickOnMusic clicker = new ClickOnMusic();
        clicker.setMesMusiques (musiques);
        clicker.setContext (context);
        monMusiqueAdapter.setmMusiqueItemClickListener (clicker);
        mRecyclerView.setLayoutManager (new LinearLayoutManager (context.getApplicationContext(), LinearLayout.VERTICAL, false));
        mRecyclerView.setAdapter (monMusiqueAdapter);
    }

    @Override
    public void onArtisteItemLongClick (View view, String musique, int position) {

    }
    private ArrayList<Musique> recuperer_musique (String genre) {
        // on recupere toutes les musiques selon l'artiste qui nous interesse
        ArrayList<Musique> musiques = new ArrayList<> ();
        for (Musique m : mesMusiques) if (m.getGenre ().equals (genre)) musiques.add (m);
        return musiques;
    }
}