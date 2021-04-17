package com.example.wolfii;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyArtisteAdapter extends RecyclerView.Adapter<MyArtisteAdapter.MyViewHolder> {
    // classe qui est responsable de chaque cellule
    // responsable du recyclage des view
    // view holder = accelerer le rendu de la liste, il sera déclaré au sein de l'adapter
    List<String> mesArtistes;


    public void setMesArtistes(List<String> mesArtistes) {
        this.mesArtistes = mesArtistes;
    }

    public MyArtisteAdapter(ArrayList<String> mesArtistes) {
        this.mesArtistes = mesArtistes;
    }

    public Object getItem(int position) {
        return mesArtistes.get(position);
    }

    public interface ArtisteItemClickListener {
        void onArtisteItemClick(View view, String musique, int position);
        void onArtisteItemLongClick(View view, String musique, int position);
    }
    private ArtisteItemClickListener mArtisteItemClickListener;

    public void setmArtisteItemClickListener(ArtisteItemClickListener mArtisteItemClickListener) {
        this.mArtisteItemClickListener = mArtisteItemClickListener;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // on cherche notre vue avec inflater
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // on va chercher notre layout
        View view = layoutInflater.inflate(R.layout.musique_item, parent, false);
        // on renvoie le viewholder
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // affiche les viewholder en donnant la position
        holder.display(mesArtistes.get(position));
        Log.d("position", position + "");
    }

    @Override
    public int getItemCount() {
        return mesArtistes.size(); // pour ne pas être embete avec les tailles de liste
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;

        public MyViewHolder(@NonNull View itemView) {
            // itemview = vue de chaque cellule
            super(itemView);

            // afficher le nom de la musique courante
            mName = (TextView) itemView.findViewById(R.id.name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mArtisteItemClickListener != null) {
                        mArtisteItemClickListener.onArtisteItemClick(
                                itemView,
                                (String) getItem(getAdapterPosition()),
                                getAdapterPosition());
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mArtisteItemClickListener != null) {
                        mArtisteItemClickListener.onArtisteItemLongClick(
                                itemView,
                                (String)getItem(getAdapterPosition()),
                                getAdapterPosition());
                    }
                    return false;
                }
            });
        }
        void display(String artiste) {
            // ne jamais le mettre dans le constructeur
            mName.setText(artiste);
        }

    }
}
