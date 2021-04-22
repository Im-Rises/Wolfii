package com.example.wolfii;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.example.wolfii.MainActivity.mesArtistes;

public class ArtistFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private MyMusiqueAdapter monAdapter;
    private String artiste;
    private  ArrayList<Musique> musiques = new ArrayList<> ();
    private MyStringAdapter monArtisteAdapter;

    ArtistFragment(){}

    @SuppressLint({"WrongConstant", "SetTextI18n"})
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_liste_recherche, container, false);

        ////////////////////////// ARTISTES /////////////////////////////
        // creation du recyclerview
        mRecyclerView = (RecyclerView) root.findViewById (R.id.myRecyclerView);

        monArtisteAdapter = new MyStringAdapter (mesArtistes, getActivity (), false);
        ClickOnArtist clickOnArtist = new ClickOnArtist ();
        clickOnArtist.setContext (getActivity ());
        clickOnArtist.setRecyclerViewForMusic (mRecyclerView);
        monArtisteAdapter.setmArtisteItemClickListener (clickOnArtist);
        mRecyclerView.setLayoutManager (new LinearLayoutManager (getActivity ().getApplicationContext (), LinearLayout.VERTICAL, false));
        mRecyclerView.setAdapter (monArtisteAdapter);

        return root;

    }
}
