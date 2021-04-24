package com.example.wolfii;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.example.wolfii.MainActivity.mesAlbums;
import static com.example.wolfii.MainActivity.mesArtistes;

public class AlbumFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private MyMusiqueAdapter monAdapter;
    private String artiste;
    private  ArrayList<Musique> musiques = new ArrayList<> ();
    private MyStringAdapter monArtisteAdapter;

    AlbumFragment (){}

    @SuppressLint({"WrongConstant", "SetTextI18n"})
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_liste_recherche, container, false);

        ////////////////////////// ARTISTES /////////////////////////////
        // creation du recyclerview
        mRecyclerView = (RecyclerView) root.findViewById (R.id.myRecyclerView);

        monArtisteAdapter = new MyStringAdapter (mesAlbums, getActivity ());
        ClickOnAlbum clickOnAlbum = new ClickOnAlbum ();
        clickOnAlbum.setContext (getActivity ());
        clickOnAlbum.setRecyclerViewForMusic (mRecyclerView);
        monArtisteAdapter.setmArtisteItemClickListener (clickOnAlbum);
        monArtisteAdapter.setIsAlbum (true);
        mRecyclerView.setLayoutManager (new GridLayoutManager (getActivity (), 2));
        mRecyclerView.setAdapter (monArtisteAdapter);

        return root;

    }
}
