package com.example.wolfii;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.example.wolfii.MainActivity.database;

public class ClickOnPlaylist implements MyArtisteAdapter.ArtisteItemClickListener {

    private MyMusiqueAdapter monMusiqueAdapter;
    private Context context;
    private ArrayList<Musique> musiques = new ArrayList<> ();
    private RecyclerView mRecyclerView;

    public void setRecyclerViewForMusic(RecyclerView rv) { mRecyclerView = rv; }

    @Override
    public void onArtisteItemClick (View view, String playlist, int position) {
        List<MainData> musiquesMainData = database.mainDao ().getMusicFromPlaylist (playlist);
        for(MainData data : musiquesMainData) {
            musiques.add(new Musique(data.getNomMusique (), data.getAuthor (), data.getPath (), data.getDuration (), data.getDateTaken ()));
        }

        monMusiqueAdapter = new MyMusiqueAdapter (musiques, context);
        ClickOnMusic clicker = new ClickOnMusic();
        clicker.setMesMusiques (musiques);
        clicker.setContext (context);
        monMusiqueAdapter.setmMusiqueItemClickListener (clicker);
        mRecyclerView.setAdapter (monMusiqueAdapter);
    }

    @Override
    public void onArtisteItemLongClick (View view, String artiste, int position) {

    }
}
