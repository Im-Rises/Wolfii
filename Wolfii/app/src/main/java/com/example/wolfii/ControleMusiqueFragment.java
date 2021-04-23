package com.example.wolfii;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.example.wolfii.MainActivity.mService;



/*A FAIRE :
 * 
 *
 */


public class ControleMusiqueFragment extends Fragment {

    private SeekBar seekBarMusique;                             //SeekBar de lecture de la musique

    private TextView txtViewMusiqueTemps, txtViewMusiqueDuree, txtViewTitreMusique;   //TextView du temps de lecture de la musique

    private Button showCurrentPlaylist;  //boutons de la page

    private ImageView cmdDemaPause, cmdRejouer;

    private ImageView cmdSuivant,cmdPrecedent;

    private ImageView imgViewMusique;

    private FragmentManager fragmentManager;

    private FragmentTransaction fragmentTransaction;

    private static final String DIRECTION_ACTIVITY = "TO_ACTIVITY";
    private static final String TYPE_MAJ = "TYPE_MAJ";
    private static final String EXTRA_MAJ_INIT = "CMD_MAJ_INIT";
    private static final String EXTRA_MAJ_SIMPLE = "CMD_MAJ_SIMPLE";
    private static final String EXTRA_MAJ_FIN = "CMD_MAJ_FIN";




////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////FONCTIONS DU CYCLE DE VIE DE LA PAGE/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /*------------------------------------------FONCTION ONCREATE-----------------------------------------------------*/
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_controle_musique, container, false);

        // on initialise le fragment manager
        fragmentManager = getActivity ().getSupportFragmentManager ();

        //Liaisons des Boutons, des TextViews et du SeekBar de l'interface dans la code.
        this.txtViewMusiqueTemps = (TextView) root.findViewById(R.id.txtViewMusiqueTemps);

        this.txtViewMusiqueDuree = (TextView) root.findViewById(R.id.txtViewMusiqueDuree);

        this.txtViewTitreMusique = (TextView) root.findViewById(R.id.txtViewTitreMusique);

        this.cmdDemaPause = (ImageView) root.findViewById(R.id.btnDemaPause);
        this.cmdDemaPause.setSoundEffectsEnabled(false);
        this.cmdDemaPause.setOnClickListener(new EcouteurBtnDemaPause());

        this.cmdPrecedent = (ImageView) root.findViewById(R.id.btnMusiquePrecedente);
        this.cmdPrecedent.setSoundEffectsEnabled(false);
        this.cmdPrecedent.setOnClickListener(new EcouteurMusiquePrecedente());

        this.cmdSuivant = (ImageView) root.findViewById(R.id.btnMusiqueSuivante);
        this.cmdSuivant.setSoundEffectsEnabled(false);
        this.cmdSuivant.setOnClickListener(new EcouteurMusiqueSuivante());

        this.cmdRejouer = (ImageView) root.findViewById(R.id.btnRejouer);
        this.cmdRejouer.setSoundEffectsEnabled(false);
        this.cmdRejouer.setOnClickListener(new EcouteurBtnRejouer());

        this.showCurrentPlaylist = root.findViewById (R.id.showCurrentPlaylist);
        this.showCurrentPlaylist.setOnClickListener (new ShowCurrentPlaylist());


        this.seekBarMusique=(SeekBar) root.findViewById(R.id.seekBarMusique);
        this.seekBarMusique.setSoundEffectsEnabled(false);
        this.seekBarMusique.setOnSeekBarChangeListener(new EcouteurSeekBar());

        this.imgViewMusique = (ImageView) root.findViewById(R.id.imgViewLogo);

        //Enregistrement du receiver pour la mise à jour de l'interface
        IntentFilter intentFilter = new IntentFilter(DIRECTION_ACTIVITY);
        getActivity().registerReceiver(broadcastReceiverMajInterface, intentFilter);

        if (mService.getMusiquePlayerIsSet())
            majInterfaceInit();//Mise à jour de l'interface
        else
            setImageRejoueRejouer();

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




