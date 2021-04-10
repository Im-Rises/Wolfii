
package com.example.lecteurmusique;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

public class MusiqueService extends Service {

    private MediaPlayer musiquePlayer;//Lecture musique
    private AudioManager musiqueManager;//AudioManager pour appeler la gestion de l'interruption musique via musiqueFocusmanager
    private AudioManager.OnAudioFocusChangeListener musiqueFocusChange;//OnAudioFocusChange pour gérer les interruptions par d'autres applications de la musique
    private AudioFocusRequest musiqueFocusRequest;//AudioFocusRequest pour les demande de focus audio pour Andorid 8.0 ou supérieur


    private static final String CHANNEL_ID = "NotifControlMusique";             //ID notification de control musique
    private static final String NOTIFICATION_CHANNEL_NAME = "NotifChannelName"; //CHANNEL name notification de control musique
    private static final int NOTIFICATION_ID = 1;                               //Notification numéro
    private NotificationCompat.Builder notifBuilder;                     //Inititalisation notification
    private NotificationManagerCompat notifManagerCompat;                //Création d'une gestion de notification de compatibilité


    private Handler handlerTemps = new Handler();               //Handler pour appeler toutes les secondes le runnable
    private Runnable runnableTemps;                             //Runnable pour mettre à jour toutes les secondes le seekbar et les temps relatifs à la musique


    private final IBinder binder = new LocalBinder();    // Binder given to clients


    private static final String ACTION_STRING_ACTIVITY = "ToActivity";



//-----------------------------------------------------------------GESTION BOUND CALLBACK SERVICE-----------------------------------------------------------------------------

    public class LocalBinder extends Binder {
        MusiqueService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MusiqueService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }



/*---------------------------------------------------------FONCTIONS DU CYCLE DE VIE DE LA CLASSE SERVICE--------------------------------------------------------------*/

    @Override
    public void onCreate() {
        super.onCreate();

        //Gestion du focus de la musique
        musiqueManager = (AudioManager) getSystemService((Context.AUDIO_SERVICE));//initialise l'AudioManager

        //Gestion de l'interruption de la musique par une autre application
        musiqueFocusChange = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                    musiquePause();
                }
            }
        };


        //Gestion du déplacement de la maj auto du seekbar et des textView
        runnableTemps = new Runnable() {
            @Override
            public void run() {
                if (musiquePlayer != null) {

/*                    seekBarMusique.setProgress(musiquePlayer.getCurrentPosition());
                    txtViewMusiqueTemps.setText(millisecondesEnMinutesSeconde(musiquePlayer.getCurrentPosition()));*/
                    envoieBroadcast();
                    //Remet dans la pile du handler un appel pour le Runnable (this)
                    handlerTemps.postDelayed(this, 1000);
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*return super.onStartCommand(intent, flags, startId);*/
        return START_STICKY;//Si l'application est arrêté toatalement alors on redémarre le service
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }



/*---------------------------------------------------------FONCTIONS DE GESTION MUSIQUE--------------------------------------------------------------*/

    public void musiqueDemaPause()
    {
        if (musiquePlayer == null)
        {
            musiqueInitialisation();
            musiqueDemaEtFocus();
            startForeground(NOTIFICATION_ID,notificationInit());//Démarre le service en foreground afin de permettre de continuer la musique après l'avoir fermé
        }
        else if (!musiquePlayer.isPlaying())
        {
            musiqueDemaEtFocus();
        }
        else
        {
            musiquePause();
        }
    }

    public void musiqueInitialisation()
    {
        musiquePlayer = MediaPlayer.create(this, R.raw.musiquetest);//Création du MediaPlayer

/*            txtViewMusiqueDuree.setText(millisecondesEnMinutesSeconde(musiquePlayer.getDuration()));*/

        musiquePlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);//Définis le mode de fonctionnement sur PARTIAL_WAKE_LOCK pour permettre à la musique de fonctionner sans être sur l'application

/*            seekBarMusique.setMax(musiquePlayer.getDuration());
            musiquePlayer.seekTo(seekBarMusique.getProgress());*/

    }

    public void musiqueDemaEtFocus() {
/*
        Fonction de demande d'utilisation unique des sorties audio du téléphone
        et démarrage de la musique.
         */

        handlerTemps.postDelayed(runnableTemps, 1000);

        int resultat;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            musiqueFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setOnAudioFocusChangeListener(musiqueFocusChange)
                    .build();
            resultat = musiqueManager.requestAudioFocus(musiqueFocusRequest);
        }
        else
        {
            resultat = musiqueManager.requestAudioFocus(musiqueFocusChange, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }

        if (resultat == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            musiquePlayer.start();//Démarre la musique
        }
    }

    public void musiquePause()
    {
        musiquePlayer.pause();
        /*Maj des boutons de la notif*/
        handlerTemps.removeCallbacks(runnableTemps);
    }


    public void musiqueArret()
    {
        if (musiquePlayer != null) {
            musiquePlayer.release();
            musiquePlayer = null;

/*            seekBarMusique.setProgress(0);
            txtViewMusiqueTemps.setText("00:00");
            txtViewMusiqueDuree.setText("00:00");*/


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                musiqueManager.abandonAudioFocusRequest(musiqueFocusRequest);
            }
            else
            {
                musiqueManager.abandonAudioFocus(musiqueFocusChange);
            }

            stopForeground(true);
            //notifManagerCompat.cancel(NOTIFICATION_ID);//Arrête la notification de contrôle musique
        }
        stopSelf();
    }


    public void musiqueSuivante()
    {

    }

    public void musiquePrecedente()
    {

    }

    public void musiqueBoucleDeboucle()
    {
        musiquePlayer.setLooping(!musiquePlayer.isLooping());
    }


