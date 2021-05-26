package com.example.wolfii;

import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class ClickOnRetour implements View.OnClickListener{
    private ArrayList<FragmentsWithReturn> fragments;
    private FragmentTransaction fragmentTransaction;

    public static int index = 0;

    public void setFragments(ArrayList<FragmentsWithReturn> fragments) {
        this.fragments = (ArrayList<FragmentsWithReturn>) fragments.clone ();
    }

    public void setFragmentTransaction(FragmentTransaction fragmentTransaction) {
        this.fragmentTransaction = fragmentTransaction;
    }

    @Override
    public void onClick (View v) {
        fragmentTransaction.replace (R.id.listes, fragments.get(index), null);
    }
}
