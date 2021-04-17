package com.example.wolfii;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.concurrent.TimeUnit;

import static com.example.wolfii.MainActivity.mBound;
import static com.example.wolfii.MainActivity.mService;



public class ControleMusiqueFragment extends Fragment {

    private SeekBar seekBarMusique;                             //SeekBar de lecture de la musique

    private TextView txtViewMusiqueTemps, txtViewMusiqueDuree;   //TextView du temps de lecture de la musique

    private Button btnDemaPause, btnArret,btnSuivant,btnPrecedent,btnRejouer;  //boutons de la page


    private static final String ACTION_STRING_ACTIVITY = "ToActivity";  //Action pour envoyer un Boradcast dans l'activité


    /*A FAIRE :
     *
     * Ajouter vérification si le service est en fonctionnement pour les OnDestroy de l'Activité et du Service
     * Ajouter musique en pause si jack débranché
     * Ajouter images des mp3 sur notification
     * Date d’envoi de votre message : Hier, à 18:36
     * Ainsi que maj notif
     * Date d’envoi de votre message : Hier, à 18:36
     * Et ajout nom musique sur notif
     * Corrigé le bug de la notification qui s'enlève parfois
     * Ajouter la maj de la notification et interface sur appui du bouton DemaPause ainsi que BoucleDeboucle
     * Vérifier le cycle de vie des fragments sur Android Studio pour l'arrêt des BroadcastReceiver etc...
     *
     */

    /*------------------------------------------FONCTION ONCREATE-----------------------------------------------------*/
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_controle_musique, container, false);

        //Liaisons des Boutons, des TextViews et du SeekBar de l'interface dans la code.
        this.txtViewMusiqueTemps = (TextView) root.findViewById(R.id.txtViewMusiqueTemps);
        this.txtViewMusiqueTemps.setSoundEffectsEnabled(false);

        this.txtViewMusiqueDuree = (TextView) root.findViewById(R.id.txtViewMusiqueDuree);
        this.txtViewMusiqueDuree.setSoundEffectsEnabled(false);

        this.btnDemaPause = (Button) root.findViewById(R.id.btnDemaPause);
        this.btnDemaPause.setSoundEffectsEnabled(false);

        this.btnArret = (Button) root.findViewById(R.id.btnArret);
        this.btnArret.setSoundEffectsEnabled(false);

        this.btnRejouer = (Button) root.findViewById(R.id.btnRejouer);
        this.btnRejouer.setSoundEffectsEnabled(false);

        this.seekBarMusique=(SeekBar) root.findViewById(R.id.seekBarMusique);
        this.seekBarMusique.setSoundEffectsEnabled(false);
        this.seekBarMusique.setOnSeekBarChangeListener(new seekBarEcouteur());

        IntentFilter intentFilter = new IntentFilter(ACTION_STRING_ACTIVITY);
        getActivity().registerReceiver(broadcastReceiverMajInterface, intentFilter);

        return root;
    }


    /*--------------------------------------FONCTION ONSTART------------------------------------------------*/
    @Override
    public void onStart() {
        super.onStart();
    }


    /*--------------------------------------FONCTION ONDESTROY------------------------------------------------*/
    @Override
    public void onDestroy() {

        //Arrêt broadcast receiver de mise à jour de l'interface
        getActivity().unregisterReceiver(broadcastReceiverMajInterface);

        super.onDestroy();
    }

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


    /*----------------------------------GESTION BROADCASTRECEIVER--------------------------------------------------*/

    private BroadcastReceiver broadcastReceiverMajInterface = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            majInterface();//Mise à jour de l'interface
        }
    };

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