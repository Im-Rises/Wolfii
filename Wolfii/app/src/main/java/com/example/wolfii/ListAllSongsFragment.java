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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import static com.example.wolfii.MainActivity.database;
import static com.example.wolfii.MainActivity.mService;

public class ListAllSongsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ArrayList<Musique> maMusique;
    private MyMusiqueAdapter monAdapter;



    @SuppressLint("WrongConstant")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_listallsongs, container, false);
        //database.getInstance (getActivity ());
        // creation du recyclerview
        mRecyclerView = (RecyclerView) root.findViewById(R.id.myRecyclerView);
        maMusique = MainActivity.maMusique; // on recupere ici toutes les musiques sous forme d'un tableau

        monAdapter = new MyMusiqueAdapter (maMusique, getActivity ());
        monAdapter.setmMusiqueItemClickListener(new MyMusiqueAdapter.MusiqueItemClickListener() {
            @Override
            public void onMusiqueItemClick(View view, Musique musique, int position) {

                Toast.makeText(getActivity(), "Lecture de : " + musique.getName(), Toast.LENGTH_SHORT).show();

                mService.setMusiquePlaylist(maMusique, position);
                mService.arretSimpleMusique();
                mService.musiqueDemaPause();

                /*
                Intent intent = new Intent(getActivity(), Lecteur.class);
                startActivity(intent);
                 */

            }

            @Override
            public void onMusiqueItemLongClick(View view, Musique musique, int position) {
                //Toast.makeText(MainActivity.this, "ah toi tu attends une suppression !", Toast.LENGTH_SHORT).show();
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

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayout.VERTICAL, false));
        mRecyclerView.setAdapter(monAdapter);

        return root;
    }
    private void rm(String path) {
        try {
            // delete the original file
            File file = new File(path);
            file.delete();
            Log.d("debug_delete", "ok");
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }
}