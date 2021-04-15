package com.example.musique_rv;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusiqueService extends Service {

    private MediaPlayer musiquePlayer;//Lecture musique
    private AudioManager musiqueManager;//AudioManager pour appeler la gestion de l'interruption musique via musiqueFocusmanager

    private AudioFocusRequest musiqueFocusRequest;//AudioFocusRequest pour les demande de focus audio pour Andorid 8.0 ou supérieur


    private static final String CHANNEL_ID = "NotifControlMusique";             //ID notification de control musique
    private static final String NOTIFICATION_CHANNEL_NAME = "NotifChannelName"; //CHANNEL name notification de control musique
    private static final int NOTIFICATION_ID = 1;                               //Notification numéro
    private NotificationCompat.Builder notifBuilder;                     //Inititalisation notification
    private NotificationManagerCompat notifManagerCompat;                //Création d'une gestion de notification de compatibilité


    private Handler handlerTemps = new Handler();               //Handler pour appeler toutes les secondes le runnable


    private final IBinder binder = new LocalBinder();    // Binder given to clients


    private static final String ACTION_STRING_ACTIVITY = "ToActivity";


    private static final String ACTION_STRING_SERVICE = "ToService";
    private static final String NAME_NOTIFICATION = "NOTIFICATION";


    private ArrayList<Musique> maMusique = new ArrayList<Musique>();
    private int positionMusique;
    /*A FAIRE :
     *
     * Corrigé le bug de la notification qui s'enlève parfois
     * Ajouter la maj de la notification et interface sur appui du bouton DemaPause ainsi que BoucleDeboucle
     *
     */

/*///////////////////////////////////////////////FONCTIONS DU CYCLE DE VIE DE LA CLASSE SERVICE//////////////////////////////////////////
/*---------------------------------------------------------FONCTION ONCREATE--------------------------------------------------------------*/

    @Override
    public void onCreate() {
        super.onCreate();

        //Gestion du focus de la musique
        musiqueManager = (AudioManager) getSystemService((Context.AUDIO_SERVICE));//initialise l'AudioManager
    }

    /*---------------------------------------------------------FONCTION ONSTARTCOMMAND--------------------------------------------------------------*/

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;//Si l'application est arrêté toatalement alors on redémarre le service
    }

    /*---------------------------------------------------------FONCTION ONDESTROY--------------------------------------------------------------*/

    @Override
    public void onDestroy() {
        //Toast.makeText(getApplicationContext(), "TEST StopSelf", Toast.LENGTH_SHORT).show();
        musiqueArret();
        super.onDestroy();
    }


