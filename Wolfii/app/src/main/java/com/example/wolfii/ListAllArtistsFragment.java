package com.example.wolfii;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import static com.example.wolfii.MainActivity.database;
import static com.example.wolfii.MainActivity.mService;
import static com.example.wolfii.MainActivity.maMusique;

public class ListAllArtistsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerViewPlaylist;
    private ArrayList<String> mesArtistes;
    private MyArtisteAdapter monArtisteAdapter;
    private MyArtisteAdapter monPlaylistAdapter;
    private MyMusiqueAdapter monMusiqueAdapter;
    private ArrayList<Musique> musiques;

    //private ArrayList<MusiquesData> dataList = new ArrayList<> ();
    //private RoomDB database;

    @SuppressLint("WrongConstant")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_mes_artistes, container, false);

        // on recupere toutes les donnees de la base de donnees et on cree son adapter
        List<MainData> dataList = database.mainDao ().getAll ();
        ArrayList<String> playlists = new ArrayList<String> ();

        // on recupere toutes les playlists
        for(MainData m : dataList) if (!playlists.contains (m.getPlaylist ())) playlists.add(m.getPlaylist ());
        mRecyclerViewPlaylist = (RecyclerView) root.findViewById (R.id.recyclerview_playlists);
        monPlaylistAdapter = new MyArtisteAdapter (playlists);

        mRecyclerViewPlaylist.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayout.VERTICAL, false));
        mRecyclerViewPlaylist.setAdapter(monPlaylistAdapter);

        // creation du recyclerview
        mRecyclerView = (RecyclerView) root.findViewById(R.id.myRecyclerView);
        mesArtistes = MainActivity.mesArtistes; // on recupere ici toutes les musiques sous forme d'un tableau

        monArtisteAdapter = new MyArtisteAdapter (mesArtistes);
        monArtisteAdapter.setmArtisteItemClickListener(new MyArtisteAdapter.ArtisteItemClickListener() {

            @Override
            public void onArtisteItemClick (View view, String artiste, int position) {

                    musiques = recuperer_musique (artiste);

                    monMusiqueAdapter = new MyMusiqueAdapter (musiques, getActivity ());
                    monMusiqueAdapter.setmMusiqueItemClickListener(new MyMusiqueAdapter.MusiqueItemClickListener() {
                        @Override
                        public void onMusiqueItemClick(View view, Musique musique, int position) {

                            Toast.makeText(getActivity(), "Lecture de : " + musique.getName(), Toast.LENGTH_SHORT).show();

                            mService.setMusiquePlaylist(musiques, position);
                            mService.arretSimpleMusique();
                            mService.musiqueDemaPause();
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
                                    //rm(musique.getPath());
                                }
                            });
                            //show dialog
                            dialog.show();

                        }
                    });
                mRecyclerView.setAdapter (monMusiqueAdapter);
            }

            @Override
            public void onArtisteItemLongClick (View view, String artiste, int position) {

            }

        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayout.VERTICAL, false));
        mRecyclerView.setAdapter(monArtisteAdapter);

        return root;
    }
    private ArrayList<Musique> recuperer_musique(String artiste) {
        // on recupere toutes les musiques selon l'artiste qui nous interesse
        ArrayList<Musique> musiques = new ArrayList<> ();
        for(Musique m : maMusique) if (m.getAuthor().equals (artiste) ) musiques.add (m);
        return musiques;
    }
}