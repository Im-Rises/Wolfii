package com.example.lecteurmusique;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MusiqueBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intentEnvoi = new Intent(context,MusiqueService.class);

        switch(intent.getAction())
        {
            case "DEMAPAUSE":
                //intentEnvoi.setAction("DEMAPAUSE");
                //context.startService(intentEnvoi);
                break;
            case "PRECEDENT":
                break;
            case "SUIVANT":
                break;
            case "ARRET":
                context.stopService(new Intent(context,MusiqueService.class));
                break;
            case "REJOUER":
                break;
        }
    }
}
