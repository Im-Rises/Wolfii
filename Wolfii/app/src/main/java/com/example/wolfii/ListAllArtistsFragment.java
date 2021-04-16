package com.example.wolfii;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListAllArtistsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ArrayList<String> mesArtistes;
    private MyArtisteAdapter monAdapter;
    private boolean mBound = false;
    private MusiqueService mService;                            //Déclaration pointeur vers le service


    @SuppressLint("WrongConstant")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_listallsongs, container, false);

        getActivity().startService(new Intent(getActivity(), MusiqueService.class));
        Intent intent = new Intent(getActivity(), MusiqueService.class);
        getActivity().bindService(intent, connection, 0);

        // creation du recyclerview
        mRecyclerView = (RecyclerView) root.findViewById(R.id.myRecyclerView);
        mesArtistes = MainActivity.mesArtistes; // on recupere ici toutes les musiques sous forme d'un tableau

        monAdapter = new MyArtisteAdapter(mesArtistes);
        monAdapter.setmArtisteItemClickListener(new MyArtisteAdapter.ArtisteItemClickListener() {

            @Override
            public void onArtisteItemClick(View view, String musique, int position) {

            }

            @Override
            public void onArtisteItemLongClick(View view, String artiste, int position) {
                //Toast.makeText(MainActivity.this, "ah toi tu attends une suppression !", Toast.LENGTH_SHORT).show();
                Log.d("debug_longclick", "suppression ?");

            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayout.VERTICAL, false));
        mRecyclerView.setAdapter(monAdapter);

        return root;
    }
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusiqueService.LocalBinder binder = (MusiqueService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}