package com.example.wolfii;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.example.wolfii.MainActivity.mesAlbums;

public class FragmentAlbum extends Fragment {
    private RecyclerView mRecyclerView;
    private MyMusiqueAdapter monAdapter;
    private String artiste;
    private  ArrayList<Musique> musiques = new ArrayList<> ();
    private MyStringAdapter monArtisteAdapter;
    private ImageView shuffle;

    FragmentAlbum (){}

    @SuppressLint({"WrongConstant", "SetTextI18n"})
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.simple_list, container, false);

        ////////////////////////// ALBUMS /////////////////////////////
        // creation du recyclerview
        mRecyclerView = (RecyclerView) root.findViewById (R.id.myRecyclerView);
        shuffle = root.findViewById (R.id.shuffle);
        shuffle.setVisibility (View.INVISIBLE);


        monArtisteAdapter = new MyStringAdapter (mesAlbums);
        monArtisteAdapter.setIsAlbum (true);
        mRecyclerView.setLayoutManager (new GridLayoutManager (getActivity (), 2));

        ClickOnAlbum clickOnAlbum = new ClickOnAlbum ();
        clickOnAlbum.setContext (getActivity ());
        clickOnAlbum.setRecyclerViewForMusic (mRecyclerView);
        clickOnAlbum.setShuffle (shuffle);
        monArtisteAdapter.setmArtisteItemClickListener (clickOnAlbum);

        mRecyclerView.setAdapter (monArtisteAdapter);

        return root;

    }


}
