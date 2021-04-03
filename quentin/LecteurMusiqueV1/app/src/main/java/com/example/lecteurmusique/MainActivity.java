package com.example.lecteurmusique;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.media.session.MediaButtonReceiver;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
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
    private NotificationCompat.Builder notifBuilder;                     //Inititalisation notification
    private NotificationManagerCompat notifManagerCompat;                //Création d'une gestion de notification decompatibilité



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
                if (focusChange==AudioManager.AUDIOFOCUS_LOSS) {
                    musiquePlayer.pause();
                    handlerTemps.removeCallbacks(runnableTemps);
                }
            }
        };


        //Inititalisation de la notification
        notificationInit();



/*        //Notification BroadcastReceiver pour récupérer les appuis sur les touches de controles de la notification de controle musique

//private BroadcastReceiver notifBroadcastReceiver;//Création d'un BroadcastReceiver pour récupérer les commmandes de la notification de contrôle
MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE)
        notifBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("TEST","Réception intent appui bouton notif");
                txtViewMusiqueTemps.setText("fontionne");
            }
        };

            private LocalBroadcastManager notibroadcasttest;
        */
    }


    public void notificationInit() {
        notifBuilder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID);//Inititalisation notification
        notifBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);            //Rend visible la notification quand le téléphone est vérouillé et permet le controle de la musique
        notifBuilder.setSmallIcon(R.drawable.image_notif_musique);                   //Image de la notification
        notifBuilder.setContentTitle("My notification");                             //Titre de la notification
        notifBuilder.setContentText("Much longer text that cannot fit one line..."); //Text de la notification
        notifBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);               //Défini la priorité de la notification
        notifBuilder.setOngoing(true);                                               //Empêche l'utilisateur de supprimer la notification
        notifBuilder.setNotificationSilent();                                        //Désactive le son de la notification
        //notifBuilder.setAutoCancel(true);                                            //Supprime la notification si on appuit dessus


        Intent musiquePlayerIntent = new Intent(this, MainActivity.class);//Déclaration Intent pour retourner sur la page de la musique
        PendingIntent musiquePlayerPenInt = PendingIntent.getActivity(this, 0, musiquePlayerIntent, 0); //Déclaration d'un pendingIntent pour utiliser l'intent précédent dans une notification
        notifBuilder.setContentIntent(musiquePlayerPenInt);                          //Ajoute l'intent à l'appui sur la notification (retour application)





////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Déclaration des Intents et PenIntents pour le contrôle de la musique sur la notification
        Intent musiqueIntentPrecedent = new Intent(this, MusiqueBroadcastReceiver.class)
            .setAction("PRECEDENT")
            .putExtra("Test",0);
        PendingIntent musiquePenIntPrecedent = PendingIntent.getBroadcast(this,1,musiqueIntentPrecedent,0);

        Intent musiqueIntentSuivant = new Intent(this, MusiqueBroadcastReceiver.class)
                .setAction("SUIVANT")
                .putExtra("Test",0);
        PendingIntent musiquePenIntSuivant = PendingIntent.getBroadcast(this, 0, musiqueIntentSuivant, 0);

        Intent musiqueIntentDemaPause = new Intent(this, MusiqueBroadcastReceiver.class)
                .setAction("DEMAPAUSE")
                .putExtra("Test",0);
        PendingIntent musiquePenIntDemaPause = PendingIntent.getBroadcast(this, 0, musiqueIntentDemaPause, 0);
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////





        //Ajout des boutons à la notification pour le contrôle musique
        notifBuilder.addAction(R.drawable.image_precedent, "Précédent", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE));//Ajout le bouton "musique précédente à la notification"
        notifBuilder.addAction(R.drawable.image_pause, "Démarrer/Pause", musiquePenIntDemaPause);//Ajout le bouton "musique Demarrer/Pause à la notification"
        notifBuilder.addAction(R.drawable.image_suivant, "Suivant", musiquePenIntSuivant);//Ajout le bouton "musique suivante à la notification"

        notifBuilder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()//Défini le style de notification en "notification de médias"
                .setShowActionsInCompactView(0,1,2));//Ajoute les boutons à la notification en mode compacté


        notifManagerCompat = NotificationManagerCompat.from(MainActivity.this);//Création d'une gestion de notification

        //Gestion si l'utilisateur utilise Android 8.0 ou supérieur
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notifChannel = new NotificationChannel(CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);//Création d'un Channel de notification pour les notifications d'Android 8.0 ou supérieur
            NotificationManager notificationManager = getSystemService(NotificationManager.class);//Création d'un NotificationManager pour les notifications d'Android 8.0 ou supérieur
            notificationManager.createNotificationChannel(notifChannel);//Création du channel de notificatios
        }
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
            musiquePlayer.start();//Démarre la musique
            notifManagerCompat.notify(NOTIFICATION_ID, notifBuilder.build());//Démarra la notification de contrôle musique
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

    @SuppressLint("SetTextI18n")
    public void musiqueArret(View view)
    {
        if (musiquePlayer!=null)
        {
            musiquePlayer.release();
            musiquePlayer=null;
            seekBarMusique.setProgress(0);
            txtViewMusiqueTemps.setText("00:00");
            txtViewMusiqueDuree.setText("00:00");
            notifManagerCompat.cancel(NOTIFICATION_ID);//Arrête la notification de contrôle musique
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
