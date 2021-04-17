package com.example.wolfii;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class ListAllSongsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ArrayList<Musique> maMusique;
    private MyMusiqueAdapter monAdapter;
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
        maMusique = MainActivity.maMusique; // on recupere ici toutes les musiques sous forme d'un tableau

        monAdapter = new MyMusiqueAdapter (maMusique, getActivity ());
        monAdapter.setmMusiqueItemClickListener(new MyMusiqueAdapter.MusiqueItemClickListener() {
            @Override
            public void onMusiqueItemClick(View view, Musique musique, int position) {

                Toast.makeText(getActivity(), "Lecture de : " + musique.getName(), Toast.LENGTH_SHORT).show();

                // ------ FAIRE EN SORTE QU'ON PUISSE LIRE LA MUSIQUE ICI

                //mService.setMusiquePlaylist(maMusique, position);
                //mService.musiqueArret();
                //mService.musiqueDemaPause();
                /*
                Intent intent = new Intent(getActivity(), Lecteur.class);
                startActivity(intent);

                 */

                 
                // position c'est l'index de la musique concernée
                // maMusique => toutes les musiques

            }

            @Override
            public void onMusiqueItemLongClick(View view, Musique musique, int position) {
                //Toast.makeText(MainActivity.this, "ah toi tu attends une suppression !", Toast.LENGTH_SHORT).show();
                Dialog dialog = new Dialog(getActivity ());

                // set content view
                dialog.setContentView(R.layout.dialog_update);

                // initialize width and height
                int width = WindowManager.LayoutParams.MATCH_PARENT;
                int height = WindowManager.LayoutParams.WRAP_CONTENT;
                //set layout
                dialog.getWindow().setLayout(width, height);
                Button btn_delete = dialog.findViewById (R.id.bt_delete);
                btn_delete.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Boolean aBoolean = new File (musique.getPathUri ().toString ()).delete ();
                        Log.d ("debug_delete", aBoolean.toString ());

                    }
                });
                //show dialog
                dialog.show();

            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayout.VERTICAL, false));
        mRecyclerView.setAdapter(monAdapter);

        return root;
    }
    private void delete(Musique m) {
        new File(m.getPathUri ().toString()).delete();
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