////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////FONCTIONS D'ACTION DES BOUTONS/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
        public void onStopTrackingTouch(SeekBar seekBar) {
            // On place la mise à jour une fois qu'on a finis de déplacer le seekbar (évite un
            // rechargement du mediasession nombreux et inutile, car en plus qu'il n'est pas visible lorsqu'on
            // déplace le seekbar de cette page)
            if (mService.getMusiquePlayerIsSet()) {
                mService.mediaSessionBoutonsMaj();
            }
        }
    }


    private class EcouteurBtnDemaPause implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (mService.getMusiquePlayerIsSet())
            {
                mService.musiqueDemaPause();
            }
        }
    }

    private class EcouteurBtnRejouer implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //Active désactive la boucle de la musique actuelle
            mService.musiqueBoucleDeboucle();
        }
    }

    private class EcouteurMusiqueSuivante implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (mService.getMusiquePlayerIsSet())
            {
                mService.musiqueSuivante();
            }
        }
    }

    private  class EcouteurMusiquePrecedente implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (mService.getMusiquePlayerIsSet())
            {
                mService.musiquePrecedente();
            }
        }
    }

    private class ShowCurrentPlaylist implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            // show current playlist
            ArrayList<Musique> currentPlaylist = mService.getCurrentPlaylist ();
            int positionMusique = mService.getPositionMusique ();

            ShowCurrentPlaylistFragment showCurrentPlaylistFragment = new ShowCurrentPlaylistFragment ();
            showCurrentPlaylistFragment.setMaMusique(currentPlaylist);

            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.listes, showCurrentPlaylistFragment, null);
            fragmentTransaction.commit();
        }
    }



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////FONCTIONS MAJ INTERFACE/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*----------------------------------GESTION BROADCASTRECEIVER--------------------------------------------------*/

    private BroadcastReceiver broadcastReceiverMajInterface = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getStringExtra(TYPE_MAJ)) {
                case EXTRA_MAJ_INIT:
                    majInterfaceInit();//Mise à jour de l'interface au démarrage de la page
                    break;
                case EXTRA_MAJ_SIMPLE:
                    majInterface();//Mise à jour de l'interface
                    break;
                case EXTRA_MAJ_FIN:
                    majInterfaceFin();//Mise à jour interface d'arrêt de la lecture de musiques
                    break;
            }
        }
    };


    public void majInterfaceInit() {
        seekBarMusique.setMax(mService.getMusiquePlayerDuration());
        //imgViewMusique.setImageBitmap(getRoundedCornerBitmap (mService.recupImageMusique(), 100));
        imgViewMusique.setImageBitmap(mService.recupImageMusique());
        txtViewMusiqueDuree.setText(millisecondesEnMinutesSeconde(mService.getMusiquePlayerDuration()));
        txtViewTitreMusique.setText(mService.getMusiqueTitre());
        majInterface();
    }


    public void majInterface() {
        if (mService.getMusiquePlayerIsSet()) {
            seekBarMusique.setProgress(mService.getMusiquePlayerPosition());
            txtViewMusiqueTemps.setText(millisecondesEnMinutesSeconde(mService.getMusiquePlayerPosition()));

            if (mService.getMusiquePlayerIsPlaying())
                cmdDemaPause.setImageBitmap(drawableEnBitmap(R.drawable.ic_baseline_pause_circle_outline_24));
            else
                cmdDemaPause.setImageBitmap(drawableEnBitmap(R.drawable.ic_baseline_play_circle_outline_24));
        }

        setImageRejoueRejouer();
    }

    @SuppressLint("SetTextI18n")
    public void majInterfaceFin()
    {
        txtViewTitreMusique.setText("...");
        txtViewMusiqueDuree.setText("00:00");
        txtViewMusiqueTemps.setText("00:00");
        seekBarMusique.setProgress(0);
        imgViewMusique.setImageBitmap(drawableEnBitmap(R.drawable.loup));
        cmdDemaPause.setImageBitmap(drawableEnBitmap(R.drawable.ic_baseline_play_circle_outline_24));
    }

    public void setImageRejoueRejouer()
    {
        if (mService.getMusiqueBoucle())
            cmdRejouer.setImageBitmap(drawableEnBitmap(R.drawable.image_rejoue));
        else
            cmdRejouer.setImageBitmap(drawableEnBitmap(R.drawable.image_rejouer));
    }



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////AUTRES FONCTIONS/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*--------------------------------------CONVERSION TEMPS EN MILLISECONDE EN MINTES ET SECONDES------------------------------------------------*/

    @SuppressLint("DefaultLocale")
    private String millisecondesEnMinutesSeconde(int tmpsMillisecondes) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(tmpsMillisecondes),
                TimeUnit.MILLISECONDS.toSeconds(tmpsMillisecondes) - TimeUnit.MILLISECONDS.toMinutes(tmpsMillisecondes) * 60);
    }

    /*--------------------------------------CONVERSION DRAWABLE EN BITMAP------------------------------------------------*/

    public Bitmap drawableEnBitmap(int drawableRes) {
        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}