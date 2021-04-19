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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
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
    private NotificationCompat.Builder notifBuilder;                     //Inititalisation notification
    private NotificationManagerCompat notifManagerCompat;                //Création d'une gestion de notification de compatibilité

    private Handler handlerTemps = new Handler();               //Handler pour appeler toutes les secondes le runnable

    private final IBinder binder = new LocalBinder();    // Binder given to clients

    private ArrayList<Musique> maMusique = new ArrayList<Musique>();
    private int positionMusique;

    private static final String DIRECTION_ACTIVITY = "TO_ACTIVITY";
    private static final String TYPE_MAJ = "TYPE_MAJ";
    private static final String EXTRA_MAJ_INIT = "CMD_MAJ_INIT";
    private static final String EXTRA_MAJ_SIMPLE = "CMD_MAJ_SIMPLE";

    private static final String DIRECTION_SERVICE = "TO_SERVICE";
    private static final String TYPE_NOTIFICATION = "TYPE_NOTIFICATION";

    private boolean enPauseParUtilisateur = true;
    private boolean enPauseParDemandeLongue = true;



    ////////////////////////////////////////////////TEST MEDIASESSION/////////////////////////////////////////////////
    private MediaSession mediaSession;


    public void mediaSessionInt()
    {
        PlaybackStateCompat playbackStateCompat;
        MediaSessionCompat.Callback mediaSessionCompatCallBack;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////


/*///////////////////////////////////////////////FONCTIONS DU CYCLE DE VIE DE LA CLASSE SERVICE//////////////////////////////////////////
/*---------------------------------------------------------FONCTION ONCREATE--------------------------------------------------------------*/

    @Override
    public void onCreate() {
        super.onCreate();
        estActif=true;
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
        Toast.makeText(getApplicationContext(),"Arrêt service",Toast.LENGTH_LONG).show();
        estActif=false;
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

                envoieBroadcast(EXTRA_MAJ_SIMPLE);

                //Remet dans la pile du handler un appel pour le Runnable (this)
                handlerTemps.postDelayed(this, 1000);
            }
        }
    };


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


    /*----------------------------------------------BROADCASTRECEIVER PAUSE MUISQUE JACK DEBRANCHEE--------------------------------------------------------------*/

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



    /*---------------------------------------------------------FONCTIONS DE GESTION MUSIQUE--------------------------------------------------------------*/

    public void musiqueDemaPause() {
        if (musiquePlayer == null) {
            musiqueInitialisation();
            musiqueDemaEtFocus();
            startForeground(NOTIFICATION_ID, notificationInit());//Démarre le service en foreground afin de permettre de continuer la musique après l'avoir fermé
            enPauseParUtilisateur=false;
        } else if (!musiquePlayer.isPlaying()) {
            musiqueDemaEtFocus();
            enPauseParUtilisateur=false;
        } else {
            musiquePause();
            enPauseParUtilisateur=true;
        }
        enPauseParDemandeLongue=false;
    }

    public void musiqueInitialisation() {
        musiquePlayer = MediaPlayer.create(this, maMusique.get(positionMusique).getPathUri());//Création du MediaPlayer
        musiquePlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);//Définis le mode de fonctionnement sur PARTIAL_WAKE_LOCK pour permettre à la musique de fonctionner sans être sur l'application
        musiquePlayer.setOnCompletionListener(new EcouteurMusiqueFinie());
    }


    public void musiqueDemaEtFocus() {
/*
        Fonction de demande d'utilisation unique des sorties audio du téléphone
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
            musiquePlayer.start();//Démarre la musique
        }
    }

    public void musiquePause() {
        musiquePlayer.pause();
        handlerTemps.removeCallbacks(runnableTemps);
        envoieBroadcast(EXTRA_MAJ_SIMPLE);
    }

    public void arretTotalMusique()
    {
        if (musiquePlayer != null) {
            musiqueArret();
            stopForeground(true);
        }
    }

    public void arretSimpleMusique()
    {
        if (musiquePlayer != null) {
            musiqueArret();
        }
    }

    private void musiqueArret() {
        handlerTemps.removeCallbacks(runnableTemps);

        musiquePlayer.release();
        musiquePlayer = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            musiqueManager.abandonAudioFocusRequest(musiqueFocusRequest);
        } else {
            musiqueManager.abandonAudioFocus(musiqueFocusChange);
        }

        unregisterReceiver(broadcastReceiverNotifCmd);
        unregisterReceiver(broadcastReceiverJack);
    }


    public void musiqueSuivante() {
        //Remplacer musique arret par une autre fonction qui arrête pas la notif etc...
        musiqueArret();
        positionMusique++;

        if (positionMusique>= maMusique.size())
            positionMusique=0;

        musiqueDemaPause();
        envoieBroadcast(EXTRA_MAJ_INIT);
    }

    public void musiquePrecedente() {
        //Remplacer musique arret par une autre fonction qui arrête pas la notif etc...
        musiqueArret();
        positionMusique--;

        if (positionMusique < 0)
            positionMusique=maMusique.size()-1;

        musiqueDemaPause();
        envoieBroadcast(EXTRA_MAJ_INIT);
    }

    public void musiqueBoucleDeboucle() {
        if (musiquePlayer != null)
            musiquePlayer.setLooping(!musiquePlayer.isLooping());
    }



    /*-----------------------------------------------------GESTION ARRIVEE EN FIN DE MUSIQUE--------------------------------------------------------------*/

    private class EcouteurMusiqueFinie implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            //Si on arrive au bout de la musique et qu'elle n'est pas en mode boucle
            //on passe à la musique suivante (pas de besoin de véirifer si la musique boucle)
                musiqueSuivante();
        }
    }


    /*-----------------------------------------------------FONCTIONS ENVOIE BROADCAST--------------------------------------------------------------*/
    public void envoieBroadcast(final String extra) {
        Intent intent = new Intent()
                .setAction(DIRECTION_ACTIVITY)
                .putExtra(TYPE_MAJ,extra);
        sendBroadcast(intent);
    }



    /*---------------------------------------------------------FONCTION BORADCASTRECEIVER NOTIFICATION COMMANDE--------------------------------------------------------------*/

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

    /*---------------------------------------------------------FONCTION GESTION NOTIFICATION--------------------------------------------------------------*/


    public Notification notificationInit() {
        notifBuilder = new NotificationCompat.Builder(MusiqueService.this, CHANNEL_ID);//Inititalisation notification
        notifBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);            //Rend visible la notification quand le téléphone est vérouillé et permet le controle de la musique
        notifBuilder.setLargeIcon(recupImageMusique());                               //Ajoute l'image de la musique lu à la notification
        notifBuilder.setSmallIcon(R.drawable.image_notif_musique);                   //Icone de la notification
        notifBuilder.setContentTitle(maMusique.get(positionMusique).getName());     //Titre de la notification
        notifBuilder.setContentText(maMusique.get(positionMusique).getAuthor());        //Text de la notification
        notifBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);               //Défini la priorité de la notification
        notifBuilder.setOngoing(true);                                               //Empêche l'utilisateur de supprimer la notification
        notifBuilder.setNotificationSilent();                                        //Désactive le son de la notification
        //notifBuilder.setSubText(": "+(positionMusique+1)+"/"+maMusique.size()+" "+millisecondesEnMinutesSeconde(parseInt(maMusique.get(positionMusique).getDuration())));
        notifBuilder.setSubText(": "+(positionMusique+1)+"/"+maMusique.size());//Donne le numéro de la musique sur la playlist en cours
        notifBuilder.setShowWhen(false);                                                //Enlève l'affichage de l'heure à laquelle la notification est apaprue
        //notifBuilder.setAutoCancel(true);                                            //Supprime la notification si on appuit dessus
        //notifBuilder.setLargeIcon(null);                                          //Ajoute aucune image à la notification


        //déclaration de l'enregistrement d'un BoradcastReceiver pour la gestion quand une prise jack est débranchée
        IntentFilter intentFilterJack = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(broadcastReceiverJack, intentFilterJack);


        Intent musiquePlayerIntent = new Intent(this, MainActivity.class);//Déclaration Intent pour retourner sur la page de la musique
        PendingIntent musiquePlayerPenInt = PendingIntent.getActivity(this, 0, musiquePlayerIntent, 0); //Déclaration d'un pendingIntent pour utiliser l'intent précédent dans une notification
        notifBuilder.setContentIntent(musiquePlayerPenInt);                          //Ajoute l'intent à l'appui sur la notification (retour application)


        //Enregistrement du BroafcastRecevier sous l'écoute du message ACTION_STRING_SERVICE
        IntentFilter intentFilter = new IntentFilter(DIRECTION_SERVICE);
        registerReceiver(broadcastReceiverNotifCmd, intentFilter);


        //Déclaration des Intents et PenIntents pour le contrôle de la musique sur la notification
        Intent musiqueIntentRejouer = new Intent()
                .setAction(DIRECTION_SERVICE)
                .putExtra(TYPE_NOTIFICATION, "REJOUER");
        PendingIntent musiquePenIntRejouer = PendingIntent.getBroadcast(this, 1, musiqueIntentRejouer, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent musiqueIntentPrecedent = new Intent()
                .setAction(DIRECTION_SERVICE)
                .putExtra(TYPE_NOTIFICATION, "PRECEDENT");
        PendingIntent musiquePenIntPrecedent = PendingIntent.getBroadcast(this, 2, musiqueIntentPrecedent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent musiqueIntentDemaPause = new Intent()
                .setAction(DIRECTION_SERVICE)
                .putExtra(TYPE_NOTIFICATION, "DEMAPAUSE");
        PendingIntent musiquePenIntDemaPause = PendingIntent.getBroadcast(this, 3, musiqueIntentDemaPause, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent musiqueIntentSuivant = new Intent()
                .setAction(DIRECTION_SERVICE)
                .putExtra(TYPE_NOTIFICATION, "SUIVANT");
        PendingIntent musiquePenIntSuivant = PendingIntent.getBroadcast(this, 4, musiqueIntentSuivant, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent musiqueIntentArret = new Intent()
                .setAction(DIRECTION_SERVICE)
                .putExtra(TYPE_NOTIFICATION, "ARRET");
        PendingIntent musiquePenIntArret = PendingIntent.getBroadcast(this, 5, musiqueIntentArret, PendingIntent.FLAG_UPDATE_CURRENT);


        //Ajout des boutons à la notification pour le contrôle musique
        notifBuilder.addAction(R.drawable.image_rejouer, "Rejouer", musiquePenIntRejouer);//Ajout le bouton "musique rejouer" à la notification"
        notifBuilder.addAction(R.drawable.image_precedent, "Précédent", musiquePenIntPrecedent);//Ajout le bouton "musique précédente à la notification"
        notifBuilder.addAction(R.drawable.image_pause, "Démarrer/Pause", musiquePenIntDemaPause);//Ajout le bouton "musique Demarrer/Pause à la notification"
        notifBuilder.addAction(R.drawable.image_suivant, "Suivant", musiquePenIntSuivant);//Ajout le bouton "musique suivante à la notification"
        notifBuilder.addAction(R.drawable.image_nettoyer, "Arret", musiquePenIntArret);//Ajout le bouton "musique arret" à la notification"



        notifBuilder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()//Défini le style de notification en "notification de médias"
                .setShowActionsInCompactView(1, 2, 3)
                //.setMediaSession()
                );//Ajoute les boutons à la notification en mode compacté


        notifManagerCompat = NotificationManagerCompat.from(MusiqueService.this);//Création d'une gestion de notification

        //Gestion si l'utilisateur utilise Android 8.0 ou supérieur
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notifChannel = new NotificationChannel(CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);//Création d'un Channel de notification pour les notifications d'Android 8.0 ou supérieur
            notifChannel.setImportance(NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);//Création d'un NotificationManager pour les notifications d'Android 8.0 ou supérieur
            notificationManager.createNotificationChannel(notifChannel);//Création du channel de notificatios
        }

        return notifBuilder.build();
    }


    /*-----------------------------------------------------------FONCTIONS RECUPERTATION IMAGE--------------------------------------------------------------*/

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
            //Si aucune image n'est trouvé dans le fichier audio R.drawable.image_notif_musique
            return xmlEnBitmap(R.drawable.image_notif_musique);
        }
    }

    private Bitmap xmlEnBitmap(int drawableRes) {
        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    public void notificationMaj()
    {

    }



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


    /*--------------------------------------AUTRES FONCTIONS------------------------------------------------*/

    @SuppressLint("DefaultLocale")
    private String millisecondesEnMinutesSeconde(int tmpsMillisecondes) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(tmpsMillisecondes),
                TimeUnit.MILLISECONDS.toSeconds(tmpsMillisecondes) - TimeUnit.MILLISECONDS.toMinutes(tmpsMillisecondes) * 60);
    }
}
