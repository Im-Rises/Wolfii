package com.example.wolfii;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import static com.example.wolfii.MainActivity.database;
import static com.example.wolfii.MainActivity.mesGenres;


public class NouveautesFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private MyStringAdapter monAdapter;
    private ImageView shuffle;


    public NouveautesFragment () {
        // Required empty public constructor
    }


    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate (R.layout.fragment_nouveautes, container, false);

        shuffle = root.findViewById (R.id.shuffle);
        shuffle.setVisibility (View.INVISIBLE);

        if(!mesGenres.contains ("Download")) mesGenres.add("Download");

        mRecyclerView= root.findViewById (R.id.myRecyclerView);
        monAdapter = new MyStringAdapter (mesGenres, getActivity ());
        monAdapter.setIsGenre (true);
        mRecyclerView.setLayoutManager (new GridLayoutManager (getActivity (), 2));

        ClickOnGenre clickOnGenre = new ClickOnGenre ();
        clickOnGenre.setRecyclerViewForMusic (mRecyclerView);
        clickOnGenre.setContext (getActivity ());
        clickOnGenre.setShuffle (shuffle);
        monAdapter.setmArtisteItemClickListener (clickOnGenre);

        mRecyclerView.setAdapter (monAdapter);

        return root;
    }
}