package com.example.wolfii;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyMusiqueAdapter extends RecyclerView.Adapter<MyMusiqueAdapter.MyViewHolder> {
    // classe qui est responsable de chaque cellule
    // responsable du recyclage des view
    // view holder = accelerer le rendu de la liste, il sera déclaré au sein de l'adapter
    List<Musique> mesMusiques;
    public static Context context;

    public List<Musique> getMesMusiques() {
        return mesMusiques;
    }

    public void setMesMusiques(List<Musique> mesMusiques) {
        this.mesMusiques = mesMusiques;
    }

    public MyMusiqueAdapter(List<Musique> mesMusiques, Context context) {
        this.mesMusiques = mesMusiques;
        this.context = context;
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
        holder.display(mesMusiques.get(position));
        Log.d("position", position + "");

        holder.bt_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);

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
    }

    @Override
    public int getItemCount() {
        return mesMusiques.size(); // pour ne pas être embete avec les tailles de liste
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private ImageView bt_settings;
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
            bt_settings = itemView.findViewById(R.id.bt_settings);
        }
        void display(Musique musique) {
            // ne jamais le mettre dans le constructeur
            mName.setText(musique.getName());
        }

    }
}
