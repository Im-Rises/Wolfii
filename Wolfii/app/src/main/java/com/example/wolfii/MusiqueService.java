package com.example.wolfii;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static java.lang.Integer.parseInt;

public class MusiqueService extends Service {

    public static boolean estActif=false;//Variable qui définis si le service est en état de fonctionnement ou arrêté

    private MediaPlayer musiquePlayer;//Lecture musique
    private AudioManager musiqueManager;//AudioManager pour appeler la gestion de l'interruption musique via musiqueFocusmanager

    private AudioFocusRequest musiqueFocusRequest;//AudioFocusRequest pour les demande de focus audio pour Andorid 8.0 ou supérieur

    private static final String CHANNEL_ID = "NotifControlMusique";             //ID notification de control musique
    private static final String NOTIFICATION_CHANNEL_NAME = "NotifChannelName"; //CHANNEL name notification de control musique
    private static final int NOTIFICATION_ID = 1;                               //Notification numéro
    private NotificationManagerCompat notifManagerCompat;                //Création d'une gestion de notification de compatibilité

    private Handler handlerTemps = new Handler();               //Handler pour appeler toutes les secondes le runnable

    private final IBinder binder = new LocalBinder();    // Binder given to clients

    public static ArrayList<Musique> maMusique = new ArrayList<Musique>();//Liste des musiques en cours à lire
    private int positionMusique;//Position de la musique en cours de lecture

    //Variables de redirection vers le BoradcastReceiver de l'activité pour la mise  à jour de la page ControleMusiqueFragment.java
    private static final String DIRECTION_ACTIVITY = "TO_ACTIVITY";
    private static final String TYPE_MAJ = "TYPE_MAJ";
    private static final String EXTRA_MAJ_INIT = "CMD_MAJ_INIT";
    private static final String EXTRA_MAJ_SIMPLE = "CMD_MAJ_SIMPLE";
    private static final String EXTRA_MAJ_FIN = "CMD_MAJ_FIN";

    //Variables de redirection vers le service (ici) pour la réception des commandes des boutons notification
    private static final String DIRECTION_SERVICE = "TO_SERVICE";
    private static final String TYPE_NOTIFICATION = "TYPE_NOTIFICATION";

    private boolean enPauseParUtilisateur = true;//Variable booléenne qui informe si la mise en pause de l'application est par l'utilisateur
    private boolean enPauseParDemandeLongue = true;//Variable booléenne qui informe si la mise en pause de l'application est par une application du système autre pour un temps indéterminé

    private boolean musiqueBoucle=false;//Variable booléenne qui informe si la musique est en train de bouclé ou non

    //Pending Intent de redirection des commandes boutons de la notification
    private PendingIntent musiquePenIntRetourAppli;
    private PendingIntent musiquePenIntRejouer;
    private PendingIntent musiquePenIntPrecedent;
    private PendingIntent musiquePenIntDemaPause;
    private PendingIntent musiquePenIntSuivant;
    private PendingIntent musiquePenIntArret;

    IntentFilter intentFilterDirecService = new IntentFilter(DIRECTION_SERVICE);//Intent Filter pour la mise ne écoute sous le filtre DIRECTION_SERVICE

    private MediaSessionCompat mediaSession;//déclaration d'une média session de contrôle musique
    private boolean mediaSessionNotifInitBool = false;//Valeur booléenne pour informer si l'utilisateur a déjà inititalisé la mediaSession et la notif




////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////FONCTIONS DU CYCLE DE VIE DE LA CLASSE SERVICE///////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/*---------------------------------------------------------FONCTION ONCREATE--------------------------------------------------------------*/

