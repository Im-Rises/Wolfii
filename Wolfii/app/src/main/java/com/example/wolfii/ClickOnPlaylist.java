package com.example.wolfii;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static com.example.wolfii.MainActivity.database;

public class ClickOnPlaylist implements MyArtisteAdapter.ArtisteItemClickListener {

    ArrayList<String> playlists = new ArrayList<String> ();

    public void setPlaylists(ArrayList<String> sPlaylists) {
        playlists = sPlaylists;
    }
    @Override
    public void onArtisteItemClick (View view, String playlist, int position) {
        List<MainData> musiques = database.mainDao ().getMusicFromPlaylist (playlist);
        //database.mainDao ().deletePlaylist (playlist);
        //getActivity ().setContentView (R.layout.liste);

                /*Button retour = getActivity ().findViewById (R.id.retour);
                retour.setOnClickListener (new View.OnClickListener() {
                    public void onClick(View v) {
                        //getActivity ().setContentView (R.layout.fragment_mes_artistes);
                    }
                });*/
    }

    @Override
    public void onArtisteItemLongClick (View view, String artiste, int position) {

    }
}
