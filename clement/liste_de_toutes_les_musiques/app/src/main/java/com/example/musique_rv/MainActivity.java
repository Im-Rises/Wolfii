package com.example.musique_rv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
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

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ArrayList<Musique> maMusique;
    private MyMusiqueAdapter monAdapter;
    private static final int MY_PERMISSION_REQUEST = 1;
    private MusiqueService mService;                            //Déclaration pointeur vers le service
    private boolean mBound = false;                             //Variable qui témoigne de l'activation du service


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(MainActivity.this, MusiqueService.class));
        Intent intent = new Intent(MainActivity.this, MusiqueService.class);
        bindService(intent, connection, 0);

        // on verifie un paquet de permission
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
            else {
                ActivityCompat.requestPermissions(MainActivity.this,
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
                Toast.makeText(MainActivity.this, "Lecture de : " + musique.getName(), Toast.LENGTH_SHORT).show();
                Log.d("debug_musique", musique.getPath());
                /*
                Intent intent = new Intent(MainActivity.this, Lecteur.class);
                startActivity(intent);
                */

                mService.setMusiquePlaylist(maMusique, position);
                mService.musiqueDemaPause();
                // position c'est l'index de la musique concernée
                // maMusique => toutes les musiques

            }

            @Override
            public void onMusiqueItemLongClick(View view, Musique musique, int position) {
                Toast.makeText(MainActivity.this, "ah toi tu attends une suppression !", Toast.LENGTH_SHORT).show();
                Log.d("debug_longclick", "suppression ?");

            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false));
        mRecyclerView.setAdapter(monAdapter);
    }
    public ArrayList getMusic() {
        ArrayList<Musique> maMusique= new ArrayList<Musique>();
        ContentResolver contentResolver = getContentResolver(); // rechercher toutes les données voulues
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; // charger les docs externes
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if(songCursor != null && songCursor.moveToFirst()) {
            // MediaStore permet de lire les metadonnees
            // on donne le numero de colonne qui correspond a chaque metadonnes qui nous interesse
            // avec notre curseur
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int songDuration = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            do {
                // on recupere une par une certaines metadonnees des nos musiques
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentPath = songCursor.getString(songLocation);
                String currentDuration = songCursor.getString(songDuration);
                // on ajoute cette musique a notre array
                maMusique.add(new Musique(currentTitle, currentArtist, currentPath, currentDuration));
            } while(songCursor.moveToNext()); // on arrete quand on est arrive a la fin du curseur
        }
        return maMusique;
    }
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusiqueService.LocalBinder binder = (MusiqueService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}