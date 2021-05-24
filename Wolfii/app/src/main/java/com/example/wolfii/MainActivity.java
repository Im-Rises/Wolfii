package com.example.wolfii;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
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

    public static ArrayList<Musique> mesMusiques = new ArrayList<>();
    public static ArrayList<String> mesArtistes = new ArrayList<>();
    public static ArrayList<String> mesGenres = new ArrayList<>();
    public static ArrayList<String> mesAlbums = new ArrayList<>();
    public static ArrayList<String> mesAlbumsImages = new ArrayList<>();

    private static final int MY_PERMISSION_REQUEST = 1;

    public static boolean estActif=false;

    public static MusiqueService mService;                            //Déclaration pointeur vers le service
    public static boolean mBound = false;                             //Variable qui témoigne de l'activation du service
    public static RoomDB database;  // notre base de donnees


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // on cache la barre d'action

        database = RoomDB.getInstance(this); // on initialise la base de donnees
        estActif=true; // pour savoir si l'applie est lancée ou pas

        //////////////////////////////////////////////////////////////
        //////////////DEMMARRAGE SERVICE ET CONNEXION/////////////////
        //////////////////////////////////////////////////////////////

        if (!MusiqueService.estActif)
        {
/*            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(MainActivity.this, MusiqueService.class));
            }
            else
            {*/
                startService(new Intent(MainActivity.this, MusiqueService.class));
            /*}*/
        }

        //Création d'une Intent pour la connexion BoundService
        Intent intent = new Intent(MainActivity.this, MusiqueService.class);
        bindService(intent, connection, 0);//Permet l'arrêt du Service avant l'arrêt du BoundService (permettant d'arrêter le service par les boutons notification)
        //bindService(intent, connection, Context.BIND_AUTO_CREATE);//Arrêt du Service autorisé que si le BoundService est au préalable arrêté
        //////////////////////////////////////////////////////////////


        // on verifie si on a bien acces a la permission de lecture des fichiers
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        }
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_nouveaute, R.id.navigation_nouveaute, R.id.navigation_dashboard, R.id.navigation_search)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        mesMusiques = getMusic(); // on recupere toutes les musiques dispo l'appareil
    }


    // On recupere toutes les musiques disponibles sur le telephone
    public ArrayList getMusic() {
        ArrayList<Musique> maMusique = new ArrayList<Musique>();
        ContentResolver contentResolver = getContentResolver(); // rechercher toutes les données voulues
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; // charger les docs externes
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            // MediaStore permet de lire les metadonnees
            // on donne le numero de colonne qui correspond a chaque metadonnes qui nous interesse
            // avec notre curseur
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int songDuration = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int songDateTaken = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_TAKEN);
            int songAlbum = songCursor.getColumnIndex (MediaStore.Audio.Media.ALBUM);
            int songGenre = 25; // la colonne des genres
            do {
                // on recupere une par une certaines metadonnees des nos musiques
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentPath = songCursor.getString(songLocation);
                String currentDuration = songCursor.getString(songDuration);
                String currentDateTaken = songCursor.getString(songDateTaken);
                String currentGenre = songCursor.getString(songGenre);
                String currentAlbum = songCursor.getString (songAlbum);

                maMusique.add(new Musique(
                        currentTitle,
                        currentArtist,
                        currentPath,
                        currentDuration,
                        currentDateTaken,
                        currentGenre,
                        currentAlbum));
                // on repertorie tous les differents genres dispos
                if(!mesGenres.contains (currentGenre)) mesGenres.add(currentGenre);
                if(!mesArtistes.contains(currentArtist)) mesArtistes.add(currentArtist);
                if(!mesAlbums.contains (currentAlbum)) {
                    mesAlbums.add(currentAlbum);
                    mesAlbumsImages.add(currentPath);
                }
            } while (songCursor.moveToNext()); // on arrete quand on est arrive a la fin du curseur
        }
        // on reverse le tableau pour avoir les titres telecharges recement en premier
        Collections.reverse(maMusique);
        return maMusique;
    }



    //////////////////////////////////////////////////////////////
    //////////////////////////BOUND SERVICE///////////////////////
    //////////////////////////////////////////////////////////////

    /*--------------------------------------GESTION BOUND SERVICE------------------------------------------------*/

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

    /*--------------------------------------FONCTION ONDESTROY------------------------------------------------*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        estActif=false;

        //Arrêt Bound Session
        unbindService(connection);
        mBound = false;

        //Arrête le service si aucune musique n'est en cours
        if (!mService.getMusiquePlayerIsSet())
        {
            //Toast.makeText(getApplicationContext(),"Le MainActivity arrête la service",Toast.LENGTH_SHORT).show();
            stopService(new Intent(MainActivity.this,MusiqueService.class));
        }
    }
}