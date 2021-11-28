package com.example.wolfii;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class ClickOnShuffle implements View.OnClickListener{
    private ArrayList musiques;
    private Context context;
    private MyMusiqueAdapter monMusiqueAdapter;
    private RecyclerView mRecyclerView;

    // SETTER
    public void setPlaylist(ArrayList playlist){this.musiques = playlist;}
    public void setContext(Context context) {this.context = context;}
    public void setmRecyclerView(RecyclerView rv) {this.mRecyclerView = rv;}

    public ArrayList shuffle(ArrayList playlist) {
        Collections.shuffle (playlist);
        return playlist;
    }

    @Override
    public void onClick (View v) {

        musiques = shuffle (musiques);

        monMusiqueAdapter = new MyMusiqueAdapter (musiques, context);
        ClickOnMusic clicker = new ClickOnMusic();
        clicker.setMesMusiques (musiques);
        clicker.setContext (context);
        monMusiqueAdapter.setmMusiqueItemClickListener (clicker);
        mRecyclerView.setAdapter (monMusiqueAdapter);
    }
}
