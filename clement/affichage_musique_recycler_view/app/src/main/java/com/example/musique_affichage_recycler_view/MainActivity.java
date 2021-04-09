package com.example.musique_affichage_recycler_view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private List<JeuVideo> mesJeux;
    private MyVideoGamesAdapter monAdapter;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        mesJeux = new ArrayList<>();

        mesJeux.add(new JeuVideo("Forza Hoziron 3", 49.90F));
        mesJeux.add(new JeuVideo("Assassin's creed", 50F));

        monAdapter = new MyVideoGamesAdapter(mesJeux);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false));
        mRecyclerView.setAdapter(monAdapter);
    }
}