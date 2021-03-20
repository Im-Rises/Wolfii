package com.example.lecteurmusique;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    MediaPlayer musiquePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void musiqueDemarrer(View view)
    {
        if (musiquePlayer == null)
        {
            musiquePlayer = MediaPlayer.create(this, R.raw.musiquetest);
        }

        musiquePlayer.start();
    }

    public void musiquePause(View view)
    {
        if (musiquePlayer != null)
            musiquePlayer.pause();
    }

    public void musiqueArret(View view)
    {
        if (musiquePlayer != null)
        {
            musiquePlayer.release();
            musiquePlayer = null;
        }
    }
}