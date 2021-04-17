package com.example.wolfii;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    public static ArrayList<Musique> maMusique = new ArrayList<>();
    public static ArrayList<String> mesArtistes = new ArrayList<>();
    private static final int MY_PERMISSION_REQUEST = 1;
    private MusiqueService mService;                            //Déclaration pointeur vers le service
    private boolean mBound = false;                             //Variable qui témoigne de l'activation du service

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_artiste, R.id.navigation_dashboard, R.id.navigation_search)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        maMusique = getMusic();
        mesArtistes = getArtistes(maMusique);
    }

    // on trie les musiques selon les artistes
    public static ArrayList<String> getArtistes(ArrayList<Musique> musiques) {
        ArrayList<String> mesArtistes = new ArrayList<>();
        for(Musique m : musiques)
            if(!mesArtistes.contains(m.getAuthor())) mesArtistes.add(m.getAuthor());
        return mesArtistes;
    }

    // On recupere toutes les musiques disponibles sur le telephone
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
            int songDateTaken = songCursor.getColumnIndex (MediaStore.Audio.Media.DATE_TAKEN);
            do {
                // on recupere une par une certaines metadonnees des nos musiques
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentPath = songCursor.getString(songLocation);
                String currentDuration = songCursor.getString(songDuration);
                String currentDateTaken = songCursor.getString (songDateTaken);
                // on ajoute cette musique a notre array
                maMusique.add(new Musique (currentTitle, currentArtist, currentPath, currentDuration, currentDateTaken));
            } while(songCursor.moveToNext()); // on arrete quand on est arrive a la fin du curseur
        }
        // on reverse le tableau pour avoir les titres telecharges recement en premier
        Collections.reverse(maMusique);
        return maMusique;
    }


}