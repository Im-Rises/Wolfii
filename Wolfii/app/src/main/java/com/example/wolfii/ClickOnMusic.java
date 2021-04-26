package com.example.wolfii;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
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
        if(position == mService.getPositionMusique ()) {
            view.setBackgroundColor (Color.CYAN);
        }
        else view.setBackgroundColor (Color.WHITE);
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

        EditText editText = dialog.findViewById (R.id.nom_playlist);
        Button addToPlaylist = dialog.findViewById (R.id.add);
        Button addToCurrentPlaylist = dialog.findViewById (R.id.addToCurrentPlaylist);

        RecyclerView rv = dialog.findViewById (R.id.myRecyclerView);
        ArrayList<String> mesPlaylists = (ArrayList<String>) database.mainDao ().getAllPlaylists ();
        Log.d("debug_playlist", mesPlaylists.toString ());
        MyStringAdapter adapter = new MyStringAdapter (mesPlaylists, context);
        rv.setLayoutManager(new LinearLayoutManager (context.getApplicationContext(), LinearLayout.VERTICAL, false));

        rv.setAdapter (adapter);

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

                PlaylistData pData = new PlaylistData ();
                pData.setNom (editText.getText ().toString ());

                try {
                    database.mainDao ().insert (data);
                    database.mainDao ().createPlaylist (pData);
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
                dialog.dismiss ();

            }
        });

    }

    @Override
    public void onMusiqueItemLongClick (View view, Musique musique, int position) {
        longClickMusic (musique, context);
    }
}
