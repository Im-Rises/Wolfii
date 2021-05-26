package com.example.wolfii;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class FragmentNoPlaylists extends Fragment {

    public FragmentNoPlaylists () {
        // Required empty public constructor
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate (R.layout.fragment_no_playlists, container, false);

        Button newPlaylist = root.findViewById (R.id.new_playlist);
        ClickOnNewPlaylist clickOnNewPlaylist = new ClickOnNewPlaylist ();
        clickOnNewPlaylist.setContext (getActivity ());
        newPlaylist.setOnClickListener (clickOnNewPlaylist);

        return root;
    }
}