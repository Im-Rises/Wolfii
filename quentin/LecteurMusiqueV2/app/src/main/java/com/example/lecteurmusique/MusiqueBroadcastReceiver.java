package com.example.lecteurmusique;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MusiqueBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("test de log de ","test 200");

    }

    /*
    Ici faire gestion de l'envoie de la mise à jour de l'UI par le service et faire
    le commandement de la musique par le MainActivity au service.
     */
}
