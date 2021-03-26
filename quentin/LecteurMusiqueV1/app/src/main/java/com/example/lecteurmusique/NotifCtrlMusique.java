/*
package com.example.lecteurmusique;

import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotifCtrlMusique {

    public void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.image_notif_musique)
                    .setContentTitle("Titre de la musique")
                    .setContentText("Description de la musique")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }
    }
}
*/
