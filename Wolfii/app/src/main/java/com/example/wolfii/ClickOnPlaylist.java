package com.example.wolfii;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.example.wolfii.MainActivity.database;

public class ClickOnPlaylist implements MyStringAdapter.ArtisteItemClickListener {

    private MyMusiqueAdapter monMusiqueAdapter;
    private static Context context;
    private ArrayList<Musique> musiques = new ArrayList<> ();
    private RecyclerView mRecyclerView;
    private ImageView shuffleiv;

    private Button retour;

    // SETTER
    public void setRecyclerViewForMusic(RecyclerView rv) { mRecyclerView = rv; }
    public void setContext(Context sContext) {context = sContext;}
    public void setShuffle(ImageView shuffle) {this.shuffleiv = shuffle;}
    public void setRetour(Button retour){this.retour = retour;}

    @Override
    public void onArtisteItemClick (View view, String playlist, int position) {
        shuffleiv.setVisibility (View.VISIBLE);

        shuffleiv.setVisibility (View.VISIBLE);
        ClickOnShuffle shuffle = new ClickOnShuffle ();
        shuffle.setContext (context);
        shuffle.setmRecyclerView (mRecyclerView);
        shuffle.setPlaylist (musiques);
        shuffleiv.setOnClickListener (shuffle);

        retour.setVisibility (View.VISIBLE);

        List<Musique> musiquesMainData = database.mainDao ().getMusicFromPlaylist (playlist);
        for(Musique data : musiquesMainData) {
            if(data.getPath () != null) {
                musiques.add (data);
            }
        }

        monMusiqueAdapter = new MyMusiqueAdapter (musiques, context);
        ClickOnMusic clicker = new ClickOnMusic();
        clicker.setMesMusiques (musiques);
        clicker.setContext (context);
        monMusiqueAdapter.setmMusiqueItemClickListener (clicker);
        mRecyclerView.setAdapter (monMusiqueAdapter);
    }

    public static void longClickPlaylist(String playlist) {
        Dialog dialog = new Dialog (context);

        // set content view
        dialog.setContentView (R.layout.dialog_playlist);

        // initialize width and height
        int width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        //set layout
        dialog.getWindow ().setLayout (width, height);
        dialog.show ();

        Button delete = dialog.findViewById (R.id.delete);
        Button rename = dialog.findViewById (R.id.rename);

        delete.setOnClickListener (new View.OnClickListener () {
            public void onClick (View v) {

                database.mainDao ().deleteFromPlaylistMusicWherePlaylist (playlist);
                database.mainDao (). deletePlaylist(playlist);

                Toast.makeText(context, "playlist " +playlist +" supprimée", Toast.LENGTH_SHORT).show();
            }
        });

        rename.setOnClickListener (new View.OnClickListener () {
            public void onClick (View v) {
                Dialog dRename = new Dialog (context);
                dRename.setContentView (R.layout.dialog_rename_playlist);

                // initialize width and height
                int width = WindowManager.LayoutParams.MATCH_PARENT;
                int height = WindowManager.LayoutParams.WRAP_CONTENT;
                //set layout
                dRename.getWindow ().setLayout (width, height);
                dRename.show ();
                Button confirmRename = dRename.findViewById (R.id.confirmRename);
                EditText name = dRename.findViewById (R.id.name);
                confirmRename.setOnClickListener (new View.OnClickListener () {
                    @Override
                    public void onClick (View v) {
                        String newName = name.getText ().toString ();
                        database.mainDao ().renamePlaylist (playlist, newName);
                        Toast.makeText(context, "playlist " +playlist +" s'appellera maintenant " + newName, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public void onArtisteItemLongClick (View view, String playlist, int position) {
        longClickPlaylist (playlist);
    }

}

