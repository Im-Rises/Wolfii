package com.example.lecteurmusique;


import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private SeekBar seekBarMusique;                             //SeekBar de lecture de la musique

    private Button btnMusiqueDemaPause;                         //Bouton pour démarrer et mettre en pause la musique
    private Button btnMusiqueArret;                          //Bouton pour arrêter la musique
    private Button btnMusiqueBoucle;                          //Bouton pour boucler la musique

    private TextView txtViewMusiqueTemps, txtViewMusiqueDuree;   //TextView du temps de lecture de la musique

    private MusiqueService mService;                            //Déclaration pointeur vers le service
    private boolean mBound = false;                             //Variable qui témoigne de l'activation du service

    private static final String ACTION_STRING_ACTIVITY = "ToActivity";  //Action pour envoyer un Boradcast dans l'activité


/*------------------------------------------FONCTION ONCREATE-----------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Liaisons des Boutons, des TextViews et du SeekBar de l'interface dans la code.

        this.txtViewMusiqueTemps = (TextView) findViewById(R.id.txtViewMusiqueTemps);
        this.txtViewMusiqueTemps.setSoundEffectsEnabled(false);

        this.txtViewMusiqueDuree = (TextView) findViewById(R.id.txtViewMusiqueDuree);
        this.txtViewMusiqueDuree.setSoundEffectsEnabled(false);

        this.seekBarMusique=(SeekBar) findViewById(R.id.seekBarMusique);
        this.seekBarMusique.setSoundEffectsEnabled(false);
        this.seekBarMusique.setOnSeekBarChangeListener(new seekBarEcouteur());
    }


/*--------------------------------------FONCTION ONSTART------------------------------------------------*/

    @Override
    protected void onStart() {
        super.onStart();

        //Démarre le service MusiqueService
        startService(new Intent(this, MusiqueService.class));

        //Création d'une Bound Session
        Intent intent = new Intent(MainActivity.this, MusiqueService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        //Enregistrement du BroafcastRecevier sous l'écoute du message ACTION_STRING_ACTIVITY
        if (broadcastReceiverMajInterface != null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_ACTIVITY);
            registerReceiver(broadcastReceiverMajInterface, intentFilter);
        }
    }



/*--------------------------------------FONCTION ONDESTROY------------------------------------------------*/
    @Override
    protected void onDestroy() {
        //Arrêt Bound Session
        unbindService(connection);
        mBound=false;

        //Arrêt broadcast receiver de mise à jour de l'interface
        unregisterReceiver(broadcastReceiverMajInterface);

        //Appel destructeur de la classe parente Activité (supprime l'activité)
        super.onDestroy();
    }


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



/*----------------------------------GESTION BROADCASTRECEIVER--------------------------------------------------*/

    private BroadcastReceiver broadcastReceiverMajInterface = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            majInterface();//Mise à jour de l'interface
        }
    };






/*--------------------------------------FONCTION/CLASS SEEKBAR------------------------------------------------*/

    private class seekBarEcouteur implements SeekBar.OnSeekBarChangeListener {

        //Evenement qui s'enclenche sur le déplacement du seekbar
        public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser)
        {
            if (fromUser && mBound && mService.getMusiquePlayerIsSet()) {
                mService.setMusiquePlayerPosition(progress);
                txtViewMusiqueTemps.setText(millisecondesEnMinutesSeconde(mService.getMusiquePlayerPosition()));
            }
        }

        //Evenement qui s'enclenche sur l'appuit sur le seekbar
        public void onStartTrackingTouch(SeekBar seekBar) {}

        //Evenement qui s'enclenche sur la fin du déplacement du seekbar
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }


/*--------------------------------------FONCTION BOUTONS------------------------------------------------*/

    public void cmdDemaPause(View view)
    {
        //Envoyer commande de démarrage et pause de la musique à la classe BroadCastReceiver
        mService.musiqueDemaPause();
        seekBarMusique.setMax(mService.getMusiquePlayerDuration());
    }


    public void cmdArret(View view)
    {
        //Envoyer commande d'arrêt de la musique à la classe BroadCastReceiver
        mService.musiqueArret();
        seekBarMusique.setProgress(0);
        txtViewMusiqueTemps.setText("00:00");
    }

    public void cmdBoucleDebouble(View view) {
        //Active désactive la boucle de la musique actuelle
        mService.musiqueBoucleDeboucle();
        //Toast.makeText(getApplicationContext(), "Lecture répétée de la musique en cours", Toast.LENGTH_SHORT).show();
    }

    public void cmdMusiqueStuivante(View view)
    {

    }

    public void cmdMusiquePrecedente(View view)
    {

    }


/*--------------------------------------FONTION MAJ INTERFACE---------------------------------------------------*/

    public void majInterface()
    {
        if (mService.getMusiquePlayerIsPlaying())
        {
            //seekBarMusique.setMax(mService.getMusiquePlayerDuration());
            //txtViewMusiqueDuree.setText(millisecondesEnMinutesSeconde(mService.getMusiquePlayerDuration()));
            seekBarMusique.setMax(mService.getMusiquePlayerDuration());
            txtViewMusiqueDuree.setText(millisecondesEnMinutesSeconde(mService.getMusiquePlayerDuration()));
            seekBarMusique.setProgress(mService.getMusiquePlayerPosition());
            txtViewMusiqueTemps.setText(millisecondesEnMinutesSeconde(mService.getMusiquePlayerPosition()));
        }
    }


/*--------------------------------------AUTRES FONCTIONS------------------------------------------------*/

    @SuppressLint("DefaultLocale")
    private String millisecondesEnMinutesSeconde(int tmpsMillisecondes) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(tmpsMillisecondes),
                TimeUnit.MILLISECONDS.toSeconds(tmpsMillisecondes) - TimeUnit.MILLISECONDS.toMinutes(tmpsMillisecondes) * 60);
    }
}
