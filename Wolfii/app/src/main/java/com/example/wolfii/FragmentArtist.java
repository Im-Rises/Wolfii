package com.example.wolfii;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.example.wolfii.MainActivity.mesArtistes;

public class FragmentArtist extends FragmentsWithReturn {
    private RecyclerView mRecyclerView;
    private MyMusiqueAdapter monAdapter;
    private String artiste;
    private  ArrayList<Musique> musiques = new ArrayList<> ();
    private MyStringAdapter monArtisteAdapter;
    private ImageView shuffle;

    FragmentArtist(){}

    @SuppressLint({"WrongConstant", "SetTextI18n"})
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.simple_list, container, false);

        ////////////////////////// ARTISTES /////////////////////////////
        // creation du recyclerview
        mRecyclerView = root.findViewById (R.id.myRecyclerView);

        shuffle = root.findViewById (R.id.shuffle);
        shuffle.setVisibility (View.INVISIBLE);

        monArtisteAdapter = new MyStringAdapter (mesArtistes);

        ClickOnArtist clickOnArtist = new ClickOnArtist ();
        clickOnArtist.setContext (getActivity ());
        clickOnArtist.setRecyclerViewForMusic (mRecyclerView);
        clickOnArtist.setShuffle (shuffle);

        monArtisteAdapter.setmArtisteItemClickListener (clickOnArtist);
        mRecyclerView.setLayoutManager (new LinearLayoutManager (getActivity ().getApplicationContext (), LinearLayout.VERTICAL, false));
        mRecyclerView.setAdapter (monArtisteAdapter);

        return root;

    }
}
