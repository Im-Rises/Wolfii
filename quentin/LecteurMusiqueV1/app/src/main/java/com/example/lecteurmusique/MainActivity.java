package com.example.lecteurmusique;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private SeekBar seekBarMusique;
    private TextView txtViewMusiqueTemps,txtViewMusiqueDuree;

    private MediaPlayer musiquePlayer;
    private Handler handlerTemps = new Handler();
    private Runnable runnableTemps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Code qui lie les objets du activity_main.xml à ceux dy MainActivity.xml*/
        this.txtViewMusiqueTemps = (TextView) findViewById(R.id.txtViewMusiqueTemps);
        this.txtViewMusiqueDuree = (TextView) findViewById(R.id.txtViewMusiqueDuree);
        this.seekBarMusique = (SeekBar) findViewById(R.id.seekBarMusique);


        //Gestion du déplacement par l'utilisateur du seekbar
        seekBarMusique.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (musiquePlayer != null) {
                    if (fromUser) {
                        musiquePlayer.seekTo(progress);
                    }
                    txtViewMusiqueTemps.setText(millisecondesEnMinutesSeconde(musiquePlayer.getCurrentPosition()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        //Gestion du déplacement de la maj auto du seekbar et des textView
        runnableTemps = new Runnable() {
            @Override
            public void run() {
                if (musiquePlayer != null) {
                    seekBarMusique.setProgress(musiquePlayer.getCurrentPosition());
                    txtViewMusiqueTemps.setText(millisecondesEnMinutesSeconde(musiquePlayer.getCurrentPosition()));
                    //Remet dans la pile du handler un appel pour le Runnable (this)
                    handlerTemps.postDelayed(this,400);
                }
            }
        };

    }





    //Code de test de notification pour la gestion de la musique en dehors de l'application
    public void test(View view) {

        //Déclaration Intent de retour à la page de lecture musique
        Intent musiquePlayerIntent = new Intent(this,MainActivity.class);
        PendingIntent musiquePlayerPenInt = PendingIntent.getActivity(this, 0, musiquePlayerIntent, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "MyNotiftest");
        builder.setSmallIcon(R.drawable.image_notif_musique);
        builder.setContentTitle("My notification");
        builder.setContentText("Much longer text that cannot fit one line...");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setOngoing(true);//Empêche l'utilisateur de supprimer la notification

        builder.setContentIntent(musiquePlayerPenInt);//Ajoute l'intent à l'appui sur la notification
        builder.setAutoCancel(true);//Supprime la notification si on appuit dessus

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);//Création d'une gestion de notification
        managerCompat.notify(1, builder.build());//Appel la notification builder
    }


    public void musiqueDemaPause(View view) {
        if (musiquePlayer == null) {
            musiquePlayer = MediaPlayer.create(this, R.raw.musiquetest);
            musiquePlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            seekBarMusique.setMax(musiquePlayer.getDuration());
            musiquePlayer.seekTo(seekBarMusique.getProgress());
            handlerTemps.postDelayed(runnableTemps,400);
            musiquePlayer.start();
        }
        else if (!musiquePlayer.isPlaying()) {
            handlerTemps.postDelayed(runnableTemps,400);
            musiquePlayer.start();
        }
        else
        {
            musiquePlayer.pause();
            handlerTemps.removeCallbacks(runnableTemps);
        }
    }

    public void musiqueArret(View view)
    {
        if (musiquePlayer!=null)
        {
            musiquePlayer.release();
            musiquePlayer=null;
            seekBarMusique.setProgress(0);
            txtViewMusiqueTemps.setText("00:00");
        }
    }


    @SuppressLint("DefaultLocale")
    private String millisecondesEnMinutesSeconde(int tmpsMillisecondes)
    {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(tmpsMillisecondes),
                TimeUnit.MILLISECONDS.toSeconds(tmpsMillisecondes) - TimeUnit.MILLISECONDS.toMinutes(tmpsMillisecondes)*60);

        //Autre méthode qui fonctionne pas ??? Fait planter l'application si on envoie le résultat de ce text dans un setText
        //return Integer.toString((tmpsMillisecondes/1000)/60).substring(0,2)+":"+Integer.toString(tmpsMillisecondes%60).substring(0,2);
        //txtViewMusiqueTemps.setText("99:99");
    }

}
