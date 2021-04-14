package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MusiqueRV extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private List<Musique> maMusique;
    private MyMusiqueAdapter monAdapter;
    private static final int MY_PERMISSION_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // on verifie un paquet de permission
        if(ContextCompat.checkSelfPermission(MusiqueRV.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(MusiqueRV.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MusiqueRV.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
            else {
                ActivityCompat.requestPermissions(MusiqueRV.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        }
        refreshRV();
    }
    @SuppressLint("WrongConstant")
    public void refreshRV() {
        // creation du recyclerview
        mRecyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        maMusique = getMusic(); // on recupere ici toutes les musiques sous forme d'un tableau

        monAdapter = new MyMusiqueAdapter(maMusique);
        monAdapter.setmMusiqueItemClickListener(new MyMusiqueAdapter.MusiqueItemClickListener() {
            @Override
            public void onMusiqueItemClick(View view, Musique musique, int position) {
                Toast.makeText(MusiqueRV.this, "Lecture de : " + musique.getName(), Toast.LENGTH_SHORT).show();
                Log.d("debug_musique", musique.getName());
            }

            @Override
            public void onMusiqueItemLongClick(View view, Musique musique, int position) {
                Toast.makeText(MusiqueRV.this, "ah toi tu attends une suppression !", Toast.LENGTH_SHORT).show();
                Log.d("debug_longclick", "suppression ?");
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false));
        mRecyclerView.setAdapter(monAdapter);
    }
    public ArrayList getMusic() {
        ArrayList<Musique> maMusique= new ArrayList<Musique>();
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if(songCursor != null && songCursor.moveToFirst()) {
            // MediaStore permet de lire les metadonnees
            // on donne le numero de colonne qui correspond a chaque metadonnes qui nous interesse
            // avec notre curseur
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do {
                // on recupere une par une certaines metadonnees des nos musiques
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentPath = songCursor.getString(songLocation);
                // on ajoute cette musique a notre array
                maMusique.add(new Musique(currentTitle, currentPath));
            } while(songCursor.moveToNext()); // on arrete quand on est arrive a la fin du curseur
        }
        return maMusique;
    }
}