    @Override
    public void onCreate() {
        super.onCreate();

        estActif=true;
        //Gestion du focus de la musique
        musiqueManager = (AudioManager) getSystemService((Context.AUDIO_SERVICE));//initialise l'AudioManager


        //Déclaration et des Intents et PenIntents pour le retour sur l'appli sur le clic de la notification
        Intent musiquePlayerIntent = new Intent(this, MainActivity.class);//Déclaration Intent pour retourner sur la page de la musique
        musiquePenIntRetourAppli = PendingIntent.getActivity(this, 0, musiquePlayerIntent, 0); //Déclaration d'un pendingIntent pour utiliser l'intent précédent dans une notification

        //Déclaration des Intents et PenIntents pour le contrôle de la musique sur la notification
        Intent musiqueIntentRejouer = new Intent()
                .setAction(DIRECTION_SERVICE)
                .putExtra(TYPE_NOTIFICATION, "REJOUER");
        musiquePenIntRejouer = PendingIntent.getBroadcast(this, 1, musiqueIntentRejouer, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent musiqueIntentPrecedent = new Intent()
                .setAction(DIRECTION_SERVICE)
                .putExtra(TYPE_NOTIFICATION, "PRECEDENT");
        musiquePenIntPrecedent = PendingIntent.getBroadcast(this, 2, musiqueIntentPrecedent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent musiqueIntentDemaPause = new Intent()
                .setAction(DIRECTION_SERVICE)
                .putExtra(TYPE_NOTIFICATION, "DEMAPAUSE");
        musiquePenIntDemaPause = PendingIntent.getBroadcast(this, 3, musiqueIntentDemaPause, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent musiqueIntentSuivant = new Intent()
                .setAction(DIRECTION_SERVICE)
                .putExtra(TYPE_NOTIFICATION, "SUIVANT");
        musiquePenIntSuivant = PendingIntent.getBroadcast(this, 4, musiqueIntentSuivant, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent musiqueIntentArret = new Intent()
                .setAction(DIRECTION_SERVICE)
                .putExtra(TYPE_NOTIFICATION, "ARRET");
        musiquePenIntArret = PendingIntent.getBroadcast(this, 5, musiqueIntentArret, PendingIntent.FLAG_UPDATE_CURRENT);
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
        Toast.makeText(getApplicationContext(),"Arrêt service",Toast.LENGTH_LONG).show();
        estActif=false;
        super.onDestroy();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////FONCTIONS DE DECLARATION DU BOUND SERVICE///////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


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


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////FONCTIONS DE MAJ DE L'INTERFACE DE LA PAGE DE CONTROLE MUSIQUE ///////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    /*---------------------------------------------------------RUNNABLE MAJ INTERFACES--------------------------------------------------------------*/


    //Gestion du déplacement de la maj auto du seekbar et des textView de la page controleur de musique
    private Runnable runnableTemps = new Runnable() {
        @Override
        public void run() {
            if (musiquePlayer != null) {
                envoieBroadcast(EXTRA_MAJ_SIMPLE);
                handlerTemps.postDelayed(this, 1000);//Remet dans la pile du handler un appel pour le Runnable (this)
            }
        }
    };


    /*-----------------------------------------------------FONCTIONS ENVOIE BROADCAST--------------------------------------------------------------*/
    public void envoieBroadcast(final String extra) {
        Intent intent = new Intent()
                .setAction(DIRECTION_ACTIVITY)
                .putExtra(TYPE_MAJ,extra);
        sendBroadcast(intent);
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////GESTION DES INTERRUPTIONS///////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*---------------------------------------------------------GESTION AUDIOFOCUS--------------------------------------------------------------*/

    //OnAudioFocusChange pour gérer les interruptions par d'autres applications de la musique
    private AudioManager.OnAudioFocusChangeListener musiqueFocusChange = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.e("Type de focusChange", " : " + focusChange);

            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN://Cas de regain du focus audio lorsqu'une application a demandé temporairement le focus audio
                {
                    if (!enPauseParDemandeLongue && !enPauseParUtilisateur)
                        musiqueDemaEtFocus();
                }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS://Cas de demande d'un focus permanent par une autre application
                    enPauseParDemandeLongue=true;
                    musiquePause();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://Cas de demande d'un focus temporaire par une autre application
                    musiquePause();
                    break;
            }
        }
    };


    /*----------------------------------------------BROADCASTRECEIVER PAUSE MUSIQUE JACK DEBRANCHEE--------------------------------------------------------------*/

    //Gestion du débranchement d'une prise jack pour écouter la musique
    private BroadcastReceiver broadcastReceiverJack = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                enPauseParDemandeLongue=true;
                musiquePause();
            }
        }
    };




////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////FONCTIONS DE GESTION DE LA MUSIQUE///////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*--------------------------------------------DEMARRAGE ET MISE EN PAUSE DE LA MUSQUE PAR L'UTILISATEUR--------------------------------------------------------------*/
    public void musiqueDemaPause() {
        if (musiquePlayer == null) {
            //Mettre ici un try catch si la musique n'est pas trouvée
            musiqueInitialisation();
            musiqueDemaEtFocus();
        }
        else if (!musiquePlayer.isPlaying())
        {
            musiqueDemaEtFocus();
        }
        else {
            musiquePause();
            enPauseParUtilisateur=true;
        }
    }

