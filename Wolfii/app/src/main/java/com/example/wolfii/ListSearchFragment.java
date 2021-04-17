package com.example.wolfii;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListSearchFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private MyMusiqueAdapter monAdapter;
    private boolean mBound = false;
    private MusiqueService mService;
    private String artiste;

    private ArrayList<Musique> musiques = new ArrayList<> ();
    ListSearchFragment(ArrayList<Musique> musiques, String artiste){
        this.musiques = musiques;
        this.artiste = artiste;
    }

    @SuppressLint({"WrongConstant", "SetTextI18n"})
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_liste_recherche, container, false);

        TextView textView = root.findViewById (R.id.search_artiste);
        textView.setText ("resultat(s) pour : \"" + this.artiste + "\"");
        // creation du recyclerview
        mRecyclerView = (RecyclerView) root.findViewById(R.id.myRecyclerView);

        monAdapter = new MyMusiqueAdapter (this.musiques);
        monAdapter.setmMusiqueItemClickListener(new MyMusiqueAdapter.MusiqueItemClickListener() {

            @Override
            public void onMusiqueItemClick (View view, Musique musique, int position) {

            }

            @Override
            public void onMusiqueItemLongClick (View view, Musique musique, int position) {

            }


        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager (getActivity().getApplicationContext(), LinearLayout.VERTICAL, false));
        mRecyclerView.setAdapter(monAdapter);

        return root;

    }
}
