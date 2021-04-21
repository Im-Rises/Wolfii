package com.example.wolfii;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.Wolfii;

import java.util.ArrayList;

import static com.example.wolfii.MainActivity.database;
import static com.example.wolfii.MainActivity.mService;

public class ClickOnMusic implements MyMusiqueAdapter.MusiqueItemClickListener {
    private ArrayList<Musique> mesMusiques;
    private Context context;

    public ClickOnMusic () {
    }

    // SETTER
    public void setMesMusiques(ArrayList<Musique> musiques) {mesMusiques = musiques;}
    public void setContext(Context sContext){context = sContext;}

    @Override
    public void onMusiqueItemClick (View view, Musique musique, int position) {
        Toast.makeText(Wolfii.getAppContext (), "Lecture de : " + musique.getName(), Toast.LENGTH_SHORT).show();

        mService.setMusiquePlaylist(mesMusiques, position);
        mService.arretSimpleMusique();
        mService.musiqueDemaPause();
    }

    @Override
    public void onMusiqueItemLongClick (View view, Musique musique, int position) {
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
        addToPlaylist.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
                MainData data = new MainData ();
                data.setNomMusique (musique.getName ());
                data.setPath (musique.getPath ());
                data.setPlaylist (editText.getText ().toString ());
                /*
                data.setAuthor (musique.getAuthor ());
                data.setDuration (musique.getDuration ());
                data.setDateTaken (musique.getDateTaken ());*/

                try {
                    database.mainDao ().insert (data);
                } catch (Exception e) {
                    Log.d ("debug_db", e.getMessage ());
                }

                dialog.dismiss ();
            }
        });
    }
}