    /*--------------------------------------------INITIALISATION MUSIQUE--------------------------------------------------------------*/

    public void musiqueInitialisation() {
        musiquePlayer = MediaPlayer.create(this, maMusique.get(positionMusique).getPathUri());//Création du MediaPlayer
        musiquePlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);//Définis le mode de fonctionnement sur PARTIAL_WAKE_LOCK pour permettre à la musique de fonctionner sans être sur l'application
        musiquePlayer.setOnCompletionListener(new EcouteurMusiqueFinie());
    }

    /*--------------------------------------------DEMANDE DE FOCUS ET DEMARRAGE DE LA MUSIQUE--------------------------------------------------------------*/

    public void musiqueDemaEtFocus() {
        /*Fonction de demande d'utilisation unique des sorties audio du téléphone
        et démarrage de la musique.
         */

        handlerTemps.postDelayed(runnableTemps, 1000);

        int resultat;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes audioAttributesParametre = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            musiqueFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setOnAudioFocusChangeListener(musiqueFocusChange)
                    .setAudioAttributes(audioAttributesParametre)
                    //.setWillPauseWhenDucked(true)//Si une application demande de diminuer le volume de la musique alors la fonction musiqueFocusChange s'active (Selon Android Studio documentation c'est acceptable de ne pas l'activer pour un lecteur de musique)
                    .build();
            resultat = musiqueManager.requestAudioFocus(musiqueFocusRequest);
        } else {
            resultat = musiqueManager.requestAudioFocus(musiqueFocusChange, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }

        if (resultat == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //déclaration de l'enregistrement d'un BoradcastReceiver pour la gestion quand une prise jack est débranchée
            IntentFilter intentFilterJack = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
            registerReceiver(broadcastReceiverJack, intentFilterJack);

            envoieBroadcast(EXTRA_MAJ_INIT);//Initialisation de la page de contrôle musique
            musiquePlayer.start();//Démarre la musique
            notificationInitEtMaj();
            enPauseParDemandeLongue=false;
            enPauseParUtilisateur=false;
        }
    }

    /*--------------------------------------------MISE EN PAUSE DE LA MUSIQUE--------------------------------------------------------------*/

    public void musiquePause() {
        musiquePlayer.pause();//Mise en pause de la musique
        handlerTemps.removeCallbacks(runnableTemps);//Arret de l'handler/reunnable qui envoie des brodcast pour la mise à jour de la page de contrôle musique
        envoieBroadcast(EXTRA_MAJ_SIMPLE);//Mise à jour de la page de controle musique
        notificationInitEtMaj();
    }

    /*--------------------------------------------ARRET DE LA MUSIQUE SIMPLE(pour le changeemnt de musique)--------------------------------------------------------------*/

    public void arretSimpleMusique()//Arret simple d'une musique en cours
    {
        if (musiquePlayer != null) {
            handlerTemps.removeCallbacks(runnableTemps);//Arret de la mise à jour de la page de contrôle musique pour le resynchroniser avec une potentielle future musique

            musiquePlayer.release();//Vidage de la mémoire de musique
            musiquePlayer = null;
        }
    }

    /*--------------------------------------------ARRET TOTAL MUSIQUE POUR L'ARRET DE LECTURE DE MUSIQUE--------------------------------------------------------------*/

    public void arretTotalMusique()//Arret appelé dès qu'on arrête toute lecture en cours
    {
        if (musiquePlayer != null) {
            arretSimpleMusique();

            //Abandon du focus audio en fonction de la version d'android
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                musiqueManager.abandonAudioFocusRequest(musiqueFocusRequest);
            } else {
                musiqueManager.abandonAudioFocus(musiqueFocusChange);
            }

            unregisterReceiver(broadcastReceiverNotifCmd);//Arret du BroadcastReceiver de réception des commandes de notification
            unregisterReceiver(broadcastReceiverJack);//Arret du BroadcastReceiver de réception du débranchement d'une prise jack

            stopForeground(true);
            arretMediaSession();
            envoieBroadcast(EXTRA_MAJ_FIN);
            mediaSessionNotifInitBool=false;
        }
    }

    /*--------------------------------------------PASSAGE A LA MUSIQUE SUIVANTE--------------------------------------------------------------*/

    public void musiqueSuivante() {
        //Remplacer musique arret par une autre fonction qui arrête pas la notif etc...
        arretSimpleMusique();
        positionMusique++;

        if (positionMusique >= maMusique.size())
            positionMusique = 0;

        //On appelle la fonction musiqueDemaPause afin d'initialiser la musique et la lancer car avant elle a été mise à nulle
        musiqueDemaPause();
        envoieBroadcast(EXTRA_MAJ_INIT);
    }

    /*--------------------------------------------PASSAGE A LA MUSIQUE PRECEDENTE--------------------------------------------------------------*/

    public void musiquePrecedente() {
        //Remplacer musique arret par une autre fonction qui arrête pas la notif etc...
        arretSimpleMusique();
        positionMusique--;

        if (positionMusique < 0)
            positionMusique=maMusique.size()-1;

        //On appelle la fonction musiqueDemaPause afin d'initialiser la musique et la lancer car avant elle a été mise à nulle
        musiqueDemaPause();
        envoieBroadcast(EXTRA_MAJ_INIT);
    }

    /*--------------------------------------------MISE EN BOUCLE DE LA MUSIQUE--------------------------------------------------------------*/

    public void musiqueBoucleDeboucle() {
        musiqueBoucle = !musiqueBoucle;
        notificationInitEtMaj();
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////GESTION FIN DE MUSIQUE///////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    /*-----------------------------------------------------GESTION ARRIVEE EN FIN DE MUSIQUE--------------------------------------------------------------*/

    private class EcouteurMusiqueFinie implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            //Si on arrive au bout de la musique et qu'elle n'est pas en mode boucle
            //on passe à la musique suivante (pas de besoin de véirifer si la musique boucle)
            if (musiqueBoucle)
            {
                musiqueDemaEtFocus();
            }
            else
            {
                musiqueSuivante();
            }
        }
    }





