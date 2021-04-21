package com.example.wolfii;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.example.wolfii.MainActivity.database;

public class ListAllArtistsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerViewPlaylist;
    private ArrayList<String> mesArtistes;
    private MyArtisteAdapter monArtisteAdapter;
    private MyArtisteAdapter monPlaylistAdapter;
    private MyMusiqueAdapter monMusiqueAdapter;
    private ArrayList<Musique> musiques;

    //private ArrayList<MusiquesData> dataList = new ArrayList<> ();
    //private RoomDB database;

    @SuppressLint("WrongConstant")
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate (R.layout.fragment_mes_artistes, container, false);

        ////////////////////////// ARTISTES /////////////////////////////
        // creation du recyclerview
        mRecyclerView = (RecyclerView) root.findViewById (R.id.myRecyclerView);
        mesArtistes = MainActivity.mesArtistes; // on recupere ici toutes les musiques sous forme d'un tableau

        monArtisteAdapter = new MyArtisteAdapter (mesArtistes);
        MainActivity.ClickOnArtist clickOnArtist = new MainActivity.ClickOnArtist ();
        clickOnArtist.setRecyclerViewForMusic (mRecyclerView);
        monArtisteAdapter.setmArtisteItemClickListener (clickOnArtist);
        mRecyclerView.setLayoutManager (new LinearLayoutManager (getActivity ().getApplicationContext (), LinearLayout.VERTICAL, false));
        mRecyclerView.setAdapter (monArtisteAdapter);

        ////////////////////// PLAYLISTS ////////////////////////////
        // on recupere toutes les donnees de la base de donnees et on cree son adapter
        List<MainData> dataList = database.mainDao ().getAll ();
        ArrayList<String> playlists = new ArrayList<String> ();

        // on recupere toutes les playlists
        for (MainData m : dataList)
            if (! playlists.contains (m.getPlaylist ())) playlists.add (m.getPlaylist ());
        mRecyclerViewPlaylist = (RecyclerView) root.findViewById (R.id.recyclerview_playlists);
        monPlaylistAdapter = new MyArtisteAdapter (playlists);

        mRecyclerViewPlaylist.setLayoutManager (new LinearLayoutManager (getActivity ().getApplicationContext (), LinearLayout.VERTICAL, false));
        MainActivity.ClickOnPlaylist clickOnPlaylist = new MainActivity.ClickOnPlaylist ();
        clickOnPlaylist.setPlaylists (playlists);
        monPlaylistAdapter.setmArtisteItemClickListener (clickOnPlaylist);
        mRecyclerViewPlaylist.setAdapter (monPlaylistAdapter);

        return root;
    }

}

