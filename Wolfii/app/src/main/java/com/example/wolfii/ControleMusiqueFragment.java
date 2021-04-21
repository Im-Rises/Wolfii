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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.concurrent.TimeUnit;

import static com.example.wolfii.MainActivity.mBound;
import static com.example.wolfii.MainActivity.mService;



/*A FAIRE :
 *
 * Pour Android < 5.0 pour mediaStyle rajouter  setShowCancelButton(true) and setCancelButtonIntent() pour arrêter appli car dans ces versions on peut pas fermer notif qui ont ongoing
 * Vérifier bon fonctionneemnt de la gestion des prises jack
 * Faire maj notif et maj du seekbar du MediaSession Pour les Android > 11
 */


public class ControleMusiqueFragment extends Fragment {

    private SeekBar seekBarMusique;                             //SeekBar de lecture de la musique

    private TextView txtViewMusiqueTemps, txtViewMusiqueDuree, txtViewTitreMusique;   //TextView du temps de lecture de la musique

    private Button cmdDemaPause, cmdArret,cmdPrecedent,cmdSuivant,cmdRejouer;  //boutons de la page

    private ImageView imgViewMusique;

    private static final String DIRECTION_ACTIVITY = "TO_ACTIVITY";
    private static final String TYPE_MAJ = "TYPE_MAJ";
    private static final String EXTRA_MAJ_INIT = "CMD_MAJ_INIT";
    private static final String EXTRA_MAJ_SIMPLE = "CMD_MAJ_SIMPLE";


    /*------------------------------------------FONCTION ONCREATE-----------------------------------------------------*/
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_controle_musique, container, false);

        //Liaisons des Boutons, des TextViews et du SeekBar de l'interface dans la code.
        this.txtViewMusiqueTemps = (TextView) root.findViewById(R.id.txtViewMusiqueTemps);

        this.txtViewMusiqueDuree = (TextView) root.findViewById(R.id.txtViewMusiqueDuree);

        this.txtViewTitreMusique = (TextView) root.findViewById(R.id.txtViewTitreMusique);



        this.cmdDemaPause = (Button) root.findViewById(R.id.btnDemaPause);
        this.cmdDemaPause.setSoundEffectsEnabled(false);
        this.cmdDemaPause.setOnClickListener(new EcouteurBtnDemaPause());

/*        this.cmdArret = (Button) root.findViewById(R.id.btnArret);
        this.cmdArret.setSoundEffectsEnabled(false);
        this.cmdArret.setOnClickListener(new EcouteurBtnArret());*/


