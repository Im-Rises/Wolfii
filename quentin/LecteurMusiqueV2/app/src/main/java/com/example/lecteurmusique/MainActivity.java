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

    private TextView txtViewMusiqueTemps, txtViewMusiqueDuree;   //TextView du temps de lecture de la musique

    MusiqueService mService;
    boolean mBound = false;



    //Fonction d'apppel lors de la création de la page
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Code qui lie les objets du activity_main.xml à ceux dy MainActivity.xml*/
        this.seekBarMusique = (SeekBar) findViewById(R.id.seekBarMusique);

        this.btnMusiqueDemaPause = (Button) findViewById(R.id.btnDemaPause);
        this.btnMusiqueArret = (Button) findViewById(R.id.btnArret);

        this.txtViewMusiqueTemps = (TextView) findViewById(R.id.txtViewMusiqueTemps);
        this.txtViewMusiqueDuree = (TextView) findViewById(R.id.txtViewMusiqueDuree);


        startService(new Intent(MainActivity.this,MusiqueService.class));//Démarre le service



/*--------------------------------------GESTION BOUTON DEMARRER------------------------------------------------*/

        btnMusiqueDemaPause.setSoundEffectsEnabled(false);
        btnMusiqueDemaPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Envoyer commande de démarrage et pause de la musique à la classe BroadCastReceiver
                //startService(new Intent(MainActivity.this,MusiqueService.class).setAction("DEMAPAUSE"));
                Intent intent = new Intent(MainActivity.this, MusiqueService.class);
                bindService(intent, connection, Context.BIND_AUTO_CREATE);
               }
        });

/*--------------------------------------GESTION BOUTON ARRET------------------------------------------------*/

        btnMusiqueArret.setSoundEffectsEnabled(false);
        btnMusiqueArret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Envoyer commande d'arrêt de la musique à la classe BroadCastReceiver
                stopService(new Intent(MainActivity.this,MusiqueService.class));
                unbindService(connection);
                mBound=false;
            }
        });



/*--------------------------------------GESTION SEEKBAR------------------------------------------------*/

        //Gestion du déplacement par l'utilisateur du seekbar
        seekBarMusique.setSoundEffectsEnabled(false);
        seekBarMusique.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser && mBound) {
                    int num = mService.getRandomNumber();
                    Log.e("BIND FONCTIONNE", "bien");
                    //musiquePlayer.seekTo(progress);
                    //txtViewMusiqueTemps.setText(millisecondesEnMinutesSeconde(musiquePlayer.getCurrentPosition()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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


/*--------------------------------------AUTRES FONCTIONS------------------------------------------------*/

    @SuppressLint("DefaultLocale")
    private String millisecondesEnMinutesSeconde(int tmpsMillisecondes) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(tmpsMillisecondes),
                TimeUnit.MILLISECONDS.toSeconds(tmpsMillisecondes) - TimeUnit.MILLISECONDS.toMinutes(tmpsMillisecondes) * 60);
    }
}
