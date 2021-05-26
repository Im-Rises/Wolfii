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

import java.util.ArrayList;

import static com.example.wolfii.MainActivity.database;

public class FragmentListArtistPlaylistAlbum extends Fragment {

    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    private Button retour, artistes, playlists, albums;

    public static ClickOnRetour clickOnRetour;

    @SuppressLint("WrongConstant")
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate (R.layout.fragment_mes_artistes, container, false);

        // on recupere nos boutons
        artistes = root.findViewById (R.id.mes_artistes);
        playlists = root.findViewById (R.id.mes_playlists);
        albums = root.findViewById (R.id.mes_albums);
        retour = root.findViewById (R.id.retour);

        fragmentManager = getActivity ().getSupportFragmentManager ();

        // on initialise nos fragments, on verifie que la base de donnees n'est pas vide pour lui
        // donner un fragment en consequence

        ArrayList<FragmentsWithReturn> fragments = new ArrayList<> ();
        fragments.add(new FragmentArtist ());
        fragments.add(new FragmentAlbum ());
        fragments.add(database.mainDao ().getAllPlaylists ().isEmpty ()
                        ? new FragmentNoPlaylists ()
                        : new FragmentPlaylist ());

        for(FragmentsWithReturn f : fragments) f.setRetour(retour);

        // de base on place artistFragment
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.listes, fragments.get(0), null);
        fragmentTransaction.commit ();

        clickOnRetour = new ClickOnRetour ();
        clickOnRetour.setFragments (fragments);
        clickOnRetour.setFragmentTransaction (fragmentManager.beginTransaction ());

        // on initialise le clicklistener pour artist et playlist
        OnClickButtonHere onClickButtonHereArtist= new OnClickButtonHere ();
        onClickButtonHereArtist.setFragment(fragments.get(0));

        OnClickButtonHere onClickButtonHerePlaylist = new OnClickButtonHere ();
        onClickButtonHerePlaylist.setFragment (fragments.get(2));

        OnClickButtonHere onClickButtonHereAlbum = new OnClickButtonHere ();
        onClickButtonHereAlbum.setFragment (fragments.get(1));

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

