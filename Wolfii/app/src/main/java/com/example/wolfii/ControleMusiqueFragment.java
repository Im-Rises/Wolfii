package com.example.wolfii;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.concurrent.TimeUnit;

public class ControleMusiqueFragment extends Fragment {
    private SeekBar seekBarMusique;                             //SeekBar de lecture de la musique

    private TextView txtViewMusiqueTemps, txtViewMusiqueDuree;   //TextView du temps de lecture de la musique

    private MusiqueService mService;                            //Déclaration pointeur vers le service
    private boolean mBound = false;                             //Variable qui témoigne de l'activation du service

    private static final String ACTION_STRING_ACTIVITY = "ToActivity";  //Action pour envoyer un Boradcast dans l'activité

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_controle_musique, container, false);

        //Liaisons des Boutons, des TextViews et du SeekBar de l'interface dans la code.
        this.txtViewMusiqueTemps = (TextView) root.findViewById(R.id.txtViewMusiqueTemps);
        this.txtViewMusiqueTemps.setSoundEffectsEnabled(false);

        this.txtViewMusiqueDuree = (TextView) root.findViewById(R.id.txtViewMusiqueDuree);
        this.txtViewMusiqueDuree.setSoundEffectsEnabled(false);

        this.seekBarMusique=(SeekBar) root.findViewById(R.id.seekBarMusique);
        this.seekBarMusique.setSoundEffectsEnabled(false);
        this.seekBarMusique.setOnSeekBarChangeListener(new seekBarEcouteur());

        return root;
    }
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
    @SuppressLint("DefaultLocale")
    private String millisecondesEnMinutesSeconde(int tmpsMillisecondes) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(tmpsMillisecondes),
                TimeUnit.MILLISECONDS.toSeconds(tmpsMillisecondes) - TimeUnit.MILLISECONDS.toMinutes(tmpsMillisecondes) * 60);
    }

    //------------------------------ TOUTES LES COMMANDES DE MUSIQUE -------------------------------------------------------

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
}