////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////GESTION NOTIFICATION/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /*--------------------------------------------INITIALISATION ET MISE A JOUR DE LA NOTIFICATION--------------------------------------------------------------*/

    public void notificationInitEtMaj() {

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(MusiqueService.this, CHANNEL_ID);//Inititalisation d'un constructeur de notification

        notifBuilder.setVisibility(NotificationCompat.VISIBILITY_SECRET);               //Rend invisible la notification quand le téléphone est vérouillé et permet le controle de la musique
        notifBuilder.setLargeIcon(recupImageMusique());                                 //Ajoute l'image de la musique lu à la notification
        notifBuilder.setSmallIcon(R.drawable.loup);                                     //Icone de la notification
        notifBuilder.setContentTitle(maMusique.get(positionMusique).getName());         //Titre de la notification
        notifBuilder.setContentText(maMusique.get(positionMusique).getAuthor());        //Text de la notification
        notifBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);                  //Défini la priorité de la notification
        notifBuilder.setOngoing(true);                                                  //Empêche l'utilisateur de supprimer la notification
        notifBuilder.setNotificationSilent();                                           //Désactive le son de la notification
        notifBuilder.setSubText(":  " + (positionMusique + 1) + "/" + maMusique.size());//Donne le numéro de la musique sur la playlist en cours
        notifBuilder.setShowWhen(false);                                                //Enlève l'affichage de l'heure à laquelle la notification est apaprue
        notifBuilder.setContentIntent(musiquePenIntRetourAppli);                        //Ajoute l'intent à l'appui sur la notification (retour application)
        notifBuilder.setDeleteIntent(musiquePenIntArret);                               //PendingIntent qui prend place lors du swipe de la notification sur Android 11

        //Enregistrement du BroafcastRecevier sous l'écoute du message ACTION_STRING_SERVICE (pour recevoir les commandes boutons)
        registerReceiver(broadcastReceiverNotifCmd, intentFilterDirecService);

        //Ajout des boutons à la notification pour le contrôle musique
        if (musiqueBoucle) {
            notifBuilder.addAction(R.drawable.image_rejoue, "Rejouer", musiquePenIntRejouer);   //Ajout le bouton "musique rejouer" à la notification"
        } else {
            notifBuilder.addAction(R.drawable.image_rejouer, "Rejouer", musiquePenIntRejouer);  //Ajout le bouton "musique rejouer" à la notification"
        }

        notifBuilder.addAction(R.drawable.image_precedent, "Précédent", musiquePenIntPrecedent); //Ajout le bouton "musique précédente à la notification"

        if (musiquePlayer.isPlaying())
        {
            notifBuilder.addAction(R.drawable.image_pause, "Pause", musiquePenIntDemaPause);    //Ajout le bouton "musique Demarrer/Pause à la notification"
        }
        else
        {
            notifBuilder.addAction(R.drawable.image_lecture, "Dema", musiquePenIntDemaPause);   //Ajout le bouton "musique Demarrer/Pause à la notification"
        }

        notifBuilder.addAction(R.drawable.image_suivant, "Suivant", musiquePenIntSuivant);      //Ajout le bouton "musique suivante à la notification"
        notifBuilder.addAction(R.drawable.image_nettoyer, "Arret", musiquePenIntArret);         //Ajout le bouton "musique arret" à la notification"

        //Si déjà initialisé une fois alors on fait une mise à jour de la mediaSession
        if (!mediaSessionNotifInitBool)
        {
            mediaSessionInit();
        }
        else
        {
            mediaSessionBoutonsMaj();
            mediaSessionDonneesMaj();
        }

        notifBuilder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()//Défini le style de notification en "notification de médias"
                .setShowActionsInCompactView(1, 2, 3)//Ajoute les boutons à la notification en mode compacté
                .setMediaSession(mediaSession.getSessionToken())//Ajout de la MediaSession au MediaStyle permet à Android 11 ou supérieur d'analyser la notification comme une gestion de musique
        );

        //Si une notification a déjà été mise au premier plan alors on la met juste à jour avec la fonction notify()
        if (!mediaSessionNotifInitBool)
        {
            notifManagerCompat = NotificationManagerCompat.from(MusiqueService.this);//Création d'une gestion de notification

            //Gestion si l'utilisateur utilise Android 8.0 ou supérieur
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notifChannel = new NotificationChannel(CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);//Création d'un Channel de notification pour les notifications d'Android 8.0 ou supérieur
                notifChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);//Création d'un NotificationManager pour les notifications d'Android 8.0 ou supérieur
                notificationManager.createNotificationChannel(notifChannel);//Création du channel de notificatios
            }

            startForeground(NOTIFICATION_ID, notifBuilder.build());//Démarre le service au premier-plan afin de permettre de continuer la musique après l'avoir fermé
        }
        else
        {
            notifManagerCompat.notify(NOTIFICATION_ID,notifBuilder.build());
        }

        mediaSessionNotifInitBool = true;//Attribue la valeur déjà initialisé une fois à notre variable booléenne
    }


    /*---------------------------------------------------------BORADCASTRECEIVER DES BOUTONS DE LA NOTIFICATION--------------------------------------------------------------*/

    private BroadcastReceiver broadcastReceiverNotifCmd = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getStringExtra(TYPE_NOTIFICATION)) {
                case "REJOUER":
                    musiqueBoucleDeboucle();
                    break;
                case "DEMAPAUSE":
                    musiqueDemaPause();
                    break;
                case "PRECEDENT":
                    musiquePrecedente();
                    break;
                case "SUIVANT":
                    musiqueSuivante();
                    break;
                case "ARRET":
                    arretTotalMusique();
                    if (!MainActivity.estActif)
                    {
                        stopSelf();
                    }
                    break;
            }
        }
    };

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////FONCTIONS DE MEDIASESSION/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /*---------------------------------------------------------FONCTION MEDIA SESSION--------------------------------------------------------------*/

    public void mediaSessionInit() {

        // Create a MediaSessionCompat
        mediaSession = new MediaSessionCompat(getApplicationContext(), "MEDIASESSION");

        // Enable callbacks from MediaButtons and TransportControls
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mediaSessionBoutonsMaj();

        mediaSessionDonneesMaj();

        mediaSession.setCallback(new EcouteurMediaSessionCompat());

        mediaSession.setActive(true);
    }


    //Class d'écoute d'appui sur les boutons de MediaSession
    private class EcouteurMediaSessionCompat extends MediaSessionCompat.Callback{
        @Override
        public void onPlay() {
            super.onPlay();
            //musiqueDemaEtFocus();
            musiqueDemaPause();
        }

        @Override
        public void onPause() {
            super.onPause();
            //musiquePause();
            musiqueDemaPause();
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            musiqueSuivante();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            musiquePrecedente();
        }

        //Seekabr de MediaSession
        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            musiquePlayer.seekTo((int) pos);
            mediaSessionBoutonsMaj();
        }
    }

    public void mediaSessionDonneesMaj()
    {
        //Intialisation des données de la musiques
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, maMusique.get(positionMusique).getAuthor())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, maMusique.get(positionMusique).getName())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, parseInt(maMusique.get(positionMusique).getDuration()))
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, recupImageMusique())
                .build()
        );
    }

    public void mediaSessionBoutonsMaj()
    {
        PlaybackStateCompat.Builder playBackBuilderTempo = new PlaybackStateCompat.Builder();

        if (musiquePlayer.isPlaying())
        {
            playBackBuilderTempo.setState(PlaybackStateCompat.STATE_PLAYING, musiquePlayer.getCurrentPosition(), 1);
        }
        else
        {
            playBackBuilderTempo.setState(PlaybackStateCompat.STATE_PAUSED, musiquePlayer.getCurrentPosition(), 1);
        }
        playBackBuilderTempo.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PLAY |
                PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS | PlaybackStateCompat.ACTION_SEEK_TO);

        mediaSession.setPlaybackState(playBackBuilderTempo.build());
    }

    private void arretMediaSession() {
        mediaSession.setActive(false);
        mediaSession.release();
    }




