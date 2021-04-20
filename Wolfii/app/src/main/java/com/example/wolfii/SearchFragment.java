package com.example.wolfii;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class SearchFragment extends Fragment {
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        fragmentManager = getActivity ().getSupportFragmentManager ();

        Button button = root.findViewById (R.id.bt_search);
        EditText editText = root.findViewById (R.id.musique);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String artiste = editText.getText ().toString ();
                ArrayList<Musique> musiques = rechercher (artiste);
                Fragment fragment = new ListSearchFragment (musiques, artiste);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.result_search, fragment, "search");
                fragmentTransaction.commit ();
            }
        });
        return root;
    }

    public ArrayList<Musique> rechercher(String musique) {
        ArrayList<Musique> musiques = new ArrayList<> ();
        for(Musique m : MainActivity.maMusique) if(m.getName ().contains (musique)) musiques.add(m);
        return musiques;
    }
}