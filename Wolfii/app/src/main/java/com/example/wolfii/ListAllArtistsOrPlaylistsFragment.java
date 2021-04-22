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

import static com.example.wolfii.MainActivity.database;

public class ListAllArtistsOrPlaylistsFragment extends Fragment {

    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    @SuppressLint("WrongConstant")
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate (R.layout.fragment_mes_artistes, container, false);
        fragmentManager = getActivity ().getSupportFragmentManager ();

        // on initialise nos fragments, on verifie que la base de donnees n'est pas vide pour lui
        // donner un fragment en consequence
        ArtistFragment artistFragment = new ArtistFragment ();
        Fragment playlistFragment = database.mainDao ().getAll ().isEmpty () ? new NoPlaylistsFragment () : new PlaylistFragment ();

        // de base on place artistFragment
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.listes, artistFragment, null);
        fragmentTransaction.commit ();

        // on recupere nos boutons
        Button artistes = root.findViewById (R.id.mes_artistes);
        Button playlists = root.findViewById (R.id.mes_playlists);

        // on initialise le clicklistener pour artist et playlist
        OnClickButtonHere onClickButtonHereArtist= new OnClickButtonHere ();
        onClickButtonHereArtist.setFragment(artistFragment);
        OnClickButtonHere OnClickButtonHerePlaylist = new OnClickButtonHere ();
        OnClickButtonHerePlaylist.setFragment (playlistFragment);

        // a chaque boutons, son onclick associe
        artistes.setOnClickListener(onClickButtonHereArtist);
        playlists.setOnClickListener(OnClickButtonHerePlaylist);

        return root;
    }
    private class OnClickButtonHere implements View.OnClickListener {

        // fragment qu'on integrera directement avec un fragment transaction
        private Fragment fragment;

        // SETTER
        public void setFragment(Fragment fragment) {this.fragment = fragment;}

        @Override
        public void onClick (View v) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.listes, fragment, null);
            fragmentTransaction.commit ();
        }
    }

}