////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////FONCTIONS DE TRAITEMENT D'IMAGES/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*----------------------------------------------------FONCTIONS RECUPERTATION/CONVERSION IMAGE--------------------------------------------------------------*/

    public Bitmap recupImageMusique() {
        MediaMetadataRetriever mediaMetadataRechercheur = new MediaMetadataRetriever();
        mediaMetadataRechercheur.setDataSource(maMusique.get(positionMusique).getPath());

        byte [] image = mediaMetadataRechercheur.getEmbeddedPicture();

        mediaMetadataRechercheur.release();

        if (image!=null)
            //Si une image n'est trouvé dans le fichier audio
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        else
        {
            //Si aucune image n'est trouvé dans le fichier mp3 alors on met le logo loup.png
            return drawableEnBitmap(R.drawable.loup);
        }
    }


    /*--------------------------------------------FONCTION DE CONVERSION D'IMAGE DRAWABLE EN BITMAP-------------------------------------------------------------*/

    public Bitmap drawableEnBitmap(int drawableRes) {
        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////GETTERS ET SETTERS/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*--------------------------------------------------------------FONCTIONS GETTER--------------------------------------------------------------*/

    public int getMusiquePlayerPosition() {
        return musiquePlayer.getCurrentPosition();
    }

    public int getMusiquePlayerDuration() {
        return musiquePlayer.getDuration();
    }

    public boolean getMusiquePlayerIsPlaying() {
        return musiquePlayer.isPlaying();
    }

    public boolean getMusiquePlayerIsSet() {
        return (musiquePlayer != null);
    }

    public String getMusiqueTitre(){return maMusique.get(positionMusique).getName();}

    public ArrayList<Musique> getCurrentPlaylist() {return maMusique;}

    public int getPositionMusique() {return this.positionMusique;}

    public boolean getMusiqueBoucle(){ return musiqueBoucle;}


    /*--------------------------------------------------------------FONCTIONS SETTER--------------------------------------------------------------*/

    public void setMusiquePlayerPosition(int seekBarPosition) {
        musiquePlayer.seekTo(seekBarPosition);
    }

    public ArrayList<Musique> copyArrayList(ArrayList<Musique> musiques) {
        ArrayList<Musique> mus = new ArrayList<Musique>();
        for (Musique m : musiques)
            mus.add(m);
        return mus;
    }

    public void setMusiquePlaylist(ArrayList<Musique> musique, int position) {
        maMusique = copyArrayList(musique);
        this.positionMusique = position;
    }
}
