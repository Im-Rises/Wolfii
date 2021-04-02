package com.example.lecteurmusique;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private SeekBar seekBarMusique;                             //SeekBar de lecture de la msuqiue
    private TextView txtViewMusiqueTemps,txtViewMusiqueDuree;   //TextView du temps de lecture de la musique

    private MediaPlayer musiquePlayer;                          //Lecture musique
    private Handler handlerTemps = new Handler();               //Handler pour appeler toutes les secondes le runnable
    private Runnable runnableTemps;                             //Runnable pour mettre à jour toutes les secondes le seekbar et les temps relatifs à la musique

    private AudioManager musiqueManager;                        //AudioManager pour appeler la gestion de l'interruption musique via musiqueFocusmanager
    private AudioManager.OnAudioFocusChangeListener musiqueFocusChange;//OnAudioFocusChange pour gérer les interruptions par d'autres applications de la musique

    private static final String CHANNEL_ID = "NotifControlMusique";             //ID notification de control musique
    private static final String NOTIFICATION_CHANNEL_NAME = "NotifChannelName"; //CHANNEL name notification de control musique
    private static final int NOTIFICATION_ID = 1;                               //Notification numéro


    //Fonction d'apppel lors de la création de la page
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
                    handlerTemps.postDelayed(this, 400);
                }
            }
        };


        //Gestion du focus de la musique
        musiqueManager = (AudioManager) getSystemService((Context.AUDIO_SERVICE));      //initialise l'AudioManager

        //Gestion de l'interruption de la musique par une autre application
        musiqueFocusChange = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                if (focusChange==AudioManager.AUDIOFOCUS_LOSS)
                    musiquePlayer.pause();
            }
        };


        //Inititalisation de la notification
        notificationInit();
    }



    public void notificationInit()
    {
        Intent musiquePlayerIntent = new Intent(this,MainActivity.class);           //Déclaration Intent pour retourner sur la page de la musique
        PendingIntent musiquePlayerPenInt = PendingIntent.getActivity(this, 0, musiquePlayerIntent, 0); //Déclaration d'un pendingIntent pour utiliser l'intent précédent dans une notification

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID);//Inititalisation notification
        builder.setSmallIcon(R.drawable.image_notif_musique);                   //Image de la notification
        builder.setContentTitle("My notification");                             //Titre de la notification
        builder.setContentText("Much longer text that cannot fit one line..."); //Text de la notification
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);               //Défini la priorité de la notification
        builder.setOngoing(true);                                               //Empêche l'utilisateur de supprimer la notification
        builder.setNotificationSilent();                                        //Désactive le son de la notification
        builder.setContentIntent(musiquePlayerPenInt);                          //Ajoute l'intent à l'appui sur la notification (retour application)
        //builder.setAutoCancel(true);                                            //Supprime la notification si on appuit dessus

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);//Création d'une gestion de notification

        //Gestion si l'utilisateur utilise Android 8.0 ou supérieur
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        managerCompat.notify(NOTIFICATION_ID, builder.build());//Appel la notification builder
    }



    public void musiqueDemaEtFocus()
    {
        /*
        Fonction de demande d'utilisation unique des sorties audio du téléphone
        et démarrage de la musique.
         */
        int result = musiqueManager.requestAudioFocus(musiqueFocusChange,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
        {
            musiquePlayer.start();
        }
    }

    public void musiqueDemaPause(View view) {

        if (musiquePlayer == null) {
            musiquePlayer = MediaPlayer.create(this, R.raw.musiquetest);
            txtViewMusiqueDuree.setText(millisecondesEnMinutesSeconde(musiquePlayer.getDuration()));
            musiquePlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            seekBarMusique.setMax(musiquePlayer.getDuration());
            musiquePlayer.seekTo(seekBarMusique.getProgress());
            handlerTemps.postDelayed(runnableTemps, 400);
            //musiquePlayer.start();
            musiqueDemaEtFocus();
        }
        else if (!musiquePlayer.isPlaying()) {
            handlerTemps.postDelayed(runnableTemps,400);
            //musiquePlayer.start();
            musiqueDemaEtFocus();
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
            txtViewMusiqueDuree.setText("00:00");
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
