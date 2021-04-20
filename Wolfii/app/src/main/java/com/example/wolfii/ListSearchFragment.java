package com.example.wolfii;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.example.wolfii.MainActivity.mService;

public class ListSearchFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private MyMusiqueAdapter monAdapter;
    private String artiste;
    private static ArrayList<Musique> musiques = new ArrayList<> ();

    ListSearchFragment(ArrayList<Musique> musiques, String artiste){
        this.musiques = musiques;
        this.artiste = artiste;
    }

    @SuppressLint({"WrongConstant", "SetTextI18n"})
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_liste_recherche, container, false);

        TextView textView = root.findViewById (R.id.search_artiste);
        textView.setText ("resultat(s) pour : \"" + this.artiste + "\"");
        // creation du recyclerview
        mRecyclerView = (RecyclerView) root.findViewById(R.id.myRecyclerView);

        monAdapter = new MyMusiqueAdapter (this.musiques, getActivity ());
        monAdapter.setmMusiqueItemClickListener(new MyMusiqueAdapter.MusiqueItemClickListener() {

            @Override
            public void onMusiqueItemClick (View view, Musique musique, int position) {
                mService.setMusiquePlaylist(musiques, position);
                mService.arretSimpleMusique();
                mService.musiqueDemaPause();
            }

            @Override
            public void onMusiqueItemLongClick (View view, Musique musique, int position) {

                    Dialog dialog = new Dialog(getActivity ());

                    // set content view
                    dialog.setContentView(R.layout.dialog_update);

                    // initialize width and height
                    int width = WindowManager.LayoutParams.MATCH_PARENT;
                    int height = WindowManager.LayoutParams.WRAP_CONTENT;
                    //set layout
                    dialog.getWindow().setLayout(width, height);
                    //show dialog
                    dialog.show();



            }


        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager (getActivity().getApplicationContext(), LinearLayout.VERTICAL, false));
        mRecyclerView.setAdapter(monAdapter);

        return root;

    }
}