/*        this.cmdPrecedent = (Button) root.findViewById(R.id.btnPrecedent);
        this.cmdPrecedent.setSoundEffectsEnabled(false);
        this.cmdPrecedent.setOnClickListener(new cmdBtnPrecedentEcouteur());

        this.cmdSuivant = (Button) root.findViewById(R.id.btnSuivant);
        this.cmdSuivant.setSoundEffectsEnabled(false);
        this.cmdSuivant.setOnClickListener(new cmdBtnSuivantEcouteur());*/


        this.cmdRejouer = (Button) root.findViewById(R.id.btnRejouer);
        this.cmdRejouer.setSoundEffectsEnabled(false);
        this.cmdRejouer.setOnClickListener(new EcouteurBtnRejouer());

        this.seekBarMusique=(SeekBar) root.findViewById(R.id.seekBarMusique);
        this.seekBarMusique.setSoundEffectsEnabled(false);
        this.seekBarMusique.setOnSeekBarChangeListener(new EcouteurSeekBar());

        this.imgViewMusique = (ImageView) root.findViewById(R.id.imgViewLogo);

        //Enregistrement du receiver pour la mise à jour de l'interface
        IntentFilter intentFilter = new IntentFilter(DIRECTION_ACTIVITY);
        getActivity().registerReceiver(broadcastReceiverMajInterface, intentFilter);

        if (mService.getMusiquePlayerIsSet()) {
            majInterfaceInit();//Mise à jour de l'interface
        }

        return root;
    }


    /*--------------------------------------FONCTION ONSTART------------------------------------------------*/
    @Override
    public void onStart() {
        super.onStart();
    }


    /*--------------------------------------FONCTION ONRESUME------------------------------------------------*/

    @Override
    public void onResume() {
        super.onResume();
        if (mService.getMusiquePlayerIsSet()) {
            majInterfaceInit();
        }
    }

    /*--------------------------------------FONCTION ONDESTROY------------------------------------------------*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Arrêt broadcast receiver de mise à jour de l'interface
        getActivity().unregisterReceiver(broadcastReceiverMajInterface);
    }

    /*--------------------------------------FONCTION/CLASS SEEKBAR------------------------------------------------*/

    private class EcouteurSeekBar implements SeekBar.OnSeekBarChangeListener {

        //Evenement qui s'enclenche sur le déplacement du seekbar
        public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser)
        {
            if (mService.getMusiquePlayerIsSet() && fromUser ) {
                mService.setMusiquePlayerPosition(progress);
                txtViewMusiqueTemps.setText(millisecondesEnMinutesSeconde(progress));
            }
        }

        //Evenement qui s'enclenche sur l'appuit sur le seekbar
        public void onStartTrackingTouch(SeekBar seekBar) {}

        //Evenement qui s'enclenche sur la fin du déplacement du seekbar
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }



    /*--------------------------------------FONCTION BOUTONS------------------------------------------------*/

    private class EcouteurBtnDemaPause implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (mService.getMusiquePlayerIsSet())
            {
                mService.musiqueDemaPause();
            }
        }
    }

/*    private class EcouteurBtnArret implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            mService.arretTotal();
            seekBarMusique.setProgress(0);
            txtViewMusiqueTemps.setText("00:00");
        }
    }*/

    private class EcouteurBtnRejouer implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //Active désactive la boucle de la musique actuelle
            if (mService.getMusiquePlayerIsSet())
                mService.musiqueBoucleDeboucle();
        }
    }


/*    public void cmdMusiqueStuivante(View view)
    {

    }

    public void cmdMusiquePrecedente(View view)
    {

    }*/



    /*----------------------------------GESTION BROADCASTRECEIVER--------------------------------------------------*/

    private BroadcastReceiver broadcastReceiverMajInterface = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mService.getMusiquePlayerIsSet()) {
                switch (intent.getStringExtra(TYPE_MAJ)) {
                    case EXTRA_MAJ_INIT:
                        majInterfaceInit();//Mise à jour de l'interface
                        break;
                    case EXTRA_MAJ_SIMPLE:
                        majInterface();//Mise à jour de l'interface
                        break;
                }
            }
        }
    };

    /*--------------------------------------FONTION MAJ INTERFACE---------------------------------------------------*/

    public void majInterfaceInit() {
        seekBarMusique.setMax(mService.getMusiquePlayerDuration());
        imgViewMusique.setImageBitmap(mService.recupImageMusique());
        txtViewMusiqueDuree.setText(millisecondesEnMinutesSeconde(mService.getMusiquePlayerDuration()));
        txtViewTitreMusique.setText(mService.getMusiqueTitre());
        majInterface();
    }


    public void majInterface() {
        seekBarMusique.setProgress(mService.getMusiquePlayerPosition());
        txtViewMusiqueTemps.setText(millisecondesEnMinutesSeconde(mService.getMusiquePlayerPosition()));
    }


    /*--------------------------------------AUTRES FONCTIONS------------------------------------------------*/

    @SuppressLint("DefaultLocale")
    private String millisecondesEnMinutesSeconde(int tmpsMillisecondes) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(tmpsMillisecondes),
                TimeUnit.MILLISECONDS.toSeconds(tmpsMillisecondes) - TimeUnit.MILLISECONDS.toMinutes(tmpsMillisecondes) * 60);
    }
}