/*-----------------------------------------------------FONCTIONS MAJ INTERFCE ET NOTIFICATION--------------------------------------------------------------*/
    public void notifUpdate()
    {

    }


/*-----------------------------------------------------FONCTIONS ENVOIE BROADCAST--------------------------------------------------------------*/
    public void envoieBroadcast()
    {
        Intent new_intent = new Intent();
        new_intent.setAction(ACTION_STRING_ACTIVITY);
        sendBroadcast(new_intent);
    }

/*--------------------------------------------------------------FONCTIONS GETTER--------------------------------------------------------------*/

    public int getMusiquePlayerPosition()
    {
        return musiquePlayer.getCurrentPosition();
    }

    public int getMusiquePlayerDuration()
    {
        return musiquePlayer.getDuration();
    }

    public boolean getMusiquePlayerIsPlaying()
    {
        return musiquePlayer.isPlaying();
    }

    public boolean getMusiquePlayerIsSet(){ return (musiquePlayer != null); }


/*--------------------------------------------------------------FONCTIONS SETTER--------------------------------------------------------------*/

    public void setMusiquePlayerPosition(int seekBarPosition){
        musiquePlayer.seekTo(seekBarPosition);
    }


/*---------------------------------------------------------FONCTION GESTION NOTIFICATION--------------------------------------------------------------*/


    public Notification notificationInit() {
        notifBuilder = new NotificationCompat.Builder(MusiqueService.this, CHANNEL_ID);//Inititalisation notification
        notifBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);            //Rend visible la notification quand le téléphone est vérouillé et permet le controle de la musique
        notifBuilder.setSmallIcon(R.drawable.image_notif_musique);                   //Image de la notification
        notifBuilder.setContentTitle("My notification");                             //Titre de la notification
        notifBuilder.setContentText("Much longer text that cannot fit one line..."); //Text de la notification
        //notifBuilder.setLargeIcon();                                                 //Ajoute l'image de la musique lu à la notification
        notifBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);               //Défini la priorité de la notification
        notifBuilder.setOngoing(true);                                               //Empêche l'utilisateur de supprimer la notification
        notifBuilder.setNotificationSilent();                                        //Désactive le son de la notification
        //notifBuilder.setAutoCancel(true);                                            //Supprime la notification si on appuit dessus


        Intent musiquePlayerIntent = new Intent(this, MainActivity.class);//Déclaration Intent pour retourner sur la page de la musique
        PendingIntent musiquePlayerPenInt = PendingIntent.getActivity(this, 0, musiquePlayerIntent, 0); //Déclaration d'un pendingIntent pour utiliser l'intent précédent dans une notification
        notifBuilder.setContentIntent(musiquePlayerPenInt);                          //Ajoute l'intent à l'appui sur la notification (retour application)


        /////////////////////////////////////////////////////Gestion boutons notification/////////////////////////////////////////////////////////////////////
        //Déclaration des Intents et PenIntents pour le contrôle de la musique sur la notification

        Intent musiqueIntentRejouer = new Intent(this, MusiqueBroadcastReceiver.class)
                .setAction("Rejouer");
        PendingIntent musiquePenIntRejouer = PendingIntent.getBroadcast(this, 0, musiqueIntentRejouer, 0);

        Intent musiqueIntentPrecedent = new Intent(this, MusiqueBroadcastReceiver.class)
                .setAction("PRECEDENT");
        PendingIntent musiquePenIntPrecedent = PendingIntent.getBroadcast(this, 1, musiqueIntentPrecedent, 0);

        Intent musiqueIntentSuivant = new Intent(this, MusiqueBroadcastReceiver.class)
                .setAction("SUIVANT");
        PendingIntent musiquePenIntSuivant = PendingIntent.getBroadcast(this, 0, musiqueIntentSuivant, 0);

        Intent musiqueIntentDemaPause = new Intent(this, MusiqueBroadcastReceiver.class)
                .setAction("DEMAPAUSE");
        PendingIntent musiquePenIntDemaPause = PendingIntent.getBroadcast(this, 0, musiqueIntentDemaPause, 0);

        Intent musiqueIntentArret = new Intent(this, MusiqueBroadcastReceiver.class)
                .setAction("ARRET");
        PendingIntent musiquePenIntArret = PendingIntent.getBroadcast(this, 0, musiqueIntentArret, 0);
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //Ajout des boutons à la notification pour le contrôle musique
        notifBuilder.addAction(R.drawable.image_rejouer, "Rejouer", musiquePenIntRejouer);//Ajout le bouton "musique rejouer" à la notification"
        notifBuilder.addAction(R.drawable.image_precedent, "Précédent", musiquePenIntPrecedent);//Ajout le bouton "musique précédente à la notification"
        notifBuilder.addAction(R.drawable.image_pause, "Démarrer/Pause", musiquePenIntDemaPause);//Ajout le bouton "musique Demarrer/Pause à la notification"
        notifBuilder.addAction(R.drawable.image_suivant, "Suivant", musiquePenIntSuivant);//Ajout le bouton "musique suivante à la notification"
        notifBuilder.addAction(R.drawable.image_arret, "Arret", musiquePenIntArret);//Ajout le bouton "musique arret" à la notification"


        notifBuilder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()//Défini le style de notification en "notification de médias"
                .setShowActionsInCompactView(1, 2, 3));//Ajoute les boutons à la notification en mode compacté


        notifManagerCompat = NotificationManagerCompat.from(MusiqueService.this);//Création d'une gestion de notification

        //Gestion si l'utilisateur utilise Android 8.0 ou supérieur
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notifChannel = new NotificationChannel(CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);//Création d'un Channel de notification pour les notifications d'Android 8.0 ou supérieur
            NotificationManager notificationManager = getSystemService(NotificationManager.class);//Création d'un NotificationManager pour les notifications d'Android 8.0 ou supérieur
            notificationManager.createNotificationChannel(notifChannel);//Création du channel de notificatios
        }

        return notifBuilder.build();
    }
}