//-----------------------------------------------------------------GESTION BOUND SERVICE-----------------------------------------------------------------------------

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


    /*---------------------------------------------------------RUNNABLE MAJ INTERFACES--------------------------------------------------------------*/


    //Gestion du déplacement de la maj auto du seekbar et des textView
    private Runnable runnableTemps = new Runnable() {
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


    /*---------------------------------------------------------GESTION AUDIOFOCUS--------------------------------------------------------------*/

    //OnAudioFocusChange pour gérer les interruptions par d'autres applications de la musique
    private AudioManager.OnAudioFocusChangeListener musiqueFocusChange = new AudioManager.OnAudioFocusChangeListener(){
        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.e("Type de focusChange", " : " + focusChange);

            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN://Cas de regain du focus audio lorsqu'une application a demandé temporairement le focus audio
                    musiquePlayer.start();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS://Cas de demande d'un focus permanent par une autre application
                    musiquePause();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://Cas de demande d'un focus temporaire par une autre application
                    musiquePause();
                    break;
                default:
            }
        }
    };


    /*---------------------------------------------------------FONCTIONS DE GESTION MUSIQUE--------------------------------------------------------------*/

    public void musiqueDemaPause()
    {
        if (musiquePlayer == null)
        {
            Toast.makeText(getApplicationContext(), positionMusique + "\n" + maMusique.get(positionMusique).getPathUri() , Toast.LENGTH_SHORT).show();

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
        musiquePlayer = MediaPlayer.create(this, maMusique.get(positionMusique).getPathUri());//Création du MediaPlayer
        musiquePlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);//Définis le mode de fonctionnement sur PARTIAL_WAKE_LOCK pour permettre à la musique de fonctionner sans être sur l'application
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
            AudioAttributes audioAttributesParametre = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            musiqueFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setOnAudioFocusChangeListener(musiqueFocusChange)
                    .setAudioAttributes(audioAttributesParametre)
                    //.setWillPauseWhenDucked(true)//Si une application demande de diminuer le volume de la musique alors la fonction musiqueFocusChange s'active (Selon Android Studio documentation c'est acceptable de ne pas l'activer pour un elcteur de musique)
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
        handlerTemps.removeCallbacks(runnableTemps);
        musiquePlayer.pause();
        /*Maj des boutons de la notif*/
    }


    public void musiqueArret()
    {
        if (musiquePlayer != null) {

            handlerTemps.removeCallbacks(runnableTemps);

            musiquePlayer.release();
            musiquePlayer = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                musiqueManager.abandonAudioFocusRequest(musiqueFocusRequest);
            }
            else
            {
                musiqueManager.abandonAudioFocus(musiqueFocusChange);
            }

            unregisterReceiver(broadcastReceiverNotifCmd);
            stopForeground(true);
            //notifManagerCompat.cancel(NOTIFICATION_ID);//Arrête la notification de contrôle musique
        }
    }


    public void musiqueSuivante()
    {

    }

    public void musiquePrecedente()
    {

    }

    public void musiqueBoucleDeboucle()
    {
        if (musiquePlayer!=null)
            musiquePlayer.setLooping(!musiquePlayer.isLooping());
    }


    /*-----------------------------------------------------FONCTIONS ENVOIE BROADCAST--------------------------------------------------------------*/
    public void envoieBroadcast()
    {
        Intent new_intent = new Intent();
        new_intent.setAction(ACTION_STRING_ACTIVITY);
        sendBroadcast(new_intent);
    }



    /*---------------------------------------------------------FONCTION BORADCASTRECEIVER NOTIFICATION COMMANDE--------------------------------------------------------------*/

    private BroadcastReceiver broadcastReceiverNotifCmd = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Toast.makeText(getApplicationContext(), "Message reçu", Toast.LENGTH_SHORT).show();

            switch(intent.getStringExtra(NAME_NOTIFICATION))
            {
                case "REJOUER":
                    musiqueBoucleDeboucle();
                    break;
                case "DEMAPAUSE":
                    musiqueDemaPause();
                    break;
                case "PRECEDENT":
                    break;
                case "SUIVANT":
                    break;
                case "ARRET":
                    stopSelf();
                    break;
            }
        }
    };

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
        //Enregistrement du BroafcastRecevier sous l'écoute du message ACTION_STRING_ACTIVITY
        if (broadcastReceiverNotifCmd != null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_SERVICE);
            registerReceiver(broadcastReceiverNotifCmd, intentFilter);
        }

        //Déclaration des Intents et PenIntents pour le contrôle de la musique sur la notification
        Intent musiqueIntentRejouer = new Intent()
                .setAction(ACTION_STRING_SERVICE)
                .putExtra(NAME_NOTIFICATION,"REJOUER");
        PendingIntent musiquePenIntRejouer = PendingIntent.getBroadcast(this, 1, musiqueIntentRejouer, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent musiqueIntentPrecedent = new Intent()
                .setAction(ACTION_STRING_SERVICE)
                .putExtra(NAME_NOTIFICATION,"PRECEDENT");
        PendingIntent musiquePenIntPrecedent = PendingIntent.getBroadcast(this, 2, musiqueIntentPrecedent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent musiqueIntentDemaPause = new Intent()
                .setAction(ACTION_STRING_SERVICE)
                .putExtra(NAME_NOTIFICATION,"DEMAPAUSE");
        PendingIntent musiquePenIntDemaPause = PendingIntent.getBroadcast(this, 3, musiqueIntentDemaPause, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent musiqueIntentSuivant = new Intent()
                .setAction(ACTION_STRING_SERVICE)
                .putExtra(NAME_NOTIFICATION,"SUIVANT");
        PendingIntent musiquePenIntSuivant = PendingIntent.getBroadcast(this, 4, musiqueIntentSuivant, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent musiqueIntentArret = new Intent()
                .setAction(ACTION_STRING_SERVICE)
                .putExtra(NAME_NOTIFICATION,"ARRET");
        PendingIntent musiquePenIntArret = PendingIntent.getBroadcast(this, 5, musiqueIntentArret, PendingIntent.FLAG_UPDATE_CURRENT);
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



        //Ajout des boutons à la notification pour le contrôle musique
        notifBuilder.addAction(R.drawable.image_rejouer, "Rejouer", musiquePenIntRejouer);//Ajout le bouton "musique rejouer" à la notification"
        notifBuilder.addAction(R.drawable.image_precedent, "Précédent", musiquePenIntPrecedent);//Ajout le bouton "musique précédente à la notification"
        notifBuilder.addAction(R.drawable.image_pause, "Démarrer/Pause", musiquePenIntDemaPause);//Ajout le bouton "musique Demarrer/Pause à la notification"
        notifBuilder.addAction(R.drawable.image_suivant, "Suivant", musiquePenIntSuivant);//Ajout le bouton "musique suivante à la notification"
        notifBuilder.addAction(R.drawable.image_nettoyer, "Arret", musiquePenIntArret);//Ajout le bouton "musique arret" à la notification"


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

    public ArrayList<Musique> copyArrayList(ArrayList<Musique> musiques) {
        ArrayList<Musique> mus = new ArrayList<Musique>();
        for(Musique m : musiques)
            mus.add(m);
        return mus;
    }
    public void setMusiquePlaylist(ArrayList<Musique> musique, int position) {
        maMusique = copyArrayList(musique);
        this.positionMusique = position;

    }
}
