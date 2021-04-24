package com.example.wolfii;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListSearchFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private MyMusiqueAdapter monAdapter;
    private String artiste;
    private static ArrayList<Musique> musiques = new ArrayList<> ();
    private ImageView shuffleiv, next, previous, reload, playPause;

    ListSearchFragment(ArrayList<Musique> musiques, String artiste){
        this.musiques = musiques;
        this.artiste = artiste;
    }

    @SuppressLint({"WrongConstant", "SetTextI18n"})
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_liste_recherche, container, false);

        shuffleiv = root.findViewById (R.id.shuffle);
        shuffleiv.setVisibility (View.INVISIBLE);

        next = root.findViewById (R.id.next);
        next.setVisibility (View.INVISIBLE);

        previous = root.findViewById (R.id.previous);
        previous.setVisibility (View.INVISIBLE);

        reload = root.findViewById (R.id.reload);
        reload.setVisibility (View.INVISIBLE);

        playPause = root.findViewById (R.id.playPause);
        playPause.setVisibility (View.INVISIBLE);

        TextView textView = root.findViewById (R.id.search_artiste);
        textView.setText ("resultat(s) pour : \"" + this.artiste + "\"");
        // creation du recyclerview
        mRecyclerView = (RecyclerView) root.findViewById(R.id.myRecyclerView);

        monAdapter = new MyMusiqueAdapter (this.musiques, getActivity ());
        ClickOnMusic clickOnMusic = new ClickOnMusic ();
        clickOnMusic.setMesMusiques (this.musiques);
        clickOnMusic.setContext (getActivity ());
        monAdapter.setmMusiqueItemClickListener(clickOnMusic);

        mRecyclerView.setLayoutManager(new LinearLayoutManager (getActivity().getApplicationContext(), LinearLayout.VERTICAL, false));
        mRecyclerView.setAdapter(monAdapter);

        return root;

    }
}
