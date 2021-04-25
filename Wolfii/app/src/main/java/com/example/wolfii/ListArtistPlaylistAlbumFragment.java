package com.example.wolfii;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static com.example.wolfii.MainActivity.database;

public class ListArtistPlaylistAlbumFragment extends Fragment {

    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    private Fragment playlistFragment, artistFragment, albumFragment;


    @SuppressLint("WrongConstant")
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate (R.layout.fragment_mes_artistes, container, false);
        fragmentManager = getActivity ().getSupportFragmentManager ();

        // on initialise nos fragments, on verifie que la base de donnees n'est pas vide pour lui
        // donner un fragment en consequence
        artistFragment = new ArtistFragment ();
        albumFragment = new AlbumFragment ();
        playlistFragment = database.mainDao ().getAllPlaylists ().isEmpty () ? new NoPlaylistsFragment () : new PlaylistFragment ();

        // de base on place artistFragment
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.listes, artistFragment, null);
        fragmentTransaction.commit ();

        // on recupere nos boutons
        Button artistes = root.findViewById (R.id.mes_artistes);
        Button playlists = root.findViewById (R.id.mes_playlists);
        Button albums = root.findViewById (R.id.mes_albums);

        // on initialise le clicklistener pour artist et playlist
        OnClickButtonHere onClickButtonHereArtist= new OnClickButtonHere ();
        onClickButtonHereArtist.setFragment(artistFragment);

        OnClickButtonHere onClickButtonHerePlaylist = new OnClickButtonHere ();
        onClickButtonHerePlaylist.setFragment (playlistFragment);

        OnClickButtonHere onClickButtonHereAlbum = new OnClickButtonHere ();
        onClickButtonHereAlbum.setFragment (albumFragment);

        // a chaque boutons, son onclick associe
        artistes.setOnClickListener(onClickButtonHereArtist);
        playlists.setOnClickListener(onClickButtonHerePlaylist);
        albums.setOnClickListener (onClickButtonHereAlbum);

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

