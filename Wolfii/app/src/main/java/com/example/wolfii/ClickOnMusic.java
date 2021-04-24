package com.example.wolfii;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.Wolfii;

import java.util.ArrayList;

import static com.example.wolfii.MainActivity.database;
import static com.example.wolfii.MainActivity.mService;
import static com.example.wolfii.MusiqueService.maMusique;

public class ClickOnMusic implements MyMusiqueAdapter.MusiqueItemClickListener {
    private ArrayList<Musique> mesMusiques;
    private static Context context;
    private ImageView shuffle;

    public ClickOnMusic () {
    }

    // SETTER
    public void setMesMusiques(ArrayList<Musique> musiques) {mesMusiques = musiques;}
    public void setContext(Context sContext){context = sContext;}
    public void setShuffle(ImageView shuffle) {this.shuffle = shuffle;}

    @Override
    public void onMusiqueItemClick (View view, Musique musique, int position) {
        Toast.makeText(Wolfii.getAppContext (), "Lecture de : " + musique.getName(), Toast.LENGTH_SHORT).show();

        mService.setMusiquePlaylist(mesMusiques, position);
        mService.musiqueDemaPause();
    }
    public static void longClickMusic(Musique musique) {
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

                dialog.dismiss ();
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

            }
        });

    }

    @Override
    public void onMusiqueItemLongClick (View view, Musique musique, int position) {
        longClickMusic (musique);
    }
}
