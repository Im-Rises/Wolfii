package com.example.wolfii;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class ControleMusiqueFragment extends Fragment {

    private SeekBar seekBarMusique;                             //SeekBar de lecture de la musique

    private TextView txtViewMusiqueTemps, txtViewMusiqueDuree, txtViewTitreMusique, txtViewAuteurMusique;   //TextView du temps de lecture de la musique

    private ImageView imgViewMusique;

    private ArrayList<Musique> currentPlaylist;

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
        fragmentManager = getActivity().getSupportFragmentManager ();

        //Liaisons des Boutons, des TextViews et du SeekBar de l'interface dans la code.
        this.txtViewMusiqueTemps = root.findViewById(R.id.txtViewMusiqueTemps);

        this.txtViewMusiqueDuree = root.findViewById(R.id.txtViewMusiqueDuree);

        this.txtViewTitreMusique = root.findViewById(R.id.txtViewTitreMusique);

        this.txtViewAuteurMusique = root.findViewById(R.id.txtViewAuteurMusique);


        this.seekBarMusique=(SeekBar) root.findViewById(R.id.seekBarMusique);
        this.seekBarMusique.setSoundEffectsEnabled(false);
        this.seekBarMusique.setOnSeekBarChangeListener(new EcouteurSeekBar());

        this.imgViewMusique = (ImageView) root.findViewById(R.id.imgViewLogo);

        //Enregistrement du receiver pour la mise à jour de l'interface
        IntentFilter intentFilter = new IntentFilter(DIRECTION_ACTIVITY);
        getActivity().registerReceiver(broadcastReceiverMajInterface, intentFilter);

        currentPlaylist = mService.getCurrentPlaylist ();
        int positionMusique = mService.getPositionMusique ();

        ShowCurrentPlaylistFragment showCurrentPlaylistFragment = new ShowCurrentPlaylistFragment ();
        showCurrentPlaylistFragment.setMaMusique(currentPlaylist);

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.listes, showCurrentPlaylistFragment, null);
        fragmentTransaction.commit();


        if (mService.getMusiquePlayerIsSet())
            majInterfaceInit();//Mise à jour de l'interface

        return root;
    }


    /*--------------------------------------FONCTION ONRESUME------------------------------------------------*/

    @Override
    public void onResume() {
        super.onResume();
        if (mService.getMusiquePlayerIsSet())
            majInterfaceInit();
        else
            majInterfaceFin();
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
        currentPlaylist = mService.getCurrentPlaylist ();
        seekBarMusique.setMax(mService.getMusiquePlayerDuration());
        imgViewMusique.setImageBitmap(mService.recupImageMusique());
        txtViewMusiqueDuree.setText(millisecondesEnMinutesSeconde(mService.getMusiquePlayerDuration()));
        txtViewTitreMusique.setText(mService.getMusiqueTitre());
        txtViewAuteurMusique.setText(mService.getMusiqueAuteur());
        majInterface();
    }


    public void majInterface() {
        if (mService.getMusiquePlayerIsSet()) {
            seekBarMusique.setProgress(mService.getMusiquePlayerPosition());
            txtViewMusiqueTemps.setText(millisecondesEnMinutesSeconde(mService.getMusiquePlayerPosition()));
        }
    }

    @SuppressLint("SetTextI18n")
    public void majInterfaceFin()
    {
        txtViewTitreMusique.setText("");
        txtViewAuteurMusique.setText("");
        txtViewMusiqueDuree.setText("00:00");
        txtViewMusiqueTemps.setText("00:00");
        seekBarMusique.setProgress(0);
        imgViewMusique.setImageBitmap(drawableEnBitmap(R.drawable.loup));
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

    public Bitmap drawableEnBitmap (int drawableRes) {
        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}