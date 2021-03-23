package com.example.lecteurmusique;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/*A faire :
* Le programme ne peut aps mettre à jour des objets à l'écran quand avec le timer...
* Il faut utiliser des runnables.
* Exemple :
* runOnUiThread(new Runnable() {
    public void run() {
        t3.setText("times up");
        t3.setVisibility(View.VISIBLE);
    }
});
* */

public class MainActivity extends AppCompatActivity {

    private MediaPlayer musiquePlayer;
    private SeekBar seekBarMusic;
    private TextView txtViewMusicTemps;

    private Timer timerMusiquePlayer;
    private int timeMusiquePlayerPeriode=1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Code qui lie les objets du activity_main.xml à ceux dy MainActivity.xml*/
        seekBarMusic = (SeekBar) findViewById(R.id.seekBarMusic);
        txtViewMusicTemps = (TextView) findViewById(R.id.txtViewMusicTemps);
    }

    /*Commande de la musique*/
    public void musiqueDemarrer(View view)
    {
        if (musiquePlayer == null)
        {
            musiquePlayer = MediaPlayer.create(this, R.raw.musiquetest);
            musiquePlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

            //Initialisation du sekBar sur le temps de la musique :
            seekBarMusic.setMax(musiquePlayer.getDuration());

            //Initialisation du timer :
            timerMusiquePlayer = new Timer();
            //Appel de la fonction toutes les "timerMusiquePalyerPeriode" millisecondes :
            timerMusiquePlayer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    seekBarMusic.setProgress(musiquePlayer.getCurrentPosition());
                    //txtViewMusicTemps.setText("00:01");
                }
            },0,timeMusiquePlayerPeriode);
        }
        musiquePlayer.start();
    }

    public void musiquePause(View view)
    {
        if (musiquePlayer != null)
        {
            musiquePlayer.pause();
        }
    }

    public void musiqueArret(View view)
    {
        if (musiquePlayer != null)
        {
            timerMusiquePlayer.cancel();
            seekBarMusic.setProgress(0);

            musiquePlayer.release();
            musiquePlayer = null;
        }
    }

/*    @Override
    public void onDestroy() {
        super.onDestroy();
        if (musiquePlayer != null) musiquePlayer.release();
    }*/

    /*Gestion du seekBar (modification de sa valeur déplace dans la musique*/


}