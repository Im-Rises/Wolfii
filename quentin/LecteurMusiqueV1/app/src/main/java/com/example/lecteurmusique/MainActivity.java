package com.example.lecteurmusique;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private SeekBar seekBarMusique;
    private TextView txtViewMusiqueTemps,txtViewMusiqueDuree;

    private MediaPlayer musiquePlayer;


    private Runnable ecranCalc;
    private Handler ecranMaj;
    private Thread ecranThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Code qui lie les objets du activity_main.xml à ceux dy MainActivity.xml*/
        this.txtViewMusiqueTemps = (TextView) findViewById(R.id.txtViewMusiqueTemps);
        this.txtViewMusiqueDuree = (TextView) findViewById(R.id.txtViewMusiqueDuree);
        this.seekBarMusique = (SeekBar) findViewById(R.id.seekBarMusique);
    }



    public void musiquDemaPause(View view)
    {
        if (musiquePlayer==null ) {
            musiquePlayer = MediaPlayer.create(this, R.raw.musiquetest);
            musiquePlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            seekBarMusique.setMax(musiquePlayer.getDuration());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    seekBarMusique.setProgress(musiquePlayer.getCurrentPosition());
                }
            });

            musiquePlayer.start();
        }
        else if (!musiquePlayer.isPlaying())
        {
            musiquePlayer.start();
        }
        else
            musiquePlayer.pause();
    }

    public void musiqueArret(View view)
    {
        if (musiquePlayer!=null)
        {
            musiquePlayer.release();
            musiquePlayer=null;
        }
    }
}