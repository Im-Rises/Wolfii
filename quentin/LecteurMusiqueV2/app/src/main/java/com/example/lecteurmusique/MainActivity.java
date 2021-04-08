package com.example.lecteurmusique;


import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private SeekBar seekBarMusique;                             //SeekBar de lecture de la musique

    private Button btnMusiqueDemaPause;                         //Bouton pour démarrer et mettre en pause la musique
    private Button btnMusiqueArret;                          //Bouton pour arrêter la musique

    private TextView txtViewMusiqueTemps, txtViewMusiqueDuree;   //TextView du temps de lecture de la musique



    //Fonction d'apppel lors de la création de la page
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Code qui lie les objets du activity_main.xml à ceux dy MainActivity.xml*/
        this.seekBarMusique = (SeekBar) findViewById(R.id.seekBarMusique);

        this.btnMusiqueDemaPause = (Button) findViewById(R.id.btnDemaPause);
        this.btnMusiqueArret = (Button) findViewById(R.id.btnArret);

        this.txtViewMusiqueTemps = (TextView) findViewById(R.id.txtViewMusiqueTemps);
        this.txtViewMusiqueDuree = (TextView) findViewById(R.id.txtViewMusiqueDuree);



        //Gestion du déplacement par l'utilisateur du seekbar
        seekBarMusique.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                /*if (musiquePlayer != null) {
                    if (fromUser) {
                        musiquePlayer.seekTo(progress);
                    }
                    txtViewMusiqueTemps.setText(millisecondesEnMinutesSeconde(musiquePlayer.getCurrentPosition()));
                }*/
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnMusiqueDemaPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Envoyer commande de démarrage et pause de la musique à la classe service.
            }
        });

        btnMusiqueArret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Envoyer commande d'arrêt de la musique à la classe service. Et même arrêt du service.
            }
        });

    }

    @SuppressLint("DefaultLocale")
    private String millisecondesEnMinutesSeconde(int tmpsMillisecondes) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(tmpsMillisecondes),
                TimeUnit.MILLISECONDS.toSeconds(tmpsMillisecondes) - TimeUnit.MILLISECONDS.toMinutes(tmpsMillisecondes) * 60);
    }
}
