package com.example.wolfii;

import android.Manifest;
import android.app.Dialog;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Wolfii;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<Musique> maMusique = new ArrayList<>();
    public static ArrayList<String> mesArtistes = new ArrayList<>();
    private static final int MY_PERMISSION_REQUEST = 1;

    public static boolean estActif=false;

    public static MusiqueService mService;                            //Déclaration pointeur vers le service
    public static boolean mBound = false;                             //Variable qui témoigne de l'activation du service
    public static RoomDB database;  // notre base de donnees
    //public static ArrayList<MainData> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {   database = RoomDB.getInstance(this); }
        catch (Exception e) {
            Log.d("debug_db", e.getMessage ());
        }
        //database = RoomDB.getInstance(this);
        //dataList = database.mainDao().getAll();
        estActif=true;

        //////////////////////////////////////////////////////////////
        //////////////DEMMARRAGE SERVICE ET CONNEXION/////////////////
        //////////////////////////////////////////////////////////////
        if (!MusiqueService.estActif)
        {
            Toast.makeText(MainActivity.this,"Démarrage du service",Toast.LENGTH_LONG).show();
            startService(new Intent(MainActivity.this, MusiqueService.class));
        }


        //Création d'une Intent pour la connexion BoundService
        Intent intent = new Intent(MainActivity.this, MusiqueService.class);
        bindService(intent, connection, 0);//Permet l'arrêt du Service avant l'arrêt du BoundService (permettant d'arrêter le service par les boutons notification)
        //bindService(intent, connection, Context.BIND_AUTO_CREATE);//Arrêt du Service autorisé que si le BoundService est au préalable arrêté
        //////////////////////////////////////////////////////////////





        // on verifie un paquet de permission
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
                R.id.navigation_home, R.id.navigation_artiste, R.id.navigation_dashboard, R.id.navigation_search, R.id.navigation_identification)
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
        for (Musique m : musiques)
            if (!mesArtistes.contains(m.getAuthor())) mesArtistes.add(m.getAuthor());
        return mesArtistes;
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
            do {
                // on recupere une par une certaines metadonnees des nos musiques
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentPath = songCursor.getString(songLocation);
                String currentDuration = songCursor.getString(songDuration);
                String currentDateTaken = songCursor.getString(songDateTaken);
                // on ajoute cette musique a notre array
                maMusique.add(new Musique(currentTitle, currentArtist, currentPath, currentDuration, currentDateTaken));
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

    //////////////FONCTION ONDESTROY (FERMETURE APPLICATION)/////
    @Override
    protected void onDestroy() {
        super.onDestroy();

        estActif=false;

        //Arrêt Bound Session
        unbindService(connection);
        mBound = false;

        //Arrête le service si aucune musique n'est en cours
        if (!mService.getMusiquePlayerIsSet() || !mService.getMusiquePlayerIsPlaying())
        {
            mService.arretTotalMusique();
            stopService(new Intent(MainActivity.this,MusiqueService.class));
        }
    }
    //////////////////////////////////////////////////////////////

    // quand on click sur une musique
    public static class ClickOnMusic implements MyMusiqueAdapter.MusiqueItemClickListener {
        private ArrayList<Musique> mesMusiques;

        // SETTER
        public void setMesMusiques(ArrayList<Musique> musiques) {
            mesMusiques = musiques;
        }

        @Override
        public void onMusiqueItemClick (View view, Musique musique, int position) {
            Toast.makeText(Wolfii.getAppContext (), "Lecture de : " + musique.getName(), Toast.LENGTH_SHORT).show();

            mService.setMusiquePlaylist(mesMusiques, position);
            mService.arretSimpleMusique();
            mService.musiqueDemaPause();
        }

        @Override
        public void onMusiqueItemLongClick (View view, Musique musique, int position) {
            Dialog dialog = new Dialog(Wolfii.getAppContext ());

            // set content view
            dialog.setContentView(R.layout.ajouter_a_une_playlist);

            // initialize width and height
            int width = WindowManager.LayoutParams.MATCH_PARENT;
            int height = WindowManager.LayoutParams.WRAP_CONTENT;
            //set layout
            dialog.getWindow().setLayout(width, height);

            EditText editText = dialog.findViewById (R.id.nom_playlist);
            Button addToPlaylist = dialog.findViewById (R.id.add);
            addToPlaylist.setOnClickListener(new View.OnClickListener() {
                public void onClick (View v) {
                    MainData data = new MainData ();
                    data.setNomMusique (musique.getName ());
                    data.setPath (musique.getPath ());
                    data.setPlaylist (editText.getText ().toString ());

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

    // quand on click sur un artiste
    public static class ClickOnArtist implements MyArtisteAdapter.ArtisteItemClickListener {

        MyMusiqueAdapter monMusiqueAdapter;
        ArrayList<Musique> musiques;
        RecyclerView mRecyclerView;

        public void setRecyclerViewForMusic(RecyclerView rv) {
            mRecyclerView = rv;
        }

        @Override
        public void onArtisteItemClick (View view, String artiste, int position) {
            musiques = recuperer_musique (artiste);

            monMusiqueAdapter = new MyMusiqueAdapter (musiques, Wolfii.getAppContext ());
            MainActivity.ClickOnMusic clicker = new MainActivity.ClickOnMusic();
            clicker.setMesMusiques (musiques);
            monMusiqueAdapter.setmMusiqueItemClickListener (clicker);
            mRecyclerView.setAdapter (monMusiqueAdapter);
        }

        @Override
        public void onArtisteItemLongClick (View view, String musique, int position) {

        }
        private ArrayList<Musique> recuperer_musique (String artiste) {
            // on recupere toutes les musiques selon l'artiste qui nous interesse
            ArrayList<Musique> musiques = new ArrayList<> ();
            for (Musique m : maMusique) if (m.getAuthor ().equals (artiste)) musiques.add (m);
            return musiques;
        }
    }
    // quand on click sur une playlist
    public static class ClickOnPlaylist implements MyArtisteAdapter.ArtisteItemClickListener {

        ArrayList<String> playlists = new ArrayList<String> ();

        public void setPlaylists(ArrayList<String> sPlaylists) {
            playlists = sPlaylists;
        }
        @Override
        public void onArtisteItemClick (View view, String playlist, int position) {
            List<MainData> musiques = database.mainDao ().getMusicFromPlaylist (playlist);
            //database.mainDao ().deletePlaylist (playlist);
            //getActivity ().setContentView (R.layout.liste);

                /*Button retour = getActivity ().findViewById (R.id.retour);
                retour.setOnClickListener (new View.OnClickListener() {
                    public void onClick(View v) {
                        //getActivity ().setContentView (R.layout.fragment_mes_artistes);
                    }
                });*/
        }

        @Override
        public void onArtisteItemLongClick (View view, String artiste, int position) {

        }
    }
}