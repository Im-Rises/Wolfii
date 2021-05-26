package com.example.wolfii;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.example.wolfii.FragmentListArtistPlaylistAlbum.clickOnRetour;
import static com.example.wolfii.MainActivity.database;

public class FragmentPlaylist extends FragmentsWithReturn {
    private RecyclerView mRecyclerView;
    private MyStringAdapter monAdapter;
    private Button newPlaylist;
    private ImageView shuffle;

    FragmentPlaylist(){}

    @SuppressLint({"WrongConstant", "SetTextI18n"})
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_playlist, container, false);

        ////////////////////// PLAYLISTS ////////////////////////////
        // on recupere toutes les donnees de la base de donnees et on cree son adapter
        ArrayList<String> playlists = (ArrayList<String>) database.mainDao ().getAllPlaylists ();

        // on desactive les boutons qui ne nous servent à rien ici
        shuffle = root.findViewById (R.id.shuffle);
        shuffle.setVisibility (View.INVISIBLE);

        retour.setVisibility (View.INVISIBLE);
        retour.setOnClickListener (clickOnRetour);


        newPlaylist = root.findViewById (R.id.new_playlist);
        ClickOnNewPlaylist clickOnNewPlaylist = new ClickOnNewPlaylist ();
        clickOnNewPlaylist.setContext (getActivity ());
        newPlaylist.setOnClickListener (clickOnNewPlaylist);


        mRecyclerView= (RecyclerView) root.findViewById (R.id.myRecyclerView);
        monAdapter = new MyStringAdapter (playlists);
        monAdapter.setIsPlaylist (true);

        mRecyclerView.setLayoutManager (new LinearLayoutManager (getActivity ().getApplicationContext (), LinearLayout.VERTICAL, false));
        ClickOnPlaylist clickOnPlaylist = new ClickOnPlaylist ();
        clickOnPlaylist.setRecyclerViewForMusic (mRecyclerView);
        clickOnPlaylist.setContext (getActivity ());
        clickOnPlaylist.setRetour (retour);
        clickOnPlaylist.setShuffle (shuffle);
        monAdapter.setmArtisteItemClickListener (clickOnPlaylist);
        mRecyclerView.setAdapter (monAdapter);

        return root;
    }
}
