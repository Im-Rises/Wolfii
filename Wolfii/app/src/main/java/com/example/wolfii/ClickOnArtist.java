package com.example.wolfii;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.Wolfii;

import java.util.ArrayList;

import static com.example.wolfii.MainActivity.database;
import static com.example.wolfii.MainActivity.mService;
import static com.example.wolfii.MainActivity.mesMusiques;
import static com.example.wolfii.MusiqueService.maMusique;

public class ClickOnArtist implements MyStringAdapter.ArtisteItemClickListener {

    private MyMusiqueAdapter monMusiqueAdapter;
    private ArrayList<Musique> musiques;
    private RecyclerView mRecyclerView;
    private Context context;
    public void setRecyclerViewForMusic(RecyclerView rv) { mRecyclerView = rv; }
    public void setContext(Context sContext){context = sContext;}

    @Override
    public void onArtisteItemClick (View view, String artiste, int position) {
        musiques = recuperer_musique (artiste);

        monMusiqueAdapter = new MyMusiqueAdapter (musiques, context);
        ClickOnMusic clicker = new ClickOnMusic();
        clicker.setMesMusiques (musiques);
        clicker.setContext (context);
        monMusiqueAdapter.setmMusiqueItemClickListener (clicker);
        mRecyclerView.setAdapter (monMusiqueAdapter);
    }

    @Override
    public void onArtisteItemLongClick (View view, String artiste, int position) {
        musiques = recuperer_musique (artiste);

        Dialog dialog = new Dialog(context);

        // set content view
        dialog.setContentView(R.layout.ajouter_a_une_playlist);

        // initialize width and height
        int width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        //set layout
        dialog.getWindow().setLayout(width, height);
        dialog.show ();

        EditText editText = dialog.findViewById (R.id.nom_playlist);
        Button addToPlaylist = dialog.findViewById (R.id.add);
        Button addToCurrentPlaylist = dialog.findViewById (R.id.addToCurrentPlaylist);

        addToPlaylist.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
                for(Musique musique : musiques) {
                    MainData data = new MainData ();
                    data.setNomMusique (musique.getName ());
                    data.setPath (musique.getPath ());
                    data.setPlaylist (editText.getText ().toString ());
                    data.setAuthor (musique.getAuthor ());
                    data.setDuration (musique.getDuration ());
                    data.setDateTaken (musique.getDateTaken ());
                    data.setGenre (musique.getGenre ());

                    try {
                        database.mainDao ().insert (data);
                    } catch (Exception e) {
                        Log.d ("debug_db", e.getMessage ());
                    }
                }
                dialog.dismiss ();
            }
        });

        addToCurrentPlaylist.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
                for (Musique musique : musiques) {
                    if (! maMusique.isEmpty ()) {
                        maMusique.add (musique);
                    } else {
                        Toast.makeText (Wolfii.getAppContext (), "Lecture de : " + musique.getName (), Toast.LENGTH_SHORT).show ();

                        ArrayList<Musique> musiqueArray = new ArrayList<> ();
                        musiqueArray.add (musique);
                        mService.setMusiquePlaylist (musiqueArray, 0);
                        mService.arretSimpleMusique ();
                        mService.musiqueDemaPause ();
                    }

                }
            }
        });

    }
    private ArrayList<Musique> recuperer_musique (String artiste) {
        // on recupere toutes les musiques selon l'artiste qui nous interesse
        ArrayList<Musique> musiques = new ArrayList<> ();
        for (Musique m : mesMusiques) if (m.getAuthor ().equals (artiste)) musiques.add (m);
        return musiques;
    }
}