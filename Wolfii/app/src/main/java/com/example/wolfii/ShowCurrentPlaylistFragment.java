package com.example.wolfii;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import static com.example.wolfii.MainActivity.mService;

public class ShowCurrentPlaylistFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ArrayList<Musique> maMusique;
    private MyMusiqueAdapter monAdapter;
    private ImageView shuffleiv, reload, playPause, next, previous;


    public void setMaMusique(ArrayList<Musique> musiques) {maMusique = musiques;}

    @SuppressLint("WrongConstant")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_liste_recherche, container, false);
        //database.getInstance (getActivity ());
        // creation du recyclerview
        mRecyclerView = root.findViewById(R.id.myRecyclerView);

        shuffleiv = root.findViewById (R.id.shuffle);
        ClickOnShuffle shuffle = new ClickOnShuffle ();
        shuffle.setContext (getActivity ());
        shuffle.setmRecyclerView (mRecyclerView);
        shuffle.setPlaylist (maMusique);
        shuffleiv.setOnClickListener (shuffle);

        next = root.findViewById (R.id.next);
        next.setOnClickListener(new EcouteurMusiqueSuivante());
        previous = root.findViewById (R.id.previous);
        previous.setOnClickListener(new EcouteurMusiquePrecedente());
        reload = root.findViewById (R.id.reload);
        reload.setOnClickListener(new EcouteurBtnRejouer());
        playPause = root.findViewById (R.id.playPause);
        playPause.setOnClickListener(new EcouteurBtnDemaPause());

        monAdapter = new MyMusiqueAdapter (maMusique, getActivity ());
        ClickOnMusic clickOnMusic = new ClickOnMusic ();
        clickOnMusic.setMesMusiques (maMusique);
        clickOnMusic.setContext (getActivity ());
        monAdapter.setmMusiqueItemClickListener(clickOnMusic);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayout.VERTICAL, false));
        mRecyclerView.setAdapter(monAdapter);

        return root;
    }



    private class EcouteurBtnDemaPause implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //if (mService.getMusiquePlayerIsSet())
            //{
            mService.musiqueDemaPause();
            //}
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
            //if (mService.getMusiquePlayerIsSet())
            //{
            mService.musiqueSuivante();
            //}
        }
    }

    private  class EcouteurMusiquePrecedente implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //if (mService.getMusiquePlayerIsSet())
            //{
            mService.musiquePrecedente();
            //}
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////FONCTIONS MAJ INTERFACE/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   /* *//*----------------------------------GESTION BROADCASTRECEIVER--------------------------------------------------*//*

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
        imgViewMusique.setImageBitmap(mService.recupImageMusique());
        txtViewMusiqueDuree.setText(millisecondesEnMinutesSeconde(mService.getMusiquePlayerDuration()));
        txtViewTitreMusique.setText(mService.getMusiqueTitre());
        txtViewAuteurMusique.setText(mService.getMusiqueAuteur());
        majInterface();
    }


    public void majInterface() {
        //if (mService.getMusiquePlayerIsSet()) {
        seekBarMusique.setProgress(mService.getMusiquePlayerPosition());
        txtViewMusiqueTemps.setText(millisecondesEnMinutesSeconde(mService.getMusiquePlayerPosition()));

        if (mService.getMusiquePlayerIsPlaying())
            cmdDemaPause.setImageBitmap(drawableEnBitmap(R.drawable.pauseblanc));
        else
            cmdDemaPause.setImageBitmap(drawableEnBitmap(R.drawable.playbutton));
        //}

        setImageRejoueRejouer();
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
        cmdDemaPause.setImageBitmap(drawableEnBitmap(R.drawable.ic_baseline_play_circle_outline_24));
        setImageRejoueRejouer();
    }

    public void setImageRejoueRejouer()
    {
        if (mService.getMusiqueBoucle())
            cmdRejouer.setImageBitmap(drawableEnBitmap(R.drawable.image_rejoue));
        else
            cmdRejouer.setImageBitmap(drawableEnBitmap(R.drawable.image_rejouer));
    }
*/

}