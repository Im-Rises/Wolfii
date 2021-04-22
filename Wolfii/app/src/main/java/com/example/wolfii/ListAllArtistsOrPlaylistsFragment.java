package com.example.wolfii;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.example.wolfii.MainActivity.database;

public class ListAllArtistsOrPlaylistsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerViewPlaylist;
    private ArrayList<String> mesArtistes;
    private MyArtisteAdapter monArtisteAdapter;
    private MyArtisteAdapter monPlaylistAdapter;
    private MyMusiqueAdapter monMusiqueAdapter;
    private ArrayList<Musique> musiques;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    //private ArrayList<MusiquesData> dataList = new ArrayList<> ();
    //private RoomDB database;

    @SuppressLint("WrongConstant")
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate (R.layout.fragment_mes_artistes, container, false);
        fragmentManager = getActivity ().getSupportFragmentManager ();

        ArtistFragment artistFragment = new ArtistFragment ();
        Fragment playlistFragment = database.mainDao ().getAll ().isEmpty () ? new NoPlaylistsFragment () : new PlaylistFragment ();

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.listes, artistFragment, null);
        fragmentTransaction.commit ();

        Button artistes = root.findViewById (R.id.mes_artistes);
        Button playlists = root.findViewById (R.id.mes_playlists);

        artistes.setOnClickListener(new View.OnClickListener () {
            public void onClick(View v) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.listes, artistFragment, null);
                fragmentTransaction.commit ();
            }
        });
        playlists.setOnClickListener(new View.OnClickListener () {
            public void onClick(View v) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.listes, playlistFragment, null);
                fragmentTransaction.commit ();
            }
        });

        return root;
    }

}

