package com.example.musique_rv;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class MyMusiqueAdapter extends RecyclerView.Adapter<MyMusiqueAdapter.MyViewHolder> {
    // classe qui est responsable de chaque cellule
    // responsable du recyclage des view
    // view holder = accelerer le rendu de la liste, il sera déclaré au sein de l'adapter
    List<Musique> mesMusiques;
    MyMusiqueAdapter(List<Musique> mesMusiques) {
        this.mesMusiques = mesMusiques;
    }

    public Object getItem(int position) {
        return mesMusiques.get(position);
    }

    public interface MusiqueItemClickListener {
        void onMusiqueItemClick(View view, Musique musique, int position);
        void onMusiqueItemLongClick(View view, Musique musique, int position);
    }
    private MusiqueItemClickListener mMusiqueItemClickListener;

    public void setmMusiqueItemClickListener(MusiqueItemClickListener mMusiqueItemClickListener) {
        this.mMusiqueItemClickListener = mMusiqueItemClickListener;
    }
    @NonNull
    @Override
    public MyMusiqueAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // on cherche notre vue avec inflater
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // on va chercher notre layout
        View view = layoutInflater.inflate(R.layout.musique_item, parent, false);
        // on renvoie le viewholder
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyMusiqueAdapter.MyViewHolder holder, int position) {
        // affiche les viewholder en donnant la position
        holder.display(mesMusiques.get(position));
        Log.d("position", position + "");
    }

    @Override
    public int getItemCount() {
        return mesMusiques.size(); // pour ne pas être embete avec les tailles de liste
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
                    if (mMusiqueItemClickListener != null) {
                        mMusiqueItemClickListener.onMusiqueItemClick(
                                itemView,
                                (Musique) getItem(getAdapterPosition()),
                                getAdapterPosition());
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mMusiqueItemClickListener != null) {
                        mMusiqueItemClickListener.onMusiqueItemLongClick(
                                itemView,
                                (Musique)getItem(getAdapterPosition()),
                                getAdapterPosition());
                    }
                    return false;
                }
            });
        }
        void display(Musique musique) {
            // ne jamais le mettre dans le constructeur
            mName.setText(musique.getName());
        }

    }
}
