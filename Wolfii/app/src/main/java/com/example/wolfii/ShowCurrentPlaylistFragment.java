package com.example.wolfii;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class ShowCurrentPlaylistFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ArrayList<Musique> maMusique;
    private MyMusiqueAdapter monAdapter;
    private ImageView shuffleiv, reload, playPause, next, previous;
    

    public void setMaMusique(ArrayList<Musique> musiques) {maMusique = musiques;}

    @SuppressLint("WrongConstant")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_liste_recherche, container, false);
        //database.getInstance (getActivity ());
        // creation du recyclerview
        mRecyclerView = root.findViewById(R.id.myRecyclerView);

        shuffleiv = root.findViewById (R.id.shuffle);
        ClickOnShuffle shuffle = new ClickOnShuffle ();
        shuffle.setContext (getActivity ());
        shuffle.setmRecyclerView (mRecyclerView);
        shuffle.setPlaylist (maMusique);
        shuffleiv.setOnClickListener (shuffle);

        next = root.findViewById (R.id.next);
        previous = root.findViewById (R.id.previous);
        reload = root.findViewById (R.id.reload);
        playPause = root.findViewById (R.id.playPause);

        monAdapter = new MyMusiqueAdapter (maMusique, getActivity ());
        ClickOnMusic clickOnMusic = new ClickOnMusic ();
        clickOnMusic.setMesMusiques (maMusique);
        clickOnMusic.setContext (getActivity ());
        monAdapter.setmMusiqueItemClickListener(clickOnMusic);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayout.VERTICAL, false));
        mRecyclerView.setAdapter(monAdapter);

        return root;
    }
    private void rm(String path) {
        try {
            // delete the original file
            File file = new File(path);
            file.delete();
            Log.d("debug_delete", "ok");
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }
}