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

    private static final String DIRECTION_ACTIVITY = "TO_ACTIVITY";
    private static final String TYPE_MAJ = "TYPE_MAJ";
    private static final String EXTRA_MAJ_INIT = "CMD_MAJ_INIT";
    private static final String EXTRA_MAJ_SIMPLE = "CMD_MAJ_SIMPLE";
    private static final String EXTRA_MAJ_FIN = "CMD_MAJ_FIN";


    public void setMaMusique(ArrayList<Musique> musiques) {maMusique = musiques;}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////FONCTIONS DU CYCLE DE VIE DE LA PAGE/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*------------------------------------------FONCTION ONCREATE-----------------------------------------------------*/
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


        //Enregistrement du receiver pour la mise à jour de l'interface
        IntentFilter intentFilter = new IntentFilter(DIRECTION_ACTIVITY);
        getActivity().registerReceiver(broadcastReceiverMajInterface, intentFilter);

        if (mService.getMusiquePlayerIsSet())
            majInterface();//Mise à jour de l'interface

        return root;
    }


    /*--------------------------------------FONCTION ONRESUME------------------------------------------------*/

    @Override
    public void onResume() {
        super.onResume();
        if (mService.getMusiquePlayerIsSet())
            majInterface();
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

    private class EcouteurBtnDemaPause implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (mService.getMusiquePlayerIsSet())
                mService.musiqueDemaPause();
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
                mService.musiqueSuivante();

        }
    }

    private  class EcouteurMusiquePrecedente implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (mService.getMusiquePlayerIsSet())
                mService.musiquePrecedente();

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
                case EXTRA_MAJ_SIMPLE:
                    majInterface();//Mise à jour de l'interface
                    break;
                case EXTRA_MAJ_FIN:
                    majInterfaceFin();//Mise à jour interface d'arrêt de la lecture de musiques
                    break;
            }
        }
    };


    public void majInterface() {
        if (mService.getMusiquePlayerIsSet()) {
            if (mService.getMusiquePlayerIsPlaying())
                playPause.setImageBitmap(drawableEnBitmap(R.drawable.pauseblanc));
            else
                playPause.setImageBitmap(drawableEnBitmap(R.drawable.playbutton));
        }
        setImageRejoueRejouer();
    }

    @SuppressLint("SetTextI18n")
    public void majInterfaceFin()
    {
        playPause.setImageBitmap(drawableEnBitmap(R.drawable.ic_baseline_play_circle_outline_24));
        setImageRejoueRejouer();
    }

    public void setImageRejoueRejouer()
    {
        if (mService.getMusiqueBoucle())
            reload.setImageBitmap(drawableEnBitmap(R.drawable.image_rejoue));
        else
            reload.setImageBitmap(drawableEnBitmap(R.drawable.ic_baseline_repeat_24));
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