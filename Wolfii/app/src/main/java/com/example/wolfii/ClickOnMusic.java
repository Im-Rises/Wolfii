package com.example.wolfii;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Wolfii;

import java.util.ArrayList;

import static com.example.wolfii.MainActivity.database;
import static com.example.wolfii.MainActivity.mService;
import static com.example.wolfii.MusiqueService.maMusique;

public class ClickOnMusic implements MyMusiqueAdapter.MusiqueItemClickListener {
    private ArrayList<Musique> mesMusiques;
    private static Context context;
    private ImageView shuffle;
    public static ArrayList<String> addToPlaylistsArray = new ArrayList<> ();

    public ClickOnMusic () {
    }

    // SETTER
    public void setMesMusiques(ArrayList<Musique> musiques) {mesMusiques = musiques;}
    public void setContext(Context sContext){context = sContext;}
    public void setShuffle(ImageView shuffle) {this.shuffle = shuffle;}

    @Override
    public void onMusiqueItemClick (View view, Musique musique, int position) {
        Toast.makeText(Wolfii.getAppContext (), "Lecture de : " + musique.getName(), Toast.LENGTH_SHORT).show();

        mService.arretSimpleMusique();
        mService.setMusiquePlaylist(mesMusiques, position);
        mService.musiqueDemaPause();
    }

    @SuppressLint("WrongConstant")
    public static void longClickMusic(Musique musique, Context context) {
        Dialog dialog = new Dialog(context);

        // set content view
        dialog.setContentView(R.layout.ajouter_a_une_playlist);

        // initialize width and height
        int width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        //set layout
        dialog.getWindow().setLayout(width, height);
        dialog.show ();

        Button addToPlaylist = dialog.findViewById (R.id.add);
        Button addToCurrentPlaylist = dialog.findViewById (R.id.addToCurrentPlaylist);
        Button hiddenTitle = dialog.findViewById (R.id.hiddenTitle);
        RecyclerView rv = dialog.findViewById (R.id.myRecyclerView);

        hiddenTitle.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                DataHiddenMusic dataHiddenMusic = new DataHiddenMusic ();
                dataHiddenMusic.setPath (musique.getPath ());

                database.mainDao ().insertMusic (musique);
                database.mainDao ().insertHiddenTitle (dataHiddenMusic);
            }
        });

        ArrayList<String> mesPlaylists = (ArrayList<String>) database.mainDao ().getAllPlaylists ();
        Log.d("debug_playlist", mesPlaylists.toString ());
        MyStringAdapter adapter = new MyStringAdapter (mesPlaylists);
        adapter.setIsLongClickMusic(true);
        rv.setLayoutManager(new LinearLayoutManager (context.getApplicationContext(), LinearLayout.VERTICAL, false));

        rv.setAdapter (adapter);

        addToPlaylist.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
                for(String playlist : addToPlaylistsArray) {
                    DataPlaylist dataPlaylist = new DataPlaylist ();
                    dataPlaylist.setNom (playlist);

                    DataPlaylistMusic dataPlaylistMusic = new DataPlaylistMusic ();
                    dataPlaylistMusic.setPath (musique.getPath ());
                    dataPlaylistMusic.setPlaylist (playlist);

                    database.mainDao ().insertMusic (musique);
                    database.mainDao ().insertPlaylist (dataPlaylist);
                    database.mainDao ().insertPlaylistMusic(dataPlaylistMusic);

                    Log.d("debug_data", dataPlaylist.getNom ());

                    dialog.dismiss ();
                }

            }
        });

        addToCurrentPlaylist.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
                if(!maMusique.isEmpty ()) {
                    maMusique.add (musique);
                }
                else {
                    Toast.makeText(Wolfii.getAppContext (), "Lecture de : " + musique.getName(), Toast.LENGTH_SHORT).show();

                    ArrayList<Musique> musiqueArray = new ArrayList<> ();
                    musiqueArray.add(musique);
                    mService.setMusiquePlaylist(musiqueArray, 0);
                    mService.arretSimpleMusique();
                    mService.musiqueDemaPause();
                }
                dialog.dismiss ();

            }
        });

    }

    @Override
    public void onMusiqueItemLongClick (View view, Musique musique, int position) {
        longClickMusic (musique, context);
    }
}
