package com.example.wolfii;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.example.wolfii.MainActivity.database;
import static com.example.wolfii.MainActivity.mService;

public class ListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ArrayList mesElements;
    private MyMusiqueAdapter monAdapter;


    public ListFragment () {
        // Required empty public constructor
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
    }

    public void setMaMusique(ArrayList<Musique> musiques) {
        mesElements = musiques;
    }
    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_list, container, false);

        mRecyclerView = (RecyclerView) root.findViewById(R.id.myRecyclerView);

        monAdapter = new MyMusiqueAdapter (mesElements, getActivity ());
        monAdapter.setmMusiqueItemClickListener(new MyMusiqueAdapter.MusiqueItemClickListener() {
            @Override
            public void onMusiqueItemClick(View view, Musique musique, int position) {

                Toast.makeText(getActivity(), "Lecture de : " + musique.getName(), Toast.LENGTH_SHORT).show();

                mService.setMusiquePlaylist(mesElements, position);
                mService.arretSimpleMusique();
                mService.musiqueDemaPause();

            }

            @Override
            public void onMusiqueItemLongClick(View view, Musique musique, int position) {
                Dialog dialog = new Dialog(getActivity ());

                // set content view
                dialog.setContentView(R.layout.ajouter_a_une_playlist);

                // initialize width and height
                int width = WindowManager.LayoutParams.MATCH_PARENT;
                int height = WindowManager.LayoutParams.WRAP_CONTENT;
                //set layout
                dialog.getWindow().setLayout(width, height);

                EditText editText = dialog.findViewById (R.id.nom_playlist);
                Button addToPlaylist = dialog.findViewById (R.id.add);
                addToPlaylist.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        MainData data = new MainData ();
                        data.setNomMusique (musique.getName ());
                        data.setPath (musique.getPath ());
                        data.setPlaylist (editText.getText ().toString ());

                        try {
                            database.mainDao ().insert (data);}
                        catch (Exception e){
                            Log.d("debug_db", e.getMessage ());
                        }

                        dialog.dismiss ();
                    }
                });

                //show dialog
                dialog.show();

            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager (getActivity().getApplicationContext(), LinearLayout.VERTICAL, false));
        mRecyclerView.setAdapter(monAdapter);

        return root;

    }
}