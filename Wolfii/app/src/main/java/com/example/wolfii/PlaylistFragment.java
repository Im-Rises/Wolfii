package com.example.wolfii;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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

import static com.example.wolfii.MainActivity.database;

public class PlaylistFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private MyStringAdapter monAdapter;
    private Button newPlaylist;
    private ImageView shuffle;


    PlaylistFragment(){}

    @SuppressLint({"WrongConstant", "SetTextI18n"})
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_playlist, container, false);

        ////////////////////// PLAYLISTS ////////////////////////////
        // on recupere toutes les donnees de la base de donnees et on cree son adapter
        List<MainData> dataList = database.mainDao ().getAll ();
        ArrayList<String> playlists = new ArrayList<String> ();

        shuffle = root.findViewById (R.id.shuffle);
        shuffle.setVisibility (View.INVISIBLE);

        newPlaylist = root.findViewById (R.id.new_playlist);
        ClickOnNewPlaylist clickOnNewPlaylist = new ClickOnNewPlaylist ();
        clickOnNewPlaylist.setContext (getActivity ());
        newPlaylist.setOnClickListener (clickOnNewPlaylist);

        // on recupere toutes les playlists
        for (MainData m : dataList)
            if (! playlists.contains (m.getPlaylist ())) playlists.add (m.getPlaylist ());
        mRecyclerView= (RecyclerView) root.findViewById (R.id.myRecyclerView);
        monAdapter = new MyStringAdapter (playlists, getActivity ());
        monAdapter.setIsPlaylist (true);

        mRecyclerView.setLayoutManager (new LinearLayoutManager (getActivity ().getApplicationContext (), LinearLayout.VERTICAL, false));
        ClickOnPlaylist clickOnPlaylist = new ClickOnPlaylist ();
        clickOnPlaylist.setRecyclerViewForMusic (mRecyclerView);
        clickOnPlaylist.setContext (getActivity ());
        clickOnPlaylist.setShuffle (shuffle);
        monAdapter.setmArtisteItemClickListener (clickOnPlaylist);
        mRecyclerView.setAdapter (monAdapter);

        return root;
